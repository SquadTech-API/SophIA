# SophIA — Assistente de Recomendação de Livros

Assistente que entende um pedido em linguagem natural e recomenda livros de um
catálogo próprio, usando **busca semântica (vetorial)** combinada com **filtros
estruturados**. Toda a IA roda **localmente** via Ollama — nada sai para serviços
externos.

O projeto é dividido em dois módulos:

```
SophIA/
├── backend/    → API (Spring Boot 4 + Spring AI + PostgreSQL/pgvector + Ollama)
└── frontend/   → Chat desktop (JavaFX / Scene Builder)
```

---

## 1. Arquitetura de agente e os 6 design patterns

O núcleo do backend é um **agente** organizado como uma máquina de estados que
planeja, executa ferramentas e observa resultados. Cada padrão exigido tem um
lugar claro no código:

| Padrão | Onde está (pacote) | Papel |
|---|---|---|
| **State Machine** | `agent` + `agent.state` | Ciclo `INIT → PLANNING → EXECUTING → OBSERVING → COMPLETED`. `Agent` roda o laço; cada estado é um `StateHandler`. |
| **Command** | `agent.command` | Cada ação é um comando (`extract_filters`, `embed_query`, `search_books`, `generate_answer`). |
| **Strategy** | `agent.planner` | Planners intercambiáveis: `ReActPlanner`, `PlanThenExecutePlanner`, `HumanInTheLoopPlanner`. |
| **Chain of Responsibility** | `agent.advisor` | Corrente de advisors em volta do agente (valida/normaliza a entrada, ajusta a saída). |
| **Observer** | `agent.observer` | `LoggingObserver` e `MetricsObserver` escutam cada transição de estado. |
| **Dependency Injection** | tudo | Via Spring (construtor). Registries recebem `List<...>` de beans automaticamente. |

**Como os padrões se encaixam numa requisição:**

```
Requisição
   │
   ▼
[Chain of Responsibility]  advisors: valida e normaliza a pergunta
   │
   ▼
[State Machine]  INIT → PLANNING → EXECUTING → OBSERVING → ... → COMPLETED
   │                        │            │
   │            [Strategy] planner    [Command] executa a tool
   │            decide o passo        (busca, embedding, etc.)
   │
   └──► [Observer] registra cada transição (logs + métricas)
```

> O fluxo de recomendação executa, em essência: **extrair filtros → gerar
> embedding da pergunta → buscar no catálogo → gerar a resposta**. O que muda
> entre os planners é *como* esses passos são decididos (de uma vez, um a um, ou
> pedindo ajuda ao usuário).

---

## 2. Stack

- **Java 21**, **Spring Boot 4.1.1**, **Spring AI 2.0.0**
- **PostgreSQL 17 + pgvector** (busca por similaridade de cosseno, índice HNSW)
- **Ollama**: `gemma4:e4b` (chat) e `nomic-embed-text` (embeddings, 768 dimensões)
- **JavaFX 21** no front

---

## 3. Como rodar (caminho recomendado: Docker)

> Pré-requisitos: **Docker + Docker Compose** e ~10 GB livres (o modelo
> `gemma4:e4b` tem ~9,6 GB e é baixado no primeiro uso).

### Passo 1 — Subir o backend + banco + IA

```bash
cd backend
docker compose up --build
```

Na primeira vez isso: cria o banco e roda o schema (`init-db/01-init.sql`), sobe o
Ollama, constrói a aplicação e baixa os modelos. Pode demorar alguns minutos.

> **Reiniciar o banco do zero** (se precisar recriar o schema):
> `docker compose down -v && docker compose up --build`
> O `-v` apaga o volume para o `01-init.sql` rodar de novo.

### Passo 2 — Carregar o catálogo (uma vez)

Com o backend no ar, em outro terminal:

```bash
# importa os 60 livros do books.csv
curl -X POST http://localhost:8080/admin/import-csv

# gera os embeddings de cada livro (necessário para a busca semântica)
curl -X POST http://localhost:8080/admin/reindex
```

### Passo 3 — Testar a API

```bash
curl "http://localhost:8080/api/chat?message=quero um livro de fantasia com dragões"

# escolhendo a estratégia:
curl "http://localhost:8080/api/chat?message=algo parecido com Dom Casmurro&planner=react"
```

### Passo 4 — Abrir o chat (front JavaFX)

Em outra aba de terminal (precisa de **JDK 21**):

```bash
cd frontend
mvn javafx:run
```

A janela abre; digite um pedido e escolha a **Estratégia** no canto superior
direito. O front fala com `http://localhost:8080` (ajuste com a variável
`SOPHIA_API_URL` se o backend estiver em outro host).

---

## 4. Rodar o backend fora do Docker (opcional, modo dev)

Útil para rodar a aplicação pela IDE enquanto só o banco e o Ollama ficam no Docker.
A configuração já aponta para `localhost` por padrão, então basta subir a infra e rodar:

```bash
cd backend
docker compose up pgvector ollama   # sobe só a infraestrutura (banco + IA)
mvn spring-boot:run                  # ou rode SophiaApplication direto pela IDE
```

(Depois rode os mesmos `POST /admin/import-csv` e `/admin/reindex`.)

> Sem Docker, você precisa de um PostgreSQL com a extensão **pgvector** e do **Ollama**
> instalados e rodando localmente (portas 5432 e 11434), com o schema criado a partir
> de `init-db/01-init.sql` e os modelos `gemma4:e4b` e `nomic-embed-text` baixados.

---

## 5. Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/chat` | Corpo JSON `{"question": "...", "planner": "..."}`. `planner` é opcional. |
| `GET`  | `/api/chat?message=...&planner=...` | Atalho para testes no navegador/curl. |
| `POST` | `/admin/import-csv` | Importa o catálogo do `books.csv`. |
| `POST` | `/admin/reindex` | Gera/atualiza os embeddings dos livros. |

Resposta do chat:

```json
{
  "answer": "texto da recomendação...",
  "planner": "plan-then-execute",
  "trace": ["extract_filters", "embed_query", "search_books", "generate_answer"]
}
```

O campo `trace` mostra os comandos que o agente executou — útil para enxergar o
raciocínio e demonstrar os padrões funcionando.

---

## 6. Configuração (`application.properties`)

```properties
sophia.planner=plan-then-execute   # estratégia padrão: react | plan-then-execute | human-in-the-loop
sophia.max-iterations=12           # trava anti-loop
sophia.search-limit=5              # nº máximo de livros por busca
sophia.import-limit=-1             # linhas importadas do CSV (-1 = todas)
```

---

## 7. Observações

- O catálogo (`backend/src/main/resources/books.csv`) traz 60 livros reais com
  descrições em português, para a busca semântica funcionar bem em PT.
- O schema é escrito à mão em `init-db/01-init.sql` (o Hibernate não altera o
  banco — `ddl-auto=none`).

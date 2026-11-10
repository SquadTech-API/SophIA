SophIA - Chat JavaFX / Scene Builder

REQUISITOS
- JDK 21
- Maven
- Scene Builder (opcional, para editar o FXML)

EXECUTAR
Na pasta do projeto (frontend), execute:
    mvn javafx:run

O front conecta no backend em http://localhost:8080 por padrao.
Para apontar para outro endereco, defina a variavel de ambiente:
    SOPHIA_API_URL=http://SEU_HOST:8080

IMPORTANTE
- O backend precisa estar rodando ANTES de enviar mensagens.
- No cabecalho ha um seletor de "Estrategia" (planner): Plan-then-Execute,
  ReAct e Human-in-the-Loop. Ele troca, em tempo real, a estrategia que o
  agente usa para raciocinar (demonstra o padrao Strategy).

ABRIR NO SCENE BUILDER
1. Abra o Scene Builder.
2. File > Open > src/main/resources/com/sophia/chat-view.fxml
3. O CSS chat.css esta na mesma pasta e e carregado pelo FXML.

ARQUIVOS
- Main.java              -> ponto de entrada da aplicacao JavaFX
- ChatController.java     -> logica da tela (envia mensagens ao backend)
- SophiaApiClient.java    -> cliente HTTP que fala com a API REST
- chat-view.fxml / chat.css -> interface (editavel no Scene Builder)

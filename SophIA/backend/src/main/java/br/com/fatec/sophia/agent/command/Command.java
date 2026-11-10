package br.com.fatec.sophia.agent.command;

import br.com.fatec.sophia.agent.AgentContext;

/**
 * Padrão <b>Command</b>: encapsula uma ação executável do agente (uma "tool").
 *
 * <p>Cada comando é uma unidade de trabalho isolada — extrair filtros, gerar o
 * embedding, buscar livros, gerar a resposta. Todos expõem a mesma interface,
 * então o executor consegue rodar qualquer um sem saber o que ele faz por dentro.
 * Cada comando lê o que precisa do {@link AgentContext} e escreve o resultado de
 * volta nele.</p>
 */
public interface Command {

    /** Nome lógico do comando, usado pelos planners para montar o plano. */
    String name();

    /** Executa a ação, lendo e escrevendo no contexto compartilhado. */
    void execute(AgentContext ctx);
}

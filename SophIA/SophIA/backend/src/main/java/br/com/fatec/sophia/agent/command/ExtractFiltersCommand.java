package br.com.fatec.sophia.agent.command;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.model.BookFilters;

/**
 * Comando: extrai filtros estruturados da pergunta do usuário usando o modelo
 * de linguagem. Ex.: "quero um livro de fantasia com mais de 4 de nota" vira
 * {@code genreName="fantasia", minRating=4.0}.
 *
 * <p>Lê {@code userQuestion} do contexto e escreve {@code filters}.</p>
 */
@Component
public class ExtractFiltersCommand implements Command {

    private final ChatClient chatClient;

    public ExtractFiltersCommand(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String name() {
        return "extract_filters";
    }

    @Override
    public void execute(AgentContext ctx) {
        BookFilters filters = chatClient.prompt()
                .system("""
                        Extraia os filtros de busca da pergunta do usuário.
                        Responda SOMENTE com um objeto JSON válido, sem nenhum texto ou formatação adicional.
                        O objeto JSON deve ter os seguintes campos:
                        {
                            "title": null,
                            "genreName": null,
                            "authorName": null,
                            "minPages": null,
                            "maxPages": null,
                            "yearPublished": null,
                            "minRating": null
                        }
                        Preencha apenas os campos que a pergunta mencionar explicitamente. Deixe o resto como null.
                        """)
                .user(ctx.getUserQuestion())
                .call()
                .entity(BookFilters.class);

        // Proteção: se o modelo devolver algo inesperado, usamos filtros vazios
        // (busca puramente semântica) em vez de quebrar o fluxo.
        if (filters == null) {
            filters = new BookFilters(null, null, null, null, null, null, null);
        }
        ctx.setFilters(filters);
    }
}

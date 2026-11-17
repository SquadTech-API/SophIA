package br.com.fatec.sophia.agent.command;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.model.Book;

/**
 * Comando: gera a resposta final (recomendação) com base nos livros encontrados.
 *
 * <p>Lê {@code results} e {@code userQuestion} e escreve {@code response}. O
 * modelo é instruído a usar SOMENTE os livros da lista (evita alucinação) e a
 * recomendar os mais próximos do pedido. A resposta é curta de propósito —
 * recomenda no máximo 3 livros — o que também deixa a geração mais rápida.</p>
 */
@Component
public class GenerateAnswerCommand implements Command {

    private final ChatClient chatClient;

    public GenerateAnswerCommand(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String name() {
        return "generate_answer";
    }

    @Override
    public void execute(AgentContext ctx) {
        List<Book> results = ctx.getResults();

        String catalogo = (results == null ? List.<Book>of() : results).stream()
                .map(b -> "- " + b.getTitle() + ": " + resumo(b.getDescription()))
                .collect(Collectors.joining("\n"));

        String response = chatClient.prompt()
                .system("""
                        Você é a SophIA, uma assistente de recomendação de livros. Seja BREVE.
                        Regras:
                        - Responda em português do Brasil.
                        - Recomende no máximo 3 livros da lista, com UMA frase curta cada dizendo por que combina.
                        - Comece direto pela primeira recomendação, SEM introdução e SEM conclusão.
                        - Use SOMENTE os livros da lista; nunca invente livros fora dela.

                        Livros disponíveis:
                        """ + catalogo)
                .user(ctx.getUserQuestion())
                .call()
                .content();

        ctx.setResponse(response);
    }

    /** Encurta a descrição para reduzir o tamanho do prompt (geração mais rápida). */
    private String resumo(String descricao) {
        if (descricao == null) {
            return "";
        }
        String d = descricao.trim();
        if (d.length() <= 180) {
            return d;
        }
        int corte = d.indexOf(". ", 110);
        if (corte > 0 && corte < 180) {
            return d.substring(0, corte + 1);
        }
        return d.substring(0, 180) + "...";
    }
}

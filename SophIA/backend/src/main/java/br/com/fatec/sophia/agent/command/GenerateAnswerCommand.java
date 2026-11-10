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
                .map(b -> "- " + b.getTitle() + ": " + b.getDescription())
                .collect(Collectors.joining("\n"));

        String response = chatClient.prompt()
                .system("""
                        Você é a SophIA, uma assistente de recomendação de livros.
                        Regras:
                        - Responda em português do Brasil, de forma simpática e direta.
                        - Recomende os livros da lista abaixo que mais combinam com o pedido (no máximo 3),
                          com 1 ou 2 frases explicando por que cada um combina.
                        - Use SOMENTE os livros da lista. NUNCA invente livros fora dela.
                        - Se não houver um encaixe perfeito, recomende mesmo assim os mais próximos da lista.
                        - Seja conciso: nada de introduções longas.

                        Livros disponíveis:
                        """ + catalogo)
                .user(ctx.getUserQuestion())
                .call()
                .content();

        ctx.setResponse(response);
    }
}

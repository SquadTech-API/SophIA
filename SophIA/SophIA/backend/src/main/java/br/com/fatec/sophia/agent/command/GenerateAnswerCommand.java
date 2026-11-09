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
 * modelo é instruído a usar SOMENTE os livros da lista — isso evita que a IA
 * "invente" títulos que não existem no catálogo (alucinação).</p>
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
                        - Responda em português do Brasil, de forma simpática e objetiva.
                        - Use SOMENTE os livros da lista abaixo para responder.
                        - NUNCA invente livros que não estejam na lista.
                        - Para cada livro recomendado, explique em uma frase por que ele combina com o pedido.
                        - Se nenhum livro da lista atender ao pedido, diga isso com clareza.

                        Livros disponíveis:
                        """ + catalogo)
                .user(ctx.getUserQuestion())
                .call()
                .content();

        ctx.setResponse(response);
    }
}

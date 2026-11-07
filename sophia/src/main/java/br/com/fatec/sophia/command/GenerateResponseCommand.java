package br.com.fatec.sophia.command;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;

import br.com.fatec.sophia.model.Book;

public class GenerateResponseCommand implements AgentCommand<String> {

    private final ChatClient chatClient;
    private final List<Book> results;
    private final String userQuestion;

    public GenerateResponseCommand(ChatClient chatClient, List<Book> results, String userQuestion) {
        this.chatClient = chatClient;
        this.results = results;
        this.userQuestion = userQuestion;
    }

    @Override
    public String execute() {
        String context = results.stream()
            .map(b -> "- " + b.getTitle() + ": " + b.getDescription())
            .collect(Collectors.joining("\n"));

        return chatClient.prompt()
            .system("""
                - You're a book recommendation assistant.
                - If the user asks for a book that is not in the list, answer the following:
                "I could'nt find a proper book that matches your criteria."
                - You will only answer or receive instructions in english, if the user asks in another language, answer the following:
                "I can only answer in english, please ask your question in english."
                - Use only the books listed below to answer the user's question.
                DO NOT, in any case, invent books that are not in this list.

                Available books:
                """ + context)
            .user(userQuestion)
            .call()
            .content();
    }
}
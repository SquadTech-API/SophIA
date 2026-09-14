package br.com.fatec.sophia.state;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;

@Component
public class ResultState implements RAGState {

    private final ChatClient chatClient;

    public ResultState(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public RAGState execute(RAGContext context) {

        System.out.println("Generating response based on the results...");

        String result = context.getResults().stream()
            .map(b -> "- " + b.getTitle() + ": " + b.getDescription())
            .collect(Collectors.joining("\n"));
        
        String response = chatClient.prompt()
            .system("""
                - You're a book recommendation assistant. 
                - If the user asks for a book that is not in the list, answer the following: 
                "I could'nt find a proper book that matches your criteria."
                - You will only answer or receive instructions in english, if the user asks in another language, answer the following: 
                "I can only answer in english, please ask your question in english."
                - Use only the books listed below to answer the user's question. 
                DO NOT, in any case, invent books that are not in this list.

                Available books:
                """ + result)
            .user(context.getUserQuestion())
            .call()
            .content();
            
        System.out.println("Response generated.");

        context.setResponse(response);
        return null; // Return the next state or null if it's the final state
    }
}

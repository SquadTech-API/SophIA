package br.com.fatec.sophia;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final RAG rag;

    public ChatController(RAG rag) {
        this.rag = rag;
    }

    @GetMapping("/chat")
    public String generate(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return rag.answer(message);
    }
}
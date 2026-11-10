package br.com.fatec.sophia.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.agent.AgentService;
import br.com.fatec.sophia.web.dto.ChatRequest;
import br.com.fatec.sophia.web.dto.ChatResponse;

/**
 * API de chat da SophIA.
 *
 * <ul>
 *   <li>{@code POST /api/chat} – recebe JSON {@link ChatRequest}; usado pelo front JavaFX.</li>
 *   <li>{@code GET  /api/chat?message=...&planner=...} – atalho para testar no navegador/curl.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private final AgentService agentService;

    public ChatController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return toResponse(agentService.chat(request.question(), request.planner()));
    }

    @GetMapping("/chat")
    public ChatResponse chat(@RequestParam("message") String message,
                             @RequestParam(value = "planner", required = false) String planner) {
        return toResponse(agentService.chat(message, planner));
    }

    private ChatResponse toResponse(AgentContext ctx) {
        return new ChatResponse(ctx.getResponse(), ctx.getPlannerName(), ctx.getTrace());
    }
}

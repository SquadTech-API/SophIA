package com.sophia;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Cliente HTTP que conversa com a API REST do backend da SophIA.
 *
 * <p>A URL base pode ser definida pela variável de ambiente
 * {@code SOPHIA_API_URL}; por padrão aponta para {@code http://localhost:8080}.</p>
 */
public class SophiaApiClient {

    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public SophiaApiClient() {
        String env = System.getenv("SOPHIA_API_URL");
        this.baseUrl = (env == null || env.isBlank()) ? "http://localhost:8080" : env.trim();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /** Resposta do backend (espelha o ChatResponse do servidor). */
    public record ChatReply(String answer, String planner, List<String> trace) {
    }

    /**
     * Envia a pergunta ao backend e devolve a resposta.
     *
     * @param question pergunta do usuário
     * @param planner  estratégia (pode ser {@code null} para usar o padrão do servidor)
     */
    public ChatReply send(String question, String planner) throws Exception {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("question", question);
        if (planner != null && !planner.isBlank()) {
            payload.put("planner", planner);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .timeout(Duration.ofMinutes(3)) // modelos locais podem demorar
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = http.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("O servidor respondeu com HTTP " + response.statusCode());
        }

        return mapper.readValue(response.body(), ChatReply.class);
    }
}

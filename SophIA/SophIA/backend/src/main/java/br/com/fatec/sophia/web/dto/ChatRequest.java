package br.com.fatec.sophia.web.dto;

/**
 * Corpo da requisição de chat.
 *
 * @param question pergunta do usuário (obrigatória)
 * @param planner  estratégia desejada: "react", "plan-then-execute" ou
 *                 "human-in-the-loop". Opcional — se nulo, usa o padrão.
 */
public record ChatRequest(String question, String planner) {
}

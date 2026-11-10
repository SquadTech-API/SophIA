package br.com.fatec.sophia.web.dto;

import java.util.List;

/**
 * Resposta do chat.
 *
 * @param answer  texto da recomendação gerado pela SophIA
 * @param planner estratégia efetivamente usada
 * @param trace   sequência de comandos que o agente executou (útil para a UI
 *                mostrar o "raciocínio" e para demonstração dos padrões)
 */
public record ChatResponse(String answer, String planner, List<String> trace) {
}

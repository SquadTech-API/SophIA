package br.com.fatec.sophia.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do {@link ChatClient} do Spring AI.
 *
 * <p>O Spring AI já fornece um {@code ChatClient.Builder} auto-configurado a
 * partir do starter do Ollama (modelo definido em {@code application.properties}).
 * Aqui apenas construímos o {@link ChatClient} padrão e o publicamos como bean,
 * para ser injetado pelos comandos que conversam com o modelo de linguagem.</p>
 */
@Configuration
public class ChatConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}

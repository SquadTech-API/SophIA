package br.com.fatec.sophia.agent.command;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.agent.AgentContext;
import br.com.fatec.sophia.common.VectorFormatter;

/**
 * Comando: gera o vetor de embedding da pergunta do usuário (busca semântica).
 *
 * <p>Lê {@code userQuestion} e escreve {@code queryEmbedding} (o literal do vetor
 * no formato do pgvector) no contexto.</p>
 */
@Component
public class EmbedQueryCommand implements Command {

    private final EmbeddingModel embeddingModel;
    private final VectorFormatter vectorFormatter;

    public EmbedQueryCommand(EmbeddingModel embeddingModel, VectorFormatter vectorFormatter) {
        this.embeddingModel = embeddingModel;
        this.vectorFormatter = vectorFormatter;
    }

    @Override
    public String name() {
        return "embed_query";
    }

    @Override
    public void execute(AgentContext ctx) {
        float[] vector = embeddingModel.embed(ctx.getUserQuestion());
        ctx.setQueryEmbedding(vectorFormatter.toLiteral(vector));
    }
}

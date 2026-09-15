package br.com.fatec.sophia.command;

import org.springframework.ai.embedding.EmbeddingModel;

public class EmbedQueryCommand implements AgentCommand<String> {

    private final EmbeddingModel embeddingModel;
    private final String userQuestion;

    public EmbedQueryCommand(EmbeddingModel embeddingModel, String userQuestion) {
        this.embeddingModel = embeddingModel;
        this.userQuestion = userQuestion;
    }

    @Override
    public String execute() {
        float[] vector = embeddingModel.embed(userQuestion);
        return toVectorLiteral(vector);
    }

    private String toVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        return sb.append("]").toString();
    }
}
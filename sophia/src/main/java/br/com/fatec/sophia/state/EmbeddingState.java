package br.com.fatec.sophia.state;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;

@Component
public class EmbeddingState implements RAGState {

    private final EmbeddingModel embeddingModel;
    private final SearchState searchState;

    public EmbeddingState(EmbeddingModel embeddingModel, SearchState searchState) {
        this.embeddingModel = embeddingModel;
        this.searchState = searchState;
    }

    @Override
    public RAGState execute(RAGContext context) {

        System.out.println("Embedding the word-keys...");
        
        float[] queryVector = embeddingModel.embed(context.getUserQuestion());
        String queryEmbeddingLiteral = toVectorLiteral(queryVector);
        context.setQueryEmbeddingLiteral(queryEmbeddingLiteral);
        System.out.println("Embedded");
        return searchState;
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

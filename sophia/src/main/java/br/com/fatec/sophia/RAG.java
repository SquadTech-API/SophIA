package br.com.fatec.sophia;

import br.com.fatec.sophia.state.ExtractFiltersState;
import br.com.fatec.sophia.state.RAGState;

import org.springframework.stereotype.Service;

@Service
public class RAG {

    private final RAGState initialState;

    public RAG(ExtractFiltersState initialState) {
        this.initialState = initialState;
    }

    public String answer(String userQuestion) {

        RAGContext context = new RAGContext(userQuestion);
        RAGState state = initialState;

        while (state != null) {
            state = state.execute(context);
        }

        return context.getResponse();
    }

}

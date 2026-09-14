package br.com.fatec.sophia.state;

import br.com.fatec.sophia.RAGContext;

public interface RAGState {
    RAGState execute(RAGContext context);
}

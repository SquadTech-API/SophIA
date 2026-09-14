package br.com.fatec.sophia.state;

import org.springframework.stereotype.Component;

import br.com.fatec.sophia.RAGContext;

@Component
public class ErrorState implements RAGState {
    @Override
    public RAGState execute(RAGContext context) {
        return null;
    }
}
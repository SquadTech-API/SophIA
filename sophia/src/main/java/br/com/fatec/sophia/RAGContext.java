package br.com.fatec.sophia;

import java.util.List;

import br.com.fatec.sophia.model.BookFilters;
import br.com.fatec.sophia.model.Book;

public class RAGContext {
    private String userQuestion;
    private BookFilters filters;
    private String queryEmbeddingLiteral;
    private List<Book> results;
    private String response;

    public RAGContext(String userQuestion) {
        this.userQuestion = userQuestion;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public BookFilters getFilters() {
        return filters;
    }

    public void setFilters(BookFilters filters) {
        this.filters = filters;
    }

    public String getQueryEmbeddingLiteral() {
        return queryEmbeddingLiteral;
    }

    public void setQueryEmbeddingLiteral(String queryEmbeddingLiteral) {
        this.queryEmbeddingLiteral = queryEmbeddingLiteral;
    }

    public List<Book> getResults() {
        return results;
    }

    public void setResults(List<Book> results) {
        this.results = results;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

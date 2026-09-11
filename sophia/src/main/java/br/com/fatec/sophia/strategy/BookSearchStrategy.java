package br.com.fatec.sophia.strategy;

import br.com.fatec.sophia.model.Book;
import java.util.List;

public interface BookSearchStrategy {
    List<Book> search(String userPrompt);
}
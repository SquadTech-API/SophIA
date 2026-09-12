package br.com.fatec.sophia;

import br.com.fatec.sophia.model.Author;
import br.com.fatec.sophia.model.Book;
import br.com.fatec.sophia.model.Genre;
import br.com.fatec.sophia.repository.AuthorRepository;
import br.com.fatec.sophia.repository.BookRepository;
import br.com.fatec.sophia.repository.GenreRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class CSVBookImporter {
    // Número de linhas a ser iteradas do arquivo, -1 para tudo.
    private static final int IMPORT_LIMIT = 50;
    // Caminho do arquivo CSV a partir do resources/.
    private static final String CSV_PATH = "books.csv";

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public CSVBookImporter(BookRepository bookRepository, AuthorRepository authorRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public int importFromCsv() throws Exception {
        Map<String, Author> authorCache = new HashMap<>();
        authorRepository.findAll().forEach(a -> authorCache.put(a.getName(), a));

        Map<String, Genre> genreCache = new HashMap<>();
        genreRepository.findAll().forEach(g -> genreCache.put(g.getName(), g));

        Set<String> existingTitles = new HashSet<>();
        bookRepository.findAll().forEach(b -> existingTitles.add(b.getTitle()));

        int imported = 0;

        try (Reader reader = new InputStreamReader(
                new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                if (IMPORT_LIMIT != -1 && imported >= IMPORT_LIMIT) {
                    break;
                }
                
                // -- Dados do livro, alterar conforme o CSV usado
                String title = safeGet(record, "title");
                if (title == null || existingTitles.contains(title)) {
                    continue;
                }

                Book book = new Book();
                book.setTitle(title);
                book.setDescription(safeGet(record, "description"));
                book.setPageCount(parseInt(safeGet(record, "pages")));
                book.setYearPublished(parseInt(safeGet(record, "original_publication_year")));
                book.setRating(parseRating(safeGet(record, "average_rating")));

                Set<Author> authors = new HashSet<>();
                for (String name : parseList(safeGet(record, "authors"))) {
                    authors.add(authorCache.computeIfAbsent(name, n -> authorRepository.save(new Author(n))));
                }
                book.setAuthors(authors);

                Set<Genre> genres = new HashSet<>();
                for (String name : parseList(safeGet(record, "genres"))) {
                    genres.add(genreCache.computeIfAbsent(name, n -> genreRepository.save(new Genre(n))));
                }
                book.setGenres(genres);

                bookRepository.save(book);
                existingTitles.add(title);
                imported++;
            }
        }

        return imported;
    }

    private String safeGet(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return (value == null || value.isBlank()) ? null : value.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        if (value == null) return null;
        try {
            return (int) Double.parseDouble(value); // trata "374.0" como 374
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseRating(String value) {
        if (value == null) return null;
        try {
            return BigDecimal.valueOf(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<String> parseList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) return List.of();
        String cleaned = rawValue.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        List<String> result = new ArrayList<>();
        for (String part : cleaned.split(",")) {
            String name = part.trim().replaceAll("^['\"]|['\"]$", "");
            if (!name.isBlank()) {
                result.add(name);
            }
        }
        return result;
    }
}
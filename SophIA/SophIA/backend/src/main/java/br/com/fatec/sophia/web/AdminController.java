package br.com.fatec.sophia.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.sophia.ingest.BookIndexer;
import br.com.fatec.sophia.ingest.CSVBookImporter;

/**
 * Endpoints administrativos para carga de dados:
 * <ul>
 *   <li>{@code POST /admin/import-csv} – importa o catálogo do {@code books.csv}.</li>
 *   <li>{@code POST /admin/reindex}    – gera/atualiza os embeddings dos livros.</li>
 * </ul>
 * Rode o import primeiro e depois o reindex (os livros precisam existir para
 * serem vetorizados).
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BookIndexer bookIndexer;
    private final CSVBookImporter csvBookImporter;

    public AdminController(BookIndexer bookIndexer, CSVBookImporter csvBookImporter) {
        this.bookIndexer = bookIndexer;
        this.csvBookImporter = csvBookImporter;
    }

    @PostMapping("/import-csv")
    public String importCsv() throws Exception {
        int count = csvBookImporter.importFromCsv();
        return count + " livros importados do CSV.";
    }

    @PostMapping("/reindex")
    public String reindex() {
        int count = bookIndexer.indexAllBooks();
        return count + " livros reindexados.";
    }
}

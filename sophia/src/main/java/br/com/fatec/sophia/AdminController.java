package br.com.fatec.sophia;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final BookIndexer bookIndexer;
    private final CSVBookImporter csvBookImporter;

    public AdminController(BookIndexer bookIndexer, CSVBookImporter csvBookImporter) {
        this.bookIndexer = bookIndexer;
        this.csvBookImporter = csvBookImporter;
    }

    @PostMapping("/admin/import-csv")
    public String importCsv() throws Exception {
        int count = csvBookImporter.importFromCsv();
        return count + " livros importados do CSV.";
    }

    @PostMapping("/admin/reindex")
    public String reindex() {
        int count = bookIndexer.indexAllBooks();
        return count + " livros reindexados.";
    }
}
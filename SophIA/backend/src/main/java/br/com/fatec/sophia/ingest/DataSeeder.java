package br.com.fatec.sophia.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.com.fatec.sophia.config.SophiaProperties;
import br.com.fatec.sophia.repository.BookRepository;

/**
 * Popula o catálogo automaticamente no primeiro boot.
 *
 * <p>Roda depois que a aplicação sobe. Se {@code sophia.auto-seed=true} e o banco
 * estiver vazio, importa o {@code books.csv} e gera os embeddings — assim você não
 * precisa chamar {@code /admin/import-csv} e {@code /admin/reindex} na mão. Se já
 * houver livros, não faz nada. Qualquer falha é apenas logada (não derruba o app):
 * nesse caso dá para rodar os endpoints de admin manualmente.</p>
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CSVBookImporter importer;
    private final BookIndexer indexer;
    private final BookRepository bookRepository;
    private final SophiaProperties properties;

    public DataSeeder(CSVBookImporter importer,
                      BookIndexer indexer,
                      BookRepository bookRepository,
                      SophiaProperties properties) {
        this.importer = importer;
        this.indexer = indexer;
        this.bookRepository = bookRepository;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isAutoSeed()) {
            return;
        }
        try {
            long existentes = bookRepository.count();
            if (existentes > 0) {
                log.info("[seed] catálogo já populado ({} livros) — nada a fazer.", existentes);
                return;
            }
            log.info("[seed] banco vazio: importando catálogo e gerando embeddings...");
            int importados = importer.importFromCsv();
            int indexados = indexer.indexAllBooks();
            log.info("[seed] catálogo pronto: {} livros importados, {} indexados.", importados, indexados);
        } catch (Exception e) {
            log.warn("[seed] não foi possível popular automaticamente ({}). "
                    + "Rode manualmente: POST /admin/import-csv e POST /admin/reindex.", e.getMessage());
        }
    }
}

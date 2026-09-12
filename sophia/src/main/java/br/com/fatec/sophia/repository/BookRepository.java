package br.com.fatec.sophia.repository;

import br.com.fatec.sophia.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = """
        SELECT b2.* FROM (
            SELECT DISTINCT b.* FROM books b
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            LEFT JOIN genres g ON bg.genre_id = g.id
            LEFT JOIN book_authors ba ON b.id = ba.book_id
            LEFT JOIN authors a ON ba.author_id = a.id
            WHERE (:title IS NULL OR b.title ILIKE CONCAT('%', :title, '%'))
              AND (:genreName IS NULL OR g.name ILIKE CONCAT('%', :genreName, '%'))
              AND (:authorName IS NULL OR a.name ILIKE CONCAT('%', :authorName, '%'))
              AND (:minPages IS NULL OR b.page_count >= :minPages)
              AND (:maxPages IS NULL OR b.page_count <= :maxPages)
              AND (:yearPublished IS NULL OR b.year_published = :yearPublished)
              AND (:minRating IS NULL OR b.rating >= :minRating)
        ) b2
        ORDER BY
            CASE WHEN :queryEmbedding IS NULL THEN 0 ELSE b2.embedding <=> CAST(:queryEmbedding AS vector) END ASC,
            b2.rating DESC
        LIMIT :limit
        """, nativeQuery = true)
List<Book> hybridSearch(
    @Param("title") String title,
    @Param("genreName") String genreName,
    @Param("authorName") String authorName,
    @Param("minPages") Integer minPages,
    @Param("maxPages") Integer maxPages,
    @Param("yearPublished") Integer yearPublished,
    @Param("minRating") Double minRating,
    @Param("queryEmbedding") String queryEmbedding,
    @Param("limit") int limit
);

    @Modifying
    @Query(value = "UPDATE books SET embedding = CAST(:embedding AS vector) WHERE id = :id", nativeQuery = true)
    void updateEmbedding(@Param("id") Long id, @Param("embedding") String embedding);

    //Evita duplicidade de livros com o mesmo título, garantindo que cada título seja único no banco de dados.
    boolean existsByTitle(String title);
}
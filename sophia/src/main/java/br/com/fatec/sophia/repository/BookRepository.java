package br.com.fatec.sophia.repository;

import br.com.fatec.sophia.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = """
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
            ORDER BY 
                CASE WHEN :queryEmbedding IS NULL THEN 0 ELSE b.embedding <=> CAST(:queryEmbedding AS vector) END ASC,
                b.rating DESC
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
}
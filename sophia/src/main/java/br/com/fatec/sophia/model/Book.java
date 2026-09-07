package br.com.fatec.sophia.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity 
@Table(name = "books")
public class Book {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", unique = true)
    private Integer bookId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "year_published")
    private Integer yearPublished;

    @Column(precision = 3, scale = 2)
    private Double rating;

    @Column(name = "embedding", columnDefinition = "vector")
    private String embedding;

	@ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

	@ManyToMany
    @JoinTable(
        name = "book_genres",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

	public Book() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Integer getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(Integer yearPublished) {
		this.yearPublished = yearPublished;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getEmbedding() {
		return embedding;
	}

	public void setEmbedding(String embedding) {
		this.embedding = embedding;
	}

	public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }
    
}
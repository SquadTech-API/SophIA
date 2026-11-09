package br.com.fatec.sophia.model;

public record BookFilters(
        String title,
        String genreName,
        String authorName,
        Integer minPages,
        Integer maxPages,
        Integer yearPublished,
        Double minRating
    ) {}
package br.com.fatec.sophia.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fatec.sophia.model.Genre;

@Repository 
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional <Genre> findByName(String name);
}
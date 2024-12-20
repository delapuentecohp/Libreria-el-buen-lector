package com.Literalura.repositorio;

import com.Literalura.modelo.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibrosRepositorio extends JpaRepository<Libros, Long> {

    Optional<Libros> findByTituloIgnoreCase(String titulo);

    @Query(value="SELECT * FROM libros WHERE idioma = :idioma", nativeQuery = true)
    List<Libros> findByIdioma(@Param("idioma") String idioma);
}
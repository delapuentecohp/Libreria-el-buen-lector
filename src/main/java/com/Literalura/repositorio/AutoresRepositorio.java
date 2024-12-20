package com.Literalura.repositorio;

import com.Literalura.modelo.Autores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoresRepositorio extends JpaRepository<Autores, Long> {

    Optional<Autores> findByNombre(String nombre);

    @Query(value = "SELECT * FROM autores WHERE ano_nacimiento <= :ano", nativeQuery = true)
    List<Autores> findAutoresConAnoNacimiento(@Param("ano") int ano);

    @Query(value = "SELECT * FROM autores WHERE ano_fallecimiento <= :ano", nativeQuery = true)
    List<Autores> findAutoresConAnoFallecimiento(@Param("ano") int ano);

    @Query(value = "SELECT autores.id, autores.nombre, ano_nacimiento,ano_fallecimiento, libros.titulo FROM autores INNER JOIN libros ON libros.autor_id = autores.id", nativeQuery = true)
    List<Autores> findAllConLibros();
}
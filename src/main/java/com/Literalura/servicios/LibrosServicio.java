package com.Literalura.servicios;


import com.Literalura.modelo.Libros;
import com.Literalura.repositorio.LibrosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LibrosServicio {

    @Autowired
    private LibrosRepositorio librosRepositorio;

    public List<Libros> listarLibros() {

        return librosRepositorio.findAll();
    }

    public List<Libros> listarLibrosPorIdioma(String idioma) {

        return librosRepositorio.findByIdioma(idioma);
    }

    public Libros crearLibro(Libros libro) {

        return librosRepositorio.save(libro);
    }

    public Optional<Libros> obtenerLibroPorId(Long id) {

        return librosRepositorio.findById(id);
    }

    public Optional<Libros> obtenerLibroPorTitulo(String titulo) {
        return librosRepositorio.findByTituloIgnoreCase(titulo);
    }

    public static List<Libros> obtenerTop5LibrosMasDescargados(List<Libros> libros) {

        libros.sort(Comparator.comparingInt(Libros::getNumeroDescargas).reversed());
        return libros.subList(0, Math.min(libros.size(), 5));
    }

}


package com.Literalura.servicios;


import com.Literalura.modelo.Autores;
import com.Literalura.repositorio.AutoresRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoresServicio {

    @Autowired
    private AutoresRepositorio autoresRepositorio;
    public List<Autores> listarAutores() {
        return autoresRepositorio.findAllConLibros();
    }

    public List<Autores> listarAutoresAnoNacimiento(int ano) {
        return autoresRepositorio.findAutoresConAnoNacimiento(ano);
    }

    public List<Autores> listarAutoresAnoFallecimiento(int ano) {
        return autoresRepositorio.findAutoresConAnoFallecimiento(ano);
    }

    public Autores crearAutor(Autores autores) {
        return autoresRepositorio.save(autores);
    }

    public Optional<Autores> obtenerAutoresPorId(Long id) {
        return autoresRepositorio.findById(id);
    }

    public Optional<Autores> obtenerAutoresPorNombre(String nombre) {
        return autoresRepositorio.findByNombre(nombre);
    }
}

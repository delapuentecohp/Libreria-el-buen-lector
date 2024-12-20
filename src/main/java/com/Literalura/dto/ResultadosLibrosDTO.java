package com.Literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadosLibrosDTO {

    @JsonProperty("results")
    private List<LibrosDTO> libros;

    public List<LibrosDTO> getLibros() {
        return libros;
    }

    public void setLibros(List<LibrosDTO> libros) {
        this.libros = libros;
    }
}

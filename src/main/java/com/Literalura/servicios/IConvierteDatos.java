package com.Literalura.servicios;

public interface IConvierteDatos {

    <T> T obtenerDatos(String Json, Class<T> clase);
}

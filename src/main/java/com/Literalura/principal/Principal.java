package com.Literalura.principal;

import com.Literalura.dto.AutoresDTO;
import com.Literalura.dto.LibrosDTO;
import com.Literalura.dto.ResultadosLibrosDTO;
import com.Literalura.modelo.Autores;
import com.Literalura.modelo.Libros;
import com.Literalura.repositorio.LibrosRepositorio;
import com.Literalura.servicios.AutoresServicio;
import com.Literalura.servicios.ConvierteDatos;
import com.Literalura.servicios.LibrosServicio;
import com.Literalura.servicios.ObtenerAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    @Autowired
    private LibrosServicio librosServicio;

    @Autowired
    private LibrosRepositorio librosRepositorio;

    @Autowired
    private AutoresServicio autoresServicio;

    @Autowired
    private ObtenerAPI obtenerAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    private static final String BASE_URL = "https://gutendex.com/books/";

    public void Menu() {
        Scanner teclado = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\t----------------------------------------------------------");
            System.out.println("\t|"+Variables.fGris+"         Bienvenidos a Libreria El buen Lector          "+Variables.b+"|");
            System.out.println("\t----------------------------------------------------------");
            System.out.println("\t|  1.- Buscar Libro por Título                           |");
            System.out.println("\t|  2.- Mostrar Libros Registrados                        |");
            System.out.println("\t|  3.- Mostrar Autores Registrados                       |");
            System.out.println("\t|  4.- Mostrar Libros Registrados por Idioma             |");
            System.out.println("\t|  5.- Mostrar Autores Registrados por año Nacimiento    |");
            System.out.println("\t|  6.- Mostrar Autores Registrados por año Fallecimiento |");
            System.out.println("\t|  7.- Mostrar Top 5 libros más descargados              |");
            System.out.println("\t|  0.- Salir                                             |");
            System.out.println("\t----------------------------------------------------------");
            System.out.print("\t Ingrese una Opcion: ");

            while (!teclado.hasNextInt()) {
                System.out.print("     Por favor, ingresa un número válido: ");
                teclado.nextLine();
            }

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("     Ingrese el título del libro: ");
                    String titulo = teclado.nextLine();
                    try {
                        String api = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
                        String json = obtenerAPI.obtenerAPI(BASE_URL + "?search=" + api);
                        ResultadosLibrosDTO resultadosLibrosDTO = convierteDatos.obtenerDatos(json, ResultadosLibrosDTO.class);
                        List<LibrosDTO> librosDTO = resultadosLibrosDTO.getLibros();
                        String titulo1 = librosDTO.get(0).getTitulo();
                        if (!librosDTO.isEmpty()){
                            for (LibrosDTO libroDTO : librosDTO) {
                                if (libroDTO.getTitulo().equalsIgnoreCase(titulo1)) {
                                    Optional<Libros> libroExistente = librosServicio.obtenerLibroPorTitulo(titulo1);
                                    if (libroExistente.isPresent()) {
                                        System.out.println(Variables.rojo+"     El libro " + titulo1 + " ya existe en la base de datos");
                                        System.out.println("     No se puede registrar el mismo libro más de una vez"+Variables.b);
                                        break;
                                    } else {
                                        Libros libros = new Libros();
                                        libros.setTitulo(librosDTO.get(0).getTitulo());
                                        libros.setIdioma(librosDTO.get(0).getIdiomas().get(0));
                                        libros.setNumeroDescargas(librosDTO.get(0).getNumeroDescargas());
                                        AutoresDTO primerAutorDTO = librosDTO.get(0).getAutores().get(0);
                                        Autores autores = autoresServicio.obtenerAutoresPorNombre(primerAutorDTO.getNombre())
                                                .orElseGet(() -> {
                                                    Autores nuevoAutor = new Autores();
                                                    nuevoAutor.setNombre(primerAutorDTO.getNombre());
                                                    nuevoAutor.setAnoNacimiento(primerAutorDTO.getAnoNacimiento());
                                                    nuevoAutor.setAnoFallecimiento(primerAutorDTO.getAnoFallecimiento());
                                                    return autoresServicio.crearAutor(nuevoAutor);
                                                });
                                        libros.setAutores(autores);
                                        librosServicio.crearLibro(libros);
                                        mostrarDetallesLibro(librosDTO);
                                        System.out.println("     " + Variables.rojo + "Libro encontrado en la API y guardado en la base de datos con exito " + Variables.b);
                                        break;
                                    }
                                }
                            }
                        }else{
                            System.out.println("     "+Variables.rojo+"Libro no encontrado en la API"+Variables.b);
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener datos de la API: " + e.getMessage());
                    }
                    break;

                case 2:
                    librosServicio.listarLibros().forEach(libros -> {
                        System.out.println("     ---------LIBRO-----------");
                        System.out.println("     Título: " + libros.getTitulo());
                        System.out.println("     Autor: " + (libros.getAutores() != null ? libros.getAutores().getNombre() : "Desconocido"));
                        System.out.println("     Idioma: " + mostrarIdioma(libros.getIdioma()));
                        System.out.println("     Número de descargas: " + libros.getNumeroDescargas());
                        System.out.println("     -------------------------");
                    });
                    break;

                case 3:
                    final Long[] x = {0L};
                    autoresServicio.listarAutores().forEach(autores -> {
                        if (x[0] != autores.getId()) {
                            System.out.println("     ----------AUTOR----------");
                            System.out.println("     Autor: " + autores.getNombre());
                            System.out.println("     Fecha de nacimiento: " + autores.getAnoNacimiento());
                            System.out.println("     Fecha de fallecimiento: " + (autores.getAnoFallecimiento() != null ? autores.getAnoFallecimiento() : "Desconocido"));
                            String libros = autores.getLibros().stream()
                                    .map(Libros::getTitulo)
                                    .collect(Collectors.joining(", "));
                            System.out.println("     Libros: [ " + libros + " ]");
                            System.out.println("     -------------------------");
                            x[0] = autores.getId();
                        }
                    });
                    break;

                case 4:
                    System.out.print("     Ingrese el idioma [español(es), inglés(en), francés(fr), portugues(pt)]: ");
                    String idioma = teclado.nextLine();
                    if (idioma.equalsIgnoreCase("ESPAÑOL")){idioma="es";}
                    if (idioma.equalsIgnoreCase("INGLES") ||idioma.equalsIgnoreCase("INGLÉS")){idioma="en";}
                    if (idioma.equalsIgnoreCase("FRANCES") || idioma.equalsIgnoreCase("FRANCÉS")){idioma="fr";}
                    if (idioma.equalsIgnoreCase("PORTUGUES")){idioma="pt";}
                    if ("es".equalsIgnoreCase(idioma) || "en".equalsIgnoreCase(idioma) || "fr".equalsIgnoreCase(idioma) || "pt".equalsIgnoreCase(idioma)) {
                        System.out.println("\t");
                        System.out.println("\t    "+Variables.fGris+"Libros Encontrados"+Variables.b);
                        System.out.println("\t");
                        librosServicio.listarLibrosPorIdioma(idioma).forEach(libro -> {
                            System.out.println("     ---------LIBRO-----------");
                            System.out.println("     Título: " + libro.getTitulo());
                            System.out.println("     Autor: " + (libro.getAutores() != null ? libro.getAutores().getNombre() : "Desconocido"));
                            System.out.println("     Idioma: " + mostrarIdioma(libro.getIdioma()));
                            System.out.println("     Número de descargas: " + libro.getNumeroDescargas());
                            System.out.println("     -------------------------");
                        });

                    } else {
                        System.out.println("     "+Variables.rojo+"Idioma no válido. Intente de nuevo."+Variables.b);
                    }
                    break;

                case 5:
                    System.out.print("     Ingrese el año : ");
                    int ano = teclado.nextInt();
                    teclado.nextLine();
                    List<Autores> autoresVivos = autoresServicio.listarAutoresAnoNacimiento(ano);
                    if (autoresVivos.isEmpty()) {
                        System.out.println("\t");
                        System.out.println("     "+Variables.rojo+"No se encontraron autores."+Variables.b);
                    } else {
                        System.out.println("     -------AUTORES-------");
                        autoresVivos.forEach(autor -> {
                            System.out.println("     Nombre: "+autor.getNombre());
                            System.out.println("     Año de Nacimiento: " + autor.getAnoNacimiento());
                            System.out.println("     ---------------------");
                        });
                    }
                    break;

                case 6:
                    System.out.print("     Ingrese el año : ");
                    int anoF = teclado.nextInt();
                    teclado.nextLine();
                    List<Autores> autoresMuertos = autoresServicio.listarAutoresAnoFallecimiento(anoF);
                    if (autoresMuertos.isEmpty()) {
                        System.out.println("\t");
                        System.out.println("     "+Variables.rojo+"No se encontraron autores."+Variables.b);
                    } else {
                        System.out.println("     -------AUTORES-------");
                        autoresMuertos.forEach(autor -> {
                            System.out.println("     Nombre: "+autor.getNombre());
                            System.out.println("     Año de Fallecimiento: " + autor.getAnoFallecimiento());
                            System.out.println("     ---------------------");
                        });
                    }
                    break;

                case 7:
                    List<Libros> libros = librosRepositorio.findAll();
                    List<Libros> top5Libros = LibrosServicio.obtenerTop5LibrosMasDescargados(libros);
                    System.out.println("\n      "+Variables.fGris+"Top 5 mas descargados"+Variables.b);
                    System.out.println("   ----------------------------");
                    System.out.println("   N° Descargas        Titulos");
                    System.out.println("   ----------------------------");
                    top5Libros.forEach(libro -> System.out.println("     "+libro.getNumeroDescargas() + "     " + libro.getTitulo().toUpperCase()));
                    System.out.println("   ----------------------------");
                    System.out.println("");
                    break;

                case 0:
                    System.out.println("     Saliendo...");
                    break;

                default:
                    System.out.println("     Opción no válida. Intente de nuevo.");

            }
        } while (opcion != 0);
        teclado.close();
    }

    private void mostrarDetallesLibro(List<LibrosDTO> librosDTO) {
        System.out.println("\t");
        System.out.println("\t     ---------LIBRO-----------");
        System.out.println("\t     Título: " + librosDTO.get(0).getTitulo());
        System.out.println("\t     Autor: " + (librosDTO.get(0).getAutores().isEmpty() ? "Desconocido" : librosDTO.get(0).getAutores().get(0).getNombre()));
        System.out.println("\t     Idioma: " + mostrarIdioma(librosDTO.get(0).getIdiomas().get(0)));
        System.out.println("\t     Número de descargas: " + librosDTO.get(0).getNumeroDescargas());
        System.out.println("\t     -------------------------");
    }

    static String mostrarIdioma(String idioma){
        if (Objects.equals(idioma, "es")){idioma = "Español";}
        if (Objects.equals(idioma, "en")){idioma = "Inglés";}
        if (Objects.equals(idioma, "fr")){idioma = "Francés";}
        if (Objects.equals(idioma, "pt")){idioma = "Portugues";}
        return idioma;
    }
}

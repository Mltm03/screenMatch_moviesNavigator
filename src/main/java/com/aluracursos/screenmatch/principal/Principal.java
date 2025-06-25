package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private String apikey= System.getenv("APIKEY_OMDB");
    private final String API_KEY = "&&apikey="+apikey;
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries=new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;

    public Principal(SerieRepository repository) {
        this.repositorio=repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por título   
                    5 - Listar top 5   
                    6 - Buscar serie por categoria   
                    7 - Series con mas de 3 temporadas
                    8 - Series con evaluación mayor a 7.8     
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                case 5:
                    buscarTop5();
                    break;
                case 6:
                    buscarPorCategoria();
                    break;
                case 7:
                    seriesConMasTemporadas();
                    break;
                case 8:
                    seriesConMasEv();
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual deseas ver los episodios: ");
        var nombreSerie= teclado.nextLine();

        Optional<Serie> serie= series.stream()
                .filter(s->s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent())
        {
            var serieEncontrada=serie.get();


            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios=temporadas.stream()
                    .flatMap(d->d.episodios().stream()
                            .map(e -> new Episodio(d.numero(),e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }

    }


    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie=new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos); antes los guardabamos directo en una lista solo para practicidad
    }

    private void mostrarSeriesBuscadas(){
       /** List<Serie> series=new ArrayList<>();
        series=datosSeries.stream()
                .map(d->new Serie(d))
                .toList();  **/
        series=repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("¿Cúal es el título de la serie?");
        var nombreSerie=teclado.nextLine();

        Optional<Serie> seriesBuscadas=repositorio.findByTituloContainsIgnoreCase(nombreSerie);
        if(seriesBuscadas.isPresent()){
            System.out.println("Serie encontrada: "+seriesBuscadas.get());
        }else{
            System.out.println("No se encontrarón coincidencias");
        }

    }


    private void buscarTop5() {

        List<Serie> top5=repositorio.findTop5ByOrderByEvaluacionDesc();
        System.out.println("Las mejores series son: ");

        top5.forEach(t-> System.out.println("Serie: "+t.getTitulo()+", "+"Evaluación: "+ t.getEvaluacion()));

    }

    private  void buscarPorCategoria(){
        System.out.println("Escribe el genero: ");
        var gen=teclado.nextLine();
        var categoria= Categoria.fromEspanol(gen);

        List<Serie> seriesEncontradas=repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria "+ gen+" son: ");
        seriesEncontradas.forEach(s-> System.out.println("Serie: "+ s.getTitulo()));
    }

    private void seriesConMasTemporadas(){
        List<Serie> series=repositorio.findByTotalTemporadasGreaterThan(8);
        System.out.println("Series con mas de 3 temporadas: ");
        series.forEach(s-> System.out.println(s.getTitulo()));
    }

    private void seriesConMasEv(){
        List<Serie> series=repositorio.findByevaluacionGreaterThan(7.8);
        System.out.println("Series con mas de 7.8 de evaluación: ");
        series.forEach(s-> System.out.println(s.getTitulo()+", evaluación: "+s.getEvaluacion()));
    }


}


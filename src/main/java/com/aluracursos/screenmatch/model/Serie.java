package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Optional;
import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double evaluacion;
    private String poster;
    private Categoria genero;
    private String actores;
    private String sinopsis;


    public Serie (DatosSerie datosSerie){
        this.titulo=datosSerie.titulo();
        this.totalTemporadas= datosSerie.totalTemporadas();
        this.evaluacion= OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0);
        this.poster= datosSerie.poster();
        this.genero= Categoria.fromString(datosSerie.genero().split(",")[0].trim());
        this.actores= datosSerie.actores();
        this.sinopsis=datosSerie.sinopsis();

        
    }

    @Override
    public String toString() {
        return
                "genero=" + genero + '\''+
                "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion='" + evaluacion + '\'' +
                ", poster='" + poster + '\'' +
                ", actores='" + actores + '\'' +
                ", sinopsis='" + sinopsis + '\'';
                
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Serie(String titulo, Integer totalTemporadas, double evaluacion, String poster, String sinopsis, String actores, Categoria genero) {
        this.titulo = titulo;
        this.totalTemporadas = totalTemporadas;
        this.evaluacion = evaluacion;
        this.poster = poster;
        this.sinopsis = sinopsis;
        this.actores = actores;
        this.genero = genero;
    }
}

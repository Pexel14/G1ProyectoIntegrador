package dam.pmdm.a101pipas.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Desafio implements Serializable {
    private String titulo, ciudad, descripcion, etiquetas, id, experiencias;
    private int porcentajeProgreso;
//    private Map<String, Experiencia> experiencias;
    private Long id2;

    public Desafio() {}

    // Constructor para el Perfil
    public Desafio(String titulo, int porcentajeProgreso) {
        this.titulo = titulo;
        this.porcentajeProgreso = porcentajeProgreso;
    }

    // Constructor para Crear Desafio
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
    }

    //Constructor para cargar los desafios en INICIO
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, String id, String si) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
        this.id = id;
    }


    // Constructor para Crear Experiencias
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, String experiencias) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
        this.experiencias = experiencias;
    }



    public String getExperiencias() {
        return experiencias;
    }
    //Constructor para cargar los desafios en DESCUBRIR
    public Desafio(String titulo, String ciudad, String id) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.id = id;
    }

    public void setExperiencias(String experiencias) {
        this.experiencias = experiencias;
    }

    public Desafio(String titulo, String experiencias) {
        this.titulo = titulo;
        this.experiencias = experiencias;
    }

    // Constructor para Crear Experiencias
//    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, Map<String, Experiencia> experiencias) {
//        this.titulo = titulo;
//        this.ciudad = ciudad;
//        this.descripcion = descripcion;
//        this.etiquetas = etiquetas;
//        this.experiencias = experiencias;
//    }


    public String getCiudad() {
        return ciudad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEtiquetas() {
        return etiquetas;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getPorcentajeProgreso() {
        return porcentajeProgreso;
    }

    public String getId() {
        return id;
    }

    public List<String> getExperienciasRequeridas() {
        if (experiencias == null || experiencias.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(experiencias.split(","));
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id) {
        this.id2 = id;
    }
}

package dam.pmdm.a101pipas.models;

import java.io.Serializable;
import java.util.Map;

public class Desafio implements Serializable {
    private String titulo, ciudad, descripcion, etiquetas, experiencias;
    private int porcentajeProgreso, id;

    // Constructor para el Perfil
    public Desafio(String titulo, int porcentajeProgreso) {
        this.titulo = titulo;
        this.porcentajeProgreso = porcentajeProgreso;
    }

    // Constructor para Crear Desafio
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, int id) {
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

    public void setExperiencias(String experiencias) {
        this.experiencias = experiencias;
    }

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

}

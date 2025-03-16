package dam.pmdm.a101pipas.models;

import java.io.Serializable;
import java.util.Map;

public class Desafio implements Serializable {
    private String titulo, ciudad, descripcion, etiquetas, id;
    private int porcentajeProgreso;
    private Map<String, Experiencia> experiencias;

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
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, String id) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
        this.id = id;
    }

    //Constructor para cargar los desafios en DESCUBRIR
    public Desafio(String titulo, String ciudad, String id) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.id = id;
    }

    // Constructor para Crear Experiencias
    public Desafio(String titulo, String ciudad, String descripcion, String etiquetas, Map<String, Experiencia> experiencias) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
        this.experiencias = experiencias;
    }

    public Map<String, Experiencia> getExperiencias() {
        return experiencias;
    }

    public void setExperiencias(Map<String, Experiencia> experiencias) {
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

    public String getId() {
        return id;
    }
}

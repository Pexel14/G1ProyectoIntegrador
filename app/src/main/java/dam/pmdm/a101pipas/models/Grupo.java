package dam.pmdm.a101pipas.models;

import java.util.ArrayList;

public class Grupo {

    private int id;
    private String titulo;
    private String contrasena;
    private int creador;
    private int desafio;
    private String fechaCreacion;
    private ArrayList<Integer> miembros;

    public Grupo() {}

    public Grupo(String titulo, String contrasena) {
        this.titulo = titulo;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getCreador() {
        return creador;
    }

    public void setCreador(int creador) {
        this.creador = creador;
    }

    public int getDesafio() {
        return desafio;
    }

    public void setDesafio(int desafio) {
        this.desafio = desafio;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public ArrayList<Integer> getMiembros() {
        return miembros;
    }

    public void setMiembros(ArrayList<Integer> miembros) {
        this.miembros = miembros;
    }
}

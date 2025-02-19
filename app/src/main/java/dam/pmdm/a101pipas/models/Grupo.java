package dam.pmdm.a101pipas.models;

public class Grupo {

    private int id;
    private String titulo;
    private String contrasena;
    private int creador;
    private int desafio;
    private String fechaCreacion;
    private String miembros;

    public Grupo() {}

    public Grupo(String titulo, String contrasena) {
        this.titulo = titulo;
        this.contrasena = contrasena;
    }

    public Grupo(String titulo, String miembros){
        this.titulo = titulo;
        this.miembros = miembros;
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

    public String getMiembros() {
        return miembros;
    }

    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }
}

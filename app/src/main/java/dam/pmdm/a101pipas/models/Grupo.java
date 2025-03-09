package dam.pmdm.a101pipas.models;

public class Grupo {

    private int creador;
    private int desafio;
    private String fecha_creacion;
    private String foto_grupo;
    private int id;
    private String miembros;
    private String titulo;
    private String contrasena;

    public Grupo() {}

    public Grupo(int creador, int desafio, String fecha_creacion, String foto_grupo, int id, String miembros, String titulo) {
        this.creador = creador;
        this.desafio = desafio;
        this.fecha_creacion = fecha_creacion;
        this.foto_grupo = foto_grupo;
        this.id = id;
        this.miembros = miembros;
        this.titulo = titulo;
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

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getMiembros() {
        return miembros;
    }

    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }
}

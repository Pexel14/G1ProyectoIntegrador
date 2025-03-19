package dam.pmdm.a101pipas.models;

public class DesafioPerfil {

    private String titulo;
    private String estado;
    private int progreso;

    public DesafioPerfil(String titulo, String estado, int progreso) {
        this.titulo = titulo;
        this.estado = estado;
        this.progreso = progreso;
    }

    public DesafioPerfil() {
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEstado() {
        return estado;
    }

    public int getProgreso() {
        return progreso;
    }
}

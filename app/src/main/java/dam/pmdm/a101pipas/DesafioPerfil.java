package dam.pmdm.a101pipas;

public class DesafioPerfil {
    private String titulo;
    private int porcentajeProgreso;

    public DesafioPerfil(String titulo, int porcentajeProgreso) {
        this.titulo = titulo;
        this.porcentajeProgreso = porcentajeProgreso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPorcentajeProgreso() {
        return porcentajeProgreso;
    }

    public void setPorcentajeProgreso(int porcentajeProgreso) {
        this.porcentajeProgreso = porcentajeProgreso;
    }
}

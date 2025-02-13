package dam.pmdm.a101pipas.models;

public class Experiencia {
    private String titulo, descripcion, link, mapa, imgExperiencia;
    private boolean completada;
    private double latitud, longitud;  // Añadir las coordenadas

    public Experiencia() {
    }

    public Experiencia(String titulo, String descripcion, String link, String mapa, String imgExperiencia, boolean completada, double latitud, double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.link = link;
        this.mapa = mapa;
        this.imgExperiencia = imgExperiencia;
        this.completada = completada;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMapa() {
        return mapa;
    }

    public void setMapa(String mapa) {
        this.mapa = mapa;
    }

    public String getImgExperiencia() {
        return imgExperiencia;
    }

    public void setImgExperiencia(String imgExperiencia) {
        this.imgExperiencia = imgExperiencia;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}

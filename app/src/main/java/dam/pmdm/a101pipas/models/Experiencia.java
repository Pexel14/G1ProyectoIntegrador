package dam.pmdm.a101pipas.models;

import com.google.android.gms.maps.model.LatLng;

public class Experiencia {
    private String titulo, descripcion, link, mapa, imgExperiencia, coordenadas;
    private boolean completada; // Añadir las coordenadas

    public Experiencia() {
    }

    public Experiencia(String titulo, String coordenadas) {
        this.titulo = titulo;
        this.coordenadas = coordenadas;
    }

    public Experiencia(String titulo, String descripcion){
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Experiencia(String titulo, String descripcion, String link, String mapa, String imgExperiencia, boolean completada, String coordenadas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.link = link;
        this.mapa = mapa;
        this.imgExperiencia = imgExperiencia;
        this.completada = completada;
        this.coordenadas = coordenadas;
    }

    public LatLng getLatLng() {
        if (coordenadas != null && coordenadas.contains(",")) {
            String[] parts = coordenadas.split(",");
            try {
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                return new LatLng(lat, lng);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
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

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }
}

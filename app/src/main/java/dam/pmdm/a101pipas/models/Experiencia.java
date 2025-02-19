package dam.pmdm.a101pipas.models;

import com.google.android.gms.maps.model.LatLng;

public class Experiencia {
    private String titulo, descripcion, imagen, coordenadas;

    public Experiencia() {
    }

    public Experiencia(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Experiencia(String titulo, String descripcion, String imagen, String coordenadas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }
}

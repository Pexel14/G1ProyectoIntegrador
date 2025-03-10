package dam.pmdm.a101pipas.models;

import com.google.android.gms.maps.model.LatLng;

public class Experiencia {
    private String titulo, descripcion, imagen, coordenadas, direccion;
    private String id; // Lo pongo para evitar errores en ExperienciasListAdapter

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo != null) {
            this.titulo = titulo;
        } else {
            this.titulo = "Sin título";
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion != null) {
            this.descripcion = descripcion;
        } else {
            this.descripcion = "Sin descripción";
        }
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        if (imagen != null) {
            this.imagen = imagen;
        } else {
            this.imagen = ""; // Imagen vacía por defecto
        }
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        if (coordenadas != null) {
            this.coordenadas = coordenadas;
        } else {
            this.coordenadas = "0,0"; // Valor por defecto
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return new LatLng(0, 0); // Coordenadas por defecto si hay error
    }

    @Override
    public String toString() {
        return "Experiencia{" +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", imagen='" + imagen + '\'' +
                ", coordenadas='" + coordenadas + '\'' +
                '}';
    }
}

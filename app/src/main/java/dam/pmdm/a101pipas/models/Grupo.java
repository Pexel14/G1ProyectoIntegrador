package dam.pmdm.a101pipas.models;

public class Grupo {

    private String nombre, miembros, contrasenia;

    public Grupo(String nombre, String miembros) {
        this.nombre = nombre;
        this.miembros = miembros;
    }

    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMiembros() {
        return miembros;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}

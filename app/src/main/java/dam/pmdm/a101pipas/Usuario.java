package dam.pmdm.a101pipas;

public class Usuario {
    private int expCompletadas;
    private String nombre;

    public Usuario(int expCompletadas, String nombre) {
        this.expCompletadas = expCompletadas;
        this.nombre = nombre;
    }

    public int getExpCompletadas() {
        return expCompletadas;
    }

    public String getNombre() {
        return nombre;
    }
}

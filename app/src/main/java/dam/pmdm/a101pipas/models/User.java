package dam.pmdm.a101pipas.models;

public class User {
    private String id;
    private String username;
    private String email;
    private String contrasenia;
    private int experiencias_completadas;

    public User(String id, String username, String email, String contrasenia) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public User(String id, String username, String email, int experiencias_completadas) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.experiencias_completadas = experiencias_completadas;
    }

    public User(int expCompletadas, String username) {
        this.experiencias_completadas = expCompletadas;
        this.username = username;
    }

    public User() {
    }

    public String getid() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getcontrasenia() {
        return contrasenia;
    }

    public int getExpCompletadas() {
        return experiencias_completadas;
    }
}

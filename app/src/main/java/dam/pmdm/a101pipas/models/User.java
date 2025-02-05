package dam.pmdm.a101pipas.models;

public class User {
    private String id;
    private String username;
    private String email;
    private String contrasenia;
    private int expCompletadas;

    public User(String id, String username, String email, String contrasenia) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public User(int expCompletadas, String username) {
        this.expCompletadas = expCompletadas;
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
        return expCompletadas;
    }
}

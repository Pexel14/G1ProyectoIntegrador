package dam.pmdm.a101pipas;

public class User {
    private String id;
    private String username;
    private String email;
    private String contrasenia;


    public User(String id, String username, String email, String contrasenia) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contrasenia = contrasenia;
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
}

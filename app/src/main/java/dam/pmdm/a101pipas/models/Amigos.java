package dam.pmdm.a101pipas.models;

public class Amigos {
    private String userId;
    private String username;
    private String fotoPerfil;

    public Amigos() {
        // Constructor vacío necesario para Firebase
    }

    public Amigos(String userId, String username, String fotoPerfil) {
        this.userId = userId;
        this.username = username;
        this.fotoPerfil = fotoPerfil;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}

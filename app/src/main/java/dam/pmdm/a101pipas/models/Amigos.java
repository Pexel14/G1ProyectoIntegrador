package dam.pmdm.a101pipas.models;

public class Amigos {
    private String username;
    private String fotoPerfil;
    private String id;

    public Amigos() {

    }

    public Amigos(String id, String username, String fotoPerfil) {
        this.id = id;
        this.username = username;
        this.fotoPerfil = fotoPerfil;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getId() {
        return id;
    }
}


package dam.pmdm.a101pipas.models;

public class User {
    private String id;
    private String username;
    private String email;
    private String contrasenia;
    private String foto_perfil;
    private String amigos;
    private String desafios;
    private String grupos;
    private int experiencias_completadas;

    public User(String id, String username, String email, String contrasenia, String foto_perfil, String grupos, String amigos, String desafios, int experiencias_completadas) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contrasenia = contrasenia;
        this.foto_perfil = foto_perfil;
        this.grupos = grupos;
        this.amigos = amigos;
        this.desafios = desafios;
        this.experiencias_completadas = experiencias_completadas;
    }

    public User(String id, String username, String email, int experiencias_completadas, String foto_perfil, String amigos, String desafios, String grupos) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.foto_perfil = foto_perfil;
        this.grupos = grupos;
        this.amigos = amigos;
        this.desafios = desafios;
        this.experiencias_completadas = experiencias_completadas;
    }

    public User(int experiencias_completadas, String username) {
        this.experiencias_completadas = experiencias_completadas;
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

    public String getAmigos() {
        return amigos;
    }

    public String getDesafios() {
        return desafios;
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public String getGrupos() {
        return grupos;
    }

    public void setGrupos(String grupos) {
        this.grupos = grupos;
    }

    public int getExperiencias_completadas() {
        return experiencias_completadas;
    }
}

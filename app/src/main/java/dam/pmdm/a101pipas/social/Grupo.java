package dam.pmdm.a101pipas.social;

public class Grupo {
//    private String idGrupo;
//    private String nombreGrupo;
//    private String fotoGrupo;
//
//    public Grupo() {
//
//    }
//
//    public Grupo(String idGrupo, String nombreGrupo, String fotoGrupo) {
//        this.idGrupo = idGrupo;
//        this.nombreGrupo = nombreGrupo;
//        this.fotoGrupo = fotoGrupo;
//    }
//
//    public String getIdGrupo() {
//        return idGrupo;
//    }
//
//    public void setIdGrupo(String idGrupo) {
//        this.idGrupo = idGrupo;
//    }
//
//    public String getNombreGrupo() {
//        return nombreGrupo;
//    }
//
//    public void setNombreGrupo(String nombreGrupo) {
//        this.nombreGrupo = nombreGrupo;
//    }
//
//    public String getFotoGrupo() {
//        return fotoGrupo;
//    }
//
//    public void setFotoGrupo(String fotoGrupo) {
//        this.fotoGrupo = fotoGrupo;
//    }

    private int creador;
    private String desafio; // Debería ser int pero en la BBDD está puesto como String
    private String fecha_creacion;
    private String foto_grupo;
//    private int id; -> Lo comento porque el id ya es la key del grupo
    private String miembros;
//    private int numero_integrantes; -> Vale con hacer un count en miembros
    private String titulo;
    private String contraseña;
    private String id;

    public Grupo() {
    }

    // Para grupos públicos
    public Grupo(int creador, String desafio, String fecha_creacion, String foto_grupo, String miembros, String titulo) {
        this.creador = creador;
        this.desafio = desafio;
        this.fecha_creacion = fecha_creacion;
        this.foto_grupo = foto_grupo;
        this.miembros = miembros;
        this.titulo = titulo;
    }

    // Para grupos privados

    public Grupo(int creador, String desafio, String fecha_creacion, String foto_grupo, String miembros, String titulo, String contraseña) {
        this.creador = creador;
        this.desafio = desafio;
        this.fecha_creacion = fecha_creacion;
        this.foto_grupo = foto_grupo;
        this.miembros = miembros;
        this.titulo = titulo;
        this.contraseña = contraseña;
    }

    // Para social
    public Grupo(String idGrupo, String nombreGrupo, String fotoGrupo) {
        this.id = idGrupo;
        this.titulo = nombreGrupo;
        this.foto_grupo = fotoGrupo;
    }

    public int getCreador() {
        return creador;
    }

    public void setCreador(int creador) {
        this.creador = creador;
    }

    public String getDesafio() {
        return desafio;
    }

    public void setDesafio(String desafio) {
        this.desafio = desafio;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getFoto_grupo() {
        return foto_grupo;
    }

    public void setFoto_grupo(String foto_grupo) {
        this.foto_grupo = foto_grupo;
    }

    public String getMiembros() {
        return miembros;
    }

    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getId() {
        return id;
    }
}

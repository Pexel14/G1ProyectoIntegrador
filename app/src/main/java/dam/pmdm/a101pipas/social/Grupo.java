package dam.pmdm.a101pipas.social;

public class Grupo {
    private String idGrupo;
    private String nombreGrupo;
    private String fotoGrupo;

    public Grupo() {

    }

    public Grupo(String idGrupo, String nombreGrupo, String fotoGrupo) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        this.fotoGrupo = fotoGrupo;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getFotoGrupo() {
        return fotoGrupo;
    }

    public void setFotoGrupo(String fotoGrupo) {
        this.fotoGrupo = fotoGrupo;
    }
}

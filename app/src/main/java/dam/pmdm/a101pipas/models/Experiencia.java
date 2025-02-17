package dam.pmdm.a101pipas.models;

public class Experiencia {
    private String titulo, desc_exp, link, mapa, imgExperiencia;
    private boolean completada;

    public Experiencia() {
    }

    // Constructor para Crear Experiencias
    public Experiencia(String titulo, String desc_exp) {
        this.titulo = titulo;
        this.desc_exp = desc_exp;
    }

    public Experiencia(String titulo, String desc_exp, String link, String mapa, String imgExperiencia, boolean completada) {
        this.titulo = titulo;
        this.desc_exp = desc_exp;
        this.link = link;
        this.mapa = mapa;
        this.imgExperiencia = imgExperiencia;
        this.completada = completada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc_exp() {
        return desc_exp;
    }

    public void setDesc_exp(String desc_exp) {
        this.desc_exp = desc_exp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMapa() {
        return mapa;
    }

    public void setMapa(String mapa) {
        this.mapa = mapa;
    }

    public String getImgExperiencia() {
        return imgExperiencia;
    }

    public void setImgExperiencia(String imgExperiencia) {
        this.imgExperiencia = imgExperiencia;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}

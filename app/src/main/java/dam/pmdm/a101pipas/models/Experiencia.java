package dam.pmdm.a101pipas.models;

import android.content.Intent;
import android.view.View;

public class Experiencia {
    private String titulo, descripcion, mapa, imgExperiencia;
    private boolean completada;

    public Experiencia() {
    }

    public Experiencia(String titulo, String descripcion, String mapa, String imgExperiencia, boolean completada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

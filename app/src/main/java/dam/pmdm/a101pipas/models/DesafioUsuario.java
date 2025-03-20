package dam.pmdm.a101pipas.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DesafioUsuario {
    private String usuario;
    private String estado;
    private String experiencias_completadas;

    public DesafioUsuario(String estado, String experiencias_completadas) {
        this.estado = estado;
        this.experiencias_completadas = experiencias_completadas;
    }

    public DesafioUsuario(String estado, String experiencias_completadas, String usuario) {
        this.estado = estado;
        this.experiencias_completadas = experiencias_completadas;
        this.usuario = usuario;
    }

    public DesafioUsuario() {
    }

    public String getEstado() {
        return estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getExperiencias_completadas() {
        return experiencias_completadas;
    }

    @Exclude
    public void setEstadoDesafioUsuario(int i) {
        if(i == 1){
            this.estado = "completado";
        } else if (i == 0){
            this.estado = "comenzado";
        }
    }

    @Exclude
    public List<String> getExperienciasCompletadasList() {
        if (experiencias_completadas == null || experiencias_completadas.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(experiencias_completadas.split(","));
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setExperiencias_completadas(String experiencias_completadas) {
        this.experiencias_completadas = experiencias_completadas;
    }
}

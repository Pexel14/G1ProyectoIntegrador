package dam.pmdm.a101pipas.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DesafioUsuario {
    private String estado;
    private String experiencias_completadas;

    public DesafioUsuario(String estado, String experiencias_completadas) {
        this.estado = estado;
        this.experiencias_completadas = experiencias_completadas;
    }

    public DesafioUsuario() {
    }

    public String getEstado() {
        return estado;
    }

    public String getExperiencias_completadas() {
        return experiencias_completadas;
    }

    public void setEstado(int i) {
        if(i == 1){
            this.estado = "completado";
        } else if (i == 0){
            this.estado = "comenzado";
        }
    }

    public List<String> getExperienciasCompletadasList() {
        if (experiencias_completadas == null || experiencias_completadas.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(experiencias_completadas.split(","));
    }
}

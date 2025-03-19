package dam.pmdm.a101pipas.perfil;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.DesafioPerfil;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.User;

public class PerfilViewModel extends ViewModel {
    private final MutableLiveData<User> usuarioLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<DesafioPerfil>> desafiosLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> filtroCompletados = new MutableLiveData<>(false);
    private final DatabaseReference refUsuarios;
    private final DatabaseReference refDesafios;
    private String usuarioId;

    public PerfilViewModel() {
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        refDesafios = FirebaseDatabase.getInstance().getReference("desafios");
    }

    public void setUsuarioId(String id) {
        usuarioId = id;
        cargarUsuario();
        cargarDesafiosUsuario();
    }

    // Métodos para filtrar (llamados desde el Fragment)
    public void seleccionarDesafiosCompletados() {
        filtroCompletados.setValue(true);
        cargarDesafiosUsuario();
    }

    public void seleccionarDesafiosEmpezados() {
        filtroCompletados.setValue(false);
        cargarDesafiosUsuario();
    }

    public void cargarUsuario() {
        refUsuarios.child(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int experiencias = 0;

                if(snapshot.hasChild("experiencias_completadas")){
                    experiencias = Integer.parseInt(snapshot.child("experiencias_completadas").getValue().toString());
                }

                User user = new User(
                        snapshot.child("id").getValue().toString(),
                        snapshot.child("username").getValue(String.class),
                        snapshot.child("email").getValue(String.class),
                        "",
                        snapshot.child("foto_perfil").getValue(String.class),
                        snapshot.child("grupos").getValue().toString(),
                        snapshot.child("amigos").getValue().toString(),
                        experiencias
                );

                usuarioLiveData.setValue(user);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void cargarDesafiosUsuario() {
        refUsuarios.child(usuarioId).child("desafios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DesafioPerfil> desafiosCombinados = new ArrayList<>();
                for (DataSnapshot desafioUserSnapshot : snapshot.getChildren()) {
                    String desafioId = desafioUserSnapshot.getKey();
                    DesafioUsuario desafioUsuario = desafioUserSnapshot.getValue(DesafioUsuario.class);

                    refDesafios.child(desafioId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot desafioSnapshot) {
                            Desafio desafioGeneral = new Desafio(
                                    desafioSnapshot.child("titulo").getValue().toString(),
                                    desafioSnapshot.child("experiencias").getValue().toString());
                            if (desafioGeneral != null) {
                                int totalExperiencias = desafioGeneral.getExperienciasRequeridas().size();
                                int completadas = desafioUsuario.getExperienciasCompletadasList().size();
                                int porcentaje = (int) ((completadas / (float) totalExperiencias) * 100);

                                DesafioPerfil desafioPerfil = new DesafioPerfil(
                                        desafioGeneral.getTitulo(),
                                        desafioUsuario.getEstado(),
                                        porcentaje
                                );
                                desafiosCombinados.add(desafioPerfil);

                                if (desafiosCombinados.size() == snapshot.getChildrenCount()) {
                                    aplicarFiltro(desafiosCombinados);
                                }
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void aplicarFiltro(List<DesafioPerfil> todosDesafios) {
        Boolean mostrarCompletados = filtroCompletados.getValue();
        List<DesafioPerfil> filtrados = new ArrayList<>();
        for (DesafioPerfil d : todosDesafios) {
            if (mostrarCompletados != null && mostrarCompletados && "completado".equals(d.getEstado())) {
                filtrados.add(d);
            } else if (mostrarCompletados != null && !mostrarCompletados && "comenzado".equals(d.getEstado())) {
                filtrados.add(d);
            }
        }
        desafiosLiveData.setValue(filtrados);
    }

    public LiveData<User> getUsuarioLiveData() { return usuarioLiveData; }
    public LiveData<List<DesafioPerfil>> getDesafiosLiveData() { return desafiosLiveData; }
}
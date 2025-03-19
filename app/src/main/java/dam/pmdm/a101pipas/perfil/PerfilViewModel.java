package dam.pmdm.a101pipas.perfil;

import android.provider.ContactsContract;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private MutableLiveData<Boolean> agregadoLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAgregadoLiveData() {
        return agregadoLiveData;
    }

    public void agregarAmigo(String usuario, String amigo) {
        final String[] idUsuario = new String[1];
        final String[] idAmigo = new String[1];

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsUsuario : snapshot.getChildren()) {
                    if (dsUsuario.getKey().equals(usuario)) {
                        idUsuario[0] = dsUsuario.child("id").getValue().toString();
                    } else if (dsUsuario.getKey().equals(amigo)) {
                        idAmigo[0] = dsUsuario.child("id").getValue().toString();
                    }
                }

//                for (DataSnapshot dsUsuario : snapshot.getChildren()) {
//                    if (dsUsuario.getKey().equals(usuario)) {
//                        String a = "";
//                        if (dsUsuario.child("amigos").hasChildren()) {
//                            a += ",";
//                        }
//
//                        a += idAmigo[0];
//
//                        refUsuarios
//                                .child(usuario)
//                                .child("amigos")
//                                .setValue(dsUsuario.child("amigos").toString() + a);
//                    } else if (dsUsuario.getKey().equals(amigo)) {
//                        String b = "";
//
//                        if (dsUsuario.child("amigos").hasChildren()) {
//                            b += ",";
//                        }
//
//                        b += idUsuario[0];
//
//                        refUsuarios
//                                .child(amigo)
//                                .child("amigos")
//                                .setValue(dsUsuario.child("amigos").toString() + b);
//                    }
//                }

                for (DataSnapshot dsUsuario : snapshot.getChildren()) {
                    String usuarioKey = dsUsuario.getKey();

                    if (usuarioKey.equals(usuario)) {
                        // Obtener el valor actual de "amigos" como String
                        String amigosActuales = dsUsuario.child("amigos").getValue(String.class);
                        String nuevosAmigos = "";

                        if (amigosActuales != null && !amigosActuales.isEmpty()) {
                            nuevosAmigos = amigosActuales + "," + idAmigo[0];
                        } else {
                            nuevosAmigos = idAmigo[0];
                        }

                        // Actualizar el nodo "amigos" del usuario
                        refUsuarios
                                .child(usuario)
                                .child("amigos")
                                .setValue(nuevosAmigos);

                    } else if (usuarioKey.equals(amigo)) {
                        // Obtener el valor actual de "amigos" del amigo
                        String amigosAmigo = dsUsuario.child("amigos").getValue(String.class);
                        String nuevosAmigosAmigo = "";

                        if (amigosAmigo != null && !amigosAmigo.isEmpty()) {
                            nuevosAmigosAmigo = amigosAmigo + "," + idUsuario[0];
                        } else {
                            nuevosAmigosAmigo = idUsuario[0];
                        }

                        // Actualizar el nodo "amigos" del amigo
                        refUsuarios
                                .child(amigo)
                                .child("amigos")
                                .setValue(nuevosAmigosAmigo);
                    }
                }

                agregadoLiveData.postValue(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private MutableLiveData<Boolean> esAmigoLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getEsAmigoLiveData() {return esAmigoLiveData;}

    public void esAmigo(String usuario, String amigo) {
        final boolean[] esAmigo = {false};
        final String[] idAmigo = {""};

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsUsuario : snapshot.getChildren()) {
                    if (dsUsuario.getKey().toString().equals(amigo)) {
                        idAmigo[0] = dsUsuario.child("id").getValue().toString();
                    }
                }

                refUsuarios.child(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("amigos").getValue().toString() != null) {
                            String[] amigos = snapshot.child("amigos").getValue().toString().split(",");


                            for (int i=0; i<amigos.length; i++) {
                                if (amigos[i].equals(idAmigo[0])) {
                                    Log.d("PerfilViewModel", "Es amigo");
                                    esAmigo[0] = true;
                                }
                            }

                        }

                        esAmigoLiveData.postValue(esAmigo[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
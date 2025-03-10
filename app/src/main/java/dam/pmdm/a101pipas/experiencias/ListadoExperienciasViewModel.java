package dam.pmdm.a101pipas.experiencias;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperienciasViewModel extends ViewModel {

    private static int total = 0;

    private Long idReal;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<String> desafioId = new MutableLiveData<>();
    public LiveData<String> getDesafioId() { return desafioId; }

    private final MutableLiveData<List<Experiencia>> experiencias = new MutableLiveData<>();
    public LiveData<List<Experiencia>> getExperiencias() { return experiencias; }

    private final MutableLiveData<Integer> progreso = new MutableLiveData<>();
    public LiveData<Integer> getProgreso() { return progreso; }

    private final MutableLiveData<String> tituloDesafio = new MutableLiveData<>();
    public LiveData<String> getTituloDesafio() { return tituloDesafio; }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void setIdDesafio(String id) {
        desafioId.setValue(id);
        cargarExperiencias(id);
        cargarTituloDesafio(id);
    }

    public void cargarExperiencias(String idDesafio) {
        if (idDesafio == null) return;

        List<Experiencia> listaExperiencias = new ArrayList<>();
        final int[] totalExperiencias = {0};

        database.child("desafios").child(idDesafio).child("experiencias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String[] exp = snapshot.getValue(String.class).split(",");
                        for (String idExp : exp) {
                            database.child("experiencias").child(idExp)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            Experiencia experiencia = new Experiencia(
                                                    snapshot2.child("titulo").getValue(String.class),
                                                    snapshot2.child("descripcion").getValue(String.class),
                                                    snapshot2.child("imagen").getValue(String.class),
                                                    snapshot2.child("coordenadas").getValue(String.class)
                                            );
                                            if (experiencia != null) {
                                                listaExperiencias.add(experiencia);
                                            }
                                            totalExperiencias[0]++;
                                            if (totalExperiencias[0] == exp.length) {
                                                experiencias.setValue(listaExperiencias);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Error al obtener experiencia: " + error.getMessage());
                                        }
                                    });
                        }

                        experiencias.setValue(listaExperiencias);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Error al obtener experiencias: " + error.getMessage());
                    }
                });
    }

    public void cargarTituloDesafio(String idDesafio) {
        if (idDesafio == null) return;

        database.child("desafios").child(idDesafio).child("titulo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        tituloDesafio.setValue(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Error al obtener título del desafío: " + error.getMessage());
                    }
                });
    }

    public void desafioEmpezado(final OnResultListener listener) {

        obtenerIdReal();

        String user = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");

        database.child("usuarios").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean empezado = false;
                if (snapshot.exists()) {
                    String[] desafios = snapshot.child("desafios").getValue(String.class).split(",");

                    for (String desafio : desafios) {
                        if (desafio.equals(String.valueOf(idReal))) {
                            empezado = true;
                            break; // Salir del bucle si se encuentra el desafío
                        }
                    }
                }
                listener.onResult(empezado); // Pasar el resultado al listener
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Error al cargar los desafíos del usuario");
                listener.onResult(false); // Si hay un error, devolvemos false
            }
        });
    }

    private void obtenerIdReal() {
        database.child("desafios").child(String.valueOf(desafioId.getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    idReal = (Long) snapshot.child("id").getValue();
                    Log.d("VIEW_MODEL", "ID DEL DESAFIO : " + idReal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void aniadirDesafioAUsuario(OnResultListener listener) {
        String user = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");

        database.child("usuarios").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String desafios = snapshot.child("desafios").getValue(String.class);
                    if (desafios == null) desafios = "";

                    if (!desafios.isEmpty()) desafios += ",";

                    desafios += idReal;

                    database.child("usuarios").child(user).child("desafios").setValue(desafios)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Llamar a desafioEmpezado SOLO después de que Firebase haya guardado el dato
                                    desafioEmpezado(listener);
                                } else {
                                    Log.e("Firebase", "Error al añadir el desafío al usuario");
                                    listener.onResult(false); // Si falla, enviamos false
                                }
                            });
                } else {
                    listener.onResult(false); // Si el usuario no existe, enviamos false
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error en la base de datos: " + error.getMessage());
                listener.onResult(false);
            }
        });
    }

    // Interfaz para el callback
    public interface OnResultListener {
        void onResult(boolean resultado);
    }
}

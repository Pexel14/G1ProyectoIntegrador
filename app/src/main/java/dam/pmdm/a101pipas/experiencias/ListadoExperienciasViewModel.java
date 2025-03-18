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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperienciasViewModel extends ViewModel {
    private static int totalExperiencias = 0;

    private Long idReal;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<String> desafioId = new MutableLiveData<>();
    public LiveData<String> getDesafioId() { return desafioId; }

    private final MutableLiveData<List<Experiencia>> experiencias = new MutableLiveData<>();
    public LiveData<List<Experiencia>> getExperiencias() { return experiencias; }

    private final MutableLiveData<Integer> progreso = new MutableLiveData<>();
    public LiveData<Integer> getProgreso() { return progreso; }

    private String tituloDesafio;
    public String getTituloDesafio() { return tituloDesafio; }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void setIdDesafio(String id, String titulo) {
        idReal = Long.parseLong(id);
        cargarExperiencias(titulo);
        tituloDesafio = titulo;
    }

    public void cargarExperiencias(String idDesafio) {
        if (idDesafio == null) return;
        totalExperiencias = 0;
        List<Experiencia> listaExperiencias = new ArrayList<>();

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
                                                    snapshot2.child("id").getValue().toString(),
                                                    snapshot2.child("titulo").getValue(String.class),
                                                    snapshot2.child("descripcion").getValue(String.class),
                                                    snapshot2.child("imagen").getValue(String.class),
                                                    snapshot2.child("coordenadas").getValue(String.class)
                                            );
                                            if (experiencia != null) {
                                                listaExperiencias.add(experiencia);
                                            }
                                            totalExperiencias++;
                                            if (totalExperiencias == exp.length) {
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

    public void desafioEmpezado(final OnResultListener listener) {
        String user = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");

        database.child("usuarios").child(user).child("desafios").child(tituloDesafio)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean empezado = snapshot.exists();

                        if (empezado) {
                            String estado = snapshot.child("estado").getValue(String.class);
                            empezado = !"completado".equals(estado);
                        }

                        listener.onResult(empezado);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error al verificar desafío: " + error.getMessage());
                        listener.onResult(false);
                    }
                });
    }

    public void comenzarDesafioEnUsuario(OnResultListener listener) {
        String user = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");

        DatabaseReference userRef = database.child("usuarios").child(user);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> desafioData = new HashMap<>();
                    desafioData.put("estado", "comenzado");
                    desafioData.put("experiencias_completadas", "");

                    userRef.child("desafios").child(tituloDesafio)
                            .setValue(desafioData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    desafioEmpezado(listener);
                                } else {
                                    listener.onResult(false);
                                    Log.e("Firebase", "Error al guardar desafío: " + task.getException());
                                }
                            });

                } else {
                    listener.onResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onResult(false);
                Log.e("Firebase", "Error en BD: " + error.getMessage());
            }
        });
    }

    public interface OnResultListener {
        void onResult(boolean resultado);
    }
}

package dam.pmdm.a101pipas.experiencias;

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
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperienciasViewModel extends ViewModel {

    private static int total = 0;

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<String> desafioId = new MutableLiveData<>();
    public LiveData<String> getDesafioId() { return desafioId; }

    private final MutableLiveData<List<Experiencia>> experiencias = new MutableLiveData<>();
    public LiveData<List<Experiencia>> getExperiencias() { return experiencias; }

    private final MutableLiveData<Integer> progreso = new MutableLiveData<>();
    public LiveData<Integer> getProgreso() { return progreso; }

    private final MutableLiveData<String> tituloDesafio = new MutableLiveData<>();
    public LiveData<String> getTituloDesafio() { return tituloDesafio; }

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
                                            Experiencia experiencia = snapshot2.getValue(Experiencia.class);
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
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Error al obtener experiencias: " + error.getMessage());
                    }
                });
    }

//        database.child("experiencias")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        List<Experiencia> listaExperiencias = new ArrayList<>();
//                        int total = 0, completadas = 0;
//
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            String titulo = data.child("titulo").getValue(String.class);
//                            String descripcion = data.child("descripcion").getValue(String.class);
//                            String imagen = data.child("imagen").getValue(String.class);
//                            String coordenadas = data.child("coordenadas").getValue(String.class);
//
//                            if (titulo != null && descripcion != null) {
//                                listaExperiencias.add(new Experiencia(
//                                        titulo,
//                                        descripcion,
//                                        imagen != null ? imagen : "",
//                                        coordenadas
//                                ));
//                                total++;
//                            }
//                        }
//
//                        experiencias.setValue(listaExperiencias);
//                        //TODO: En usuarios se encuentra experiencias_completadas,
//                        // cuando se complete una experiencia que se sume en el usuario
//                        //progreso.setValue(total > 0 ? (completadas * 100 / total) : 0);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        Log.e("Firebase", "Error al obtener experiencias: " + error.getMessage());
//                    }
//                });



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
}

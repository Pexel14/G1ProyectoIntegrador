package dam.pmdm.a101pipas.desafios.inicio;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;

public class InicioViewModel extends ViewModel {

    private final MutableLiveData<List<Desafio>> desafiosLiveData = new MutableLiveData<>();
    private ValueEventListener listenerUsuario, listenerDesafios;

    private String [] desafiosID;

    public LiveData<List<Desafio>> getDesafiosLiveData() {
        return desafiosLiveData;
    }

    public void cargarFragmentosDesafiosDesdeFirebase(DatabaseReference refDesafiosUsuario, DatabaseReference refDesafios) {
        listenerUsuario = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    desafiosLiveData.setValue(new ArrayList<>());
                    return;
                }

                refDesafiosUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> aux = new ArrayList<>();

                        Object valor = snapshot.getValue();

                        Log.d("INICIO", valor.getClass().getSimpleName() + " - " + snapshot.getChildrenCount());

                        if(valor instanceof Map){
                            Map<String, Object> map = (Map<String, Object>) valor;

                            if(map != null){
                                Set<String> claves = map.keySet();

                                aux = new ArrayList<>(claves);

                            }
                        }

                        if(!aux.isEmpty()){
                            desafiosID = aux.toArray(new String[0]);
                        } else {
                            desafiosID = new String [0];
                        }

                        cargarDesafiosPorId(refDesafios, desafiosID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", R.string.inicio_fragment_error_consulta + error.getMessage());
            }
        };

        refDesafiosUsuario.addValueEventListener(listenerUsuario);
    }

    private void cargarDesafiosPorId(DatabaseReference refDesafios, String[] desafiosId) {
        listenerDesafios = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Desafio> fragmentos = new ArrayList<>();

                for (DataSnapshot desafio : snapshot.getChildren()) {
                    String desafioId = desafio.child("titulo").getValue().toString();

                    if (Arrays.asList(desafiosId).contains(desafioId)) {
                        String titulo = desafio.child("titulo").getValue(String.class);
                        String etiquetas = desafio.child("etiquetas").getValue(String.class);
                        String descripcion = desafio.child("descripcion").getValue(String.class);
                        String ciudad = desafio.child("ciudad").getValue(String.class);

                        fragmentos.add(new Desafio(titulo, ciudad, descripcion, etiquetas, desafioId));
                    }
                }

                desafiosLiveData.setValue(fragmentos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", R.string.inicio_fragment_error_consulta + error.getMessage());
            }
        };

        refDesafios.addValueEventListener(listenerDesafios);
    }


}


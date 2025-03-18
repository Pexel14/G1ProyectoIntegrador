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

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;

public class InicioViewModel extends ViewModel {

    private final MutableLiveData<List<Desafio>> desafiosLiveData = new MutableLiveData<>();
    private ValueEventListener listenerUsuario, listenerDesafios;

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

                String[] desafiosID = snapshot.getValue().toString().split(",");
                cargarDesafiosPorId(refDesafios, desafiosID);
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
                    String desafioId = ""; 

                    DataSnapshot idSnapshot = desafio.child("id");

                    if (idSnapshot.exists() && idSnapshot.getValue() != null) {
                        desafioId = idSnapshot.getValue().toString();
                        Log.d("Firebase", "ID del desafío: " + desafioId);
                    } else {
                        Log.e("Firebase", "El campo 'id' no existe o es null");
                    }

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


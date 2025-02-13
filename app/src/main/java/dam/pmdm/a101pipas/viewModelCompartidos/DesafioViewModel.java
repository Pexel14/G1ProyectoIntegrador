package dam.pmdm.a101pipas.viewModelCompartidos;

import android.util.Log;

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

public class DesafioViewModel extends ViewModel {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<String> desafioId = new MutableLiveData<>();
    public LiveData<String> getDesafioId() { return desafioId; }

    private final MutableLiveData<List<Experiencia>> experiencias = new MutableLiveData<>();
    public LiveData<List<Experiencia>> getExperiencias() { return experiencias; }

    public void setDesafioId(String id) {
        desafioId.setValue(id);
        getExperienciasPorDesafio(id);
    }

    public void getExperienciasPorDesafio(String id) {
        if (id == null) return;

        database.child("desafios").child(id).child("experiencias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Experiencia> listaExperiencias = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String titulo = child.child("titulo").getValue(String.class);
                            String coordenadas = child.child("coordenadas").getValue(String.class);
                            if (titulo != null && coordenadas != null) {
                                listaExperiencias.add(new Experiencia(titulo, coordenadas));
                            }
                        }
                        experiencias.setValue(listaExperiencias);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(R.string.geolocalizacion_error_leer_exp + error.getMessage());
                    }
                });
    }
}

package dam.pmdm.a101pipas.viewModelCompartidos;

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

import dam.pmdm.a101pipas.models.Experiencia;

public class DesafioViewModel extends ViewModel {

    private MutableLiveData<String> desafioActual;
    private MutableLiveData<List<Experiencia>> experiencias;
    private DatabaseReference dbRef;

    public DesafioViewModel() {
        desafioActual = new MutableLiveData<>();
        experiencias = new MutableLiveData<>();
        dbRef = FirebaseDatabase.getInstance().getReference("desafios");
    }

    // Getter para el desafío actual
    public LiveData<String> getDesafioActual() {
        return desafioActual;
    }

    // Setter para el desafío actual
    public void setDesafioActual(String desafioId) {
        desafioActual.setValue(desafioId);
        cargarExperiencias(desafioId);
    }

    // Getter para las experiencias
    public LiveData<List<Experiencia>> getExperiencias() {
        return experiencias;
    }

    public void cargarExperiencias(String desafioId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("desafios").child(desafioId).child("experiencias");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Experiencia> experienciaList = new ArrayList<>();
                for (DataSnapshot experienciaSnapshot : snapshot.getChildren()) {
                    Experiencia experiencia = experienciaSnapshot.getValue(Experiencia.class);
                    if (experiencia != null) {
                        experienciaList.add(experiencia);
                    }
                }
                experiencias.setValue(experienciaList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DesafioViewModel", "Error cargando experiencias: " + error.getMessage());
            }
        });
    }
}
package dam.pmdm.a101pipas.geolocalizacion;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class GeolocalizacionViewModel extends ViewModel {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();

    public LiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng location) {
        currentLocation.setValue(location);
    }

    private final MutableLiveData<LatLng> destinoExpSelec = new MutableLiveData<>();

    public void setDestinoExperiencia(LatLng destino) {
        destinoExpSelec.setValue(destino);
    }

    public LiveData<LatLng> getDestinoExperiencia() {
        return destinoExpSelec;
    }

    private final MutableLiveData<String> desafioId = new MutableLiveData<>();
    public LiveData<String> getDesafioId() { return desafioId; }

    private final MutableLiveData<List<Experiencia>> experiencias = new MutableLiveData<>();
    public LiveData<List<Experiencia>> getExperiencias() { return experiencias; }

    private static List<Experiencia> listaExperiencias;

    public void setDesafioId(String id) {
        desafioId.setValue(id);
        getExperienciasPorDesafio(id);
    }

    public void getExperienciasPorDesafio(String id) {
        if (id == null) return;
        listaExperiencias = new ArrayList<>();
        database.child("desafios").child(id).child("experiencias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String [] exp = snapshot.getValue(String.class).split(",");
                        for (String idExp: exp) {
                            database.child("experiencias").child(idExp).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    for (DataSnapshot data : snapshot2.getChildren()) {

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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

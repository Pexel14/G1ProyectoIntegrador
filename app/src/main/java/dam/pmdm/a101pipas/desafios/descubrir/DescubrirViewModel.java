package dam.pmdm.a101pipas.desafios.descubrir;

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

public class DescubrirViewModel extends ViewModel {

    private final MutableLiveData<List<TarjetaDesafioDescubrirFragment>> fragmentosList;
    private final DatabaseReference ref;

    public DescubrirViewModel() {
        fragmentosList = new MutableLiveData<>();
        ref = FirebaseDatabase.getInstance().getReference("desafios");
    }

    public LiveData<List<TarjetaDesafioDescubrirFragment>> getFragmentosList() {
        return fragmentosList;
    }

    public void cargarFragmentos(String filtro1Text) {
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TarjetaDesafioDescubrirFragment> fragmentos = new ArrayList<>();

                for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                    String titulo = nodeSnapshot.child("titulo").getValue(String.class);
                    String ciudad = nodeSnapshot.child("ciudad").getValue(String.class);
                    String sEtiquetas = nodeSnapshot.child("etiquetas").getValue(String.class);
                    String[] etiquetas = sEtiquetas.split(",");
                    String key = nodeSnapshot.getKey();

                    if (ciudad != null && (ciudad.equals(filtro1Text) || sEtiquetas.contains("Gastronomía") || sEtiquetas.contains("Cultura"))) {
                        fragmentos.add(TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, etiquetas, key));
                    }

                }

                fragmentosList.setValue(fragmentos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al cargar fragmentos: " + error.getMessage());
            }
        });
    }
}

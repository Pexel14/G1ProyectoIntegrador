package dam.pmdm.a101pipas.social;

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

import dam.pmdm.a101pipas.models.Amigos;

public class AmigosViewModel extends ViewModel {
    private final MutableLiveData<List<Amigos>> amigosList = new MutableLiveData<>();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("usuarios");

    public AmigosViewModel() {
        cargarListaAmigosDesdeFirebase();
    }

    public LiveData<List<Amigos>> getAmigosList() {
        return amigosList;
    }

    private void cargarListaAmigosDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Amigos> lista = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String userId = data.getKey();
                    String username = data.child("username").getValue(String.class);
                    String email = data.child("email").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);

                    if (username != null && userId != null) {
                        lista.add(new Amigos(userId, username, fotoPerfil, email));
                    }
                }
                amigosList.setValue(lista);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // A gestionar
            }
        });
    }
}

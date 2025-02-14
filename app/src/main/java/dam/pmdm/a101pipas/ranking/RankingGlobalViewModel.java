package dam.pmdm.a101pipas.ranking;

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

import dam.pmdm.a101pipas.models.User;

public class RankingGlobalViewModel extends ViewModel {
    private final MutableLiveData<List<User>> _usuarios = new MutableLiveData<>();
    public LiveData<List<User>> usuarios = _usuarios;

    private final DatabaseReference ref;

    public RankingGlobalViewModel() {
        ref = FirebaseDatabase.getInstance().getReference("usuarios");
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        ref.orderByChild("experiencias_completadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> listaUsuarios = new ArrayList<>();
                String nombre;
                Integer completadas;
                for (DataSnapshot subNodo : snapshot.getChildren()) {
                    nombre = subNodo.child("username").getValue(String.class);
                    completadas = subNodo.child("experiencias_completadas").getValue(Integer.class);
                    if (completadas != null) {
                        listaUsuarios.add(new User(completadas, nombre));
                    }
                }
                _usuarios.setValue(listaUsuarios);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Ranking", "Error al acceder a la base de datos");
            }
        });
    }
}



package dam.pmdm.a101pipas.social;

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
import java.util.List;

import dam.pmdm.a101pipas.desafios.descubrir.TarjetaDesafioDescubrirFragment;
import dam.pmdm.a101pipas.models.Grupo;

public class CrearGrupoViewModel extends ViewModel {

    private DatabaseReference refDesafios = FirebaseDatabase.getInstance().getReference("desafios");
    private DatabaseReference refGrupos = FirebaseDatabase.getInstance().getReference("grupos");;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Obtener si existe un usuario
    private MutableLiveData<Boolean> usuarioExisteMutableLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getUsuarioExisteLiveData() {return usuarioExisteMutableLiveData;}

    public void getUsuarioExiste(String usuario) {

        refUsuarios.child(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usuarioExisteMutableLiveData.setValue(true);
                } else {
                    usuarioExisteMutableLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CrearGrupoViewModel", "Error al obtener si un usuario existe");
                usuarioExisteMutableLiveData.setValue(null); // Limpiar el LiveData
            }
        });

    }

    public void limpiarUsuarioExisteLiveData() {
        usuarioExisteMutableLiveData.setValue(null);
    }

    // Crear un grupo
    private MutableLiveData<Boolean> grupoCreadoLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getCrearGrupoLiveData() {return grupoCreadoLiveData;}

    public void crearGrupo(String key, Grupo grupo) {
        refGrupos.child(key).setValue(grupo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    grupoCreadoLiveData.setValue(true);
                } else {
                    grupoCreadoLiveData.setValue(false);
                }
            }
        });
    }

    // Obtener los desafíos
    private MutableLiveData<List<TarjetaDesafioDescubrirFragment>> desafiosLiveData = new MutableLiveData<>();

    public LiveData<List<TarjetaDesafioDescubrirFragment>> getDesafiosLiveData() {
        return desafiosLiveData;
    }

    public void cargarFragments() {

        refUsuarios.child(
                mAuth.getCurrentUser().getEmail()
                .split("@")[0].replace(".", "")
        ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] desafios = snapshot.child("desafios").getValue().toString().split(",");

                refDesafios.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<TarjetaDesafioDescubrirFragment> fragments = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot desafio : snapshot.getChildren()) {

                                String id = desafio.child("id").getValue().toString();

                                for (int i=0; i<desafios.length; i++) {
                                    if (desafios[i].equals(id)) {
                                        String key = desafio.getKey();
                                        String titulo = desafio.child("titulo").getValue(String.class);
                                        String ciudad = desafio.child("ciudad").getValue(String.class);

                                        Log.d("FirebaseData", "Key: " + key + ", Titulo: " + titulo + ", Ciudad: " + ciudad);

                                        TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, key);
                                        fragments.add(fragment);
                                    }
                                }
                            }
                        }

                        desafiosLiveData.postValue(fragments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error al obtener desafíos: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
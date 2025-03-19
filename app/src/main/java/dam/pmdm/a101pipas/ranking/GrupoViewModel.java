package dam.pmdm.a101pipas.ranking;

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
import java.util.HashMap;
import java.util.Map;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

public class GrupoViewModel extends ViewModel {

    private MutableLiveData<Grupo> grupoLiveData;
    private MutableLiveData<ArrayList<User>> miembrosLiveData;

    private int idGrupo;

    public GrupoViewModel() {
        grupoLiveData = new MutableLiveData<>();
        miembrosLiveData = new MutableLiveData<>();
    }

    public LiveData<Grupo> getGrupo() {
        return grupoLiveData;
    }

    public MutableLiveData<ArrayList<User>> getMiembrosLiveData() {
        return miembrosLiveData;
    }

    public void setIdGrupo(int id) {
        this.idGrupo = id;
        cargarGrupo();
        cargarMiembros();
    }

    // Método para cargar los datos del grupo desde Firebase usando el ID
    private void cargarGrupo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("grupos").child(String.valueOf(idGrupo));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String titulo = dataSnapshot.child("titulo").getValue(String.class);
                    String contrasena = dataSnapshot.child("contrasena").getValue(String.class);
                    Grupo grupo = new Grupo(titulo, contrasena);
                    grupoLiveData.setValue(grupo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(R.string.error_al_leer_el_grupo + error.getMessage());
            }
        });
    }

    private void cargarMiembros(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> listaUsuarios = new ArrayList<>();
                String grupos;
                for (DataSnapshot data : snapshot.getChildren()) {
                    grupos = data.child("grupos").getValue().toString();
                    if(grupos != null && !grupos.isEmpty()){
                        if(grupos.contains(String.valueOf(idGrupo))){
                            int experiencias = 0;

                            if(data.hasChild("experiencias_completadas")){
                                experiencias = Integer.parseInt(data.child("experiencias_completadas").getValue().toString());
                            }

                            listaUsuarios.add(new User(
                                    data.child("id").getValue().toString(),
                                    data.child("username").getValue(String.class),
                                    data.child("email").getValue(String.class),
                                    data.child("contrasenia").getValue(String.class),
                                    data.child("foto_perfil").getValue(String.class),
                                    data.child("grupos").getValue().toString(),
                                    "",
                                    experiencias

                            ));
                        }
                    }
                }
                miembrosLiveData.setValue(listaUsuarios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
package dam.pmdm.a101pipas.ranking;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

public class GrupoViewModel extends ViewModel {

    private MutableLiveData<Grupo> grupoLiveData;
    private MutableLiveData<ArrayList<User>> miembrosLiveData;

    private int idGrupo;
    private String experiencias;
    private static Desafio desafio;

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

    public void conseguirExperiencias(int idDesafio){

        conseguirTitulo(idDesafio).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
                    refUsuarios.child(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".",""))
                        .child("desafios").child(desafio.getTitulo()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DesafioUsuario desafioUsuario = new DesafioUsuario(
                                        snapshot.child("estado").getValue().toString(),
                                        snapshot.child("experiencias_completadas").getValue().toString()
                                );

                                experiencias = desafioUsuario.getExperiencias_completadas();
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
                }
            }
        });
    }

    private Task<Boolean> conseguirTitulo(int idDesafio) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference refDesafios = FirebaseDatabase.getInstance().getReference("desafios");

        refDesafios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.child("id").getValue().toString().equals(String.valueOf(idDesafio))){
                        desafio = new Desafio(
                                data.child("titulo").getValue(String.class),
                                data.child("experiencias").getValue().toString()
                        );
                        taskCompletionSource.setResult(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult(false);
            }
        });


        return taskCompletionSource.getTask();
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

    public String getExperiencias() {
        return experiencias;
    }
}
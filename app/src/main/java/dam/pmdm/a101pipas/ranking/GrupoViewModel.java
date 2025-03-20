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
import java.util.List;
import java.util.Map;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

public class GrupoViewModel extends ViewModel {

    private MutableLiveData<Grupo> grupoLiveData;
    private MutableLiveData<ArrayList<User>> miembrosLiveData;
    private MutableLiveData<DesafioUsuario> experienciasLiveData;
    private MutableLiveData<Desafio> desafioLiveData;

    private static List<DesafioUsuario> desafioUsuarios = new ArrayList<>();
    private int idGrupo;
    private String experiencias;
    private static Desafio desafio;

    public GrupoViewModel() {
        grupoLiveData = new MutableLiveData<>();
        miembrosLiveData = new MutableLiveData<>();
        experienciasLiveData = new MutableLiveData<>();
        desafioLiveData = new MutableLiveData<>();
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
                    String desafio = dataSnapshot.child("desafio").getValue().toString();
                    String miembros = dataSnapshot.child("miembros").getValue().toString();
                    String contrasena = dataSnapshot.child("contrasena").getValue(String.class);
                    Grupo grupo = new Grupo(titulo, contrasena, Integer.parseInt(desafio), miembros);
                    grupoLiveData.setValue(grupo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(R.string.error_al_leer_el_grupo + error.getMessage());
            }
        });
    }

    public void conseguirExperiencias(int idDesafio, String miembros){
        conseguirTitulo(idDesafio).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    conseguirDesafio(miembros).addOnCompleteListener(new OnCompleteListener<ArrayList<DesafioUsuario>>() {
                        @Override
                        public void onComplete(@NonNull Task<ArrayList<DesafioUsuario>> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().isEmpty()){

                                    for(DesafioUsuario desafioUsuario : task.getResult()){
                                        experienciasLiveData.setValue(desafioUsuario);
                                    }

                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private Task<ArrayList<DesafioUsuario>> conseguirDesafio(String miembros) {
        String [] usuarios = miembros.split(",");
        TaskCompletionSource<ArrayList<DesafioUsuario>> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        for (String user : usuarios){
            refUsuarios.child(user)
                    .child("desafios").child(desafio.getTitulo()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                desafioUsuarios = new ArrayList<>();

                                desafioUsuarios.add(new DesafioUsuario(
                                        snapshot.child("estado").getValue().toString(),
                                        snapshot.child("experiencias_completadas").getValue().toString(),
                                        user
                                ));

                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {
                            taskCompletionSource.setResult(new ArrayList<>());
                        }
                    });
        }
        if(!desafioUsuarios.isEmpty()){
            taskCompletionSource.setResult(new ArrayList<>(desafioUsuarios));
        }
        return taskCompletionSource.getTask();
    }

    private Task<Boolean> conseguirTitulo(int idDesafio) {
        desafioLiveData.setValue(null);
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference refDesafios = FirebaseDatabase.getInstance().getReference("desafios");

        refDesafios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.child("id").getValue().toString().equals(String.valueOf(idDesafio))){
                        desafioLiveData.setValue(new Desafio(
                                data.child("titulo").getValue(String.class),
                                data.child("experiencias").getValue().toString()
                        ));

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
                            Map<String, DesafioUsuario> desafios = null;
                            if(data.hasChild("experiencias_completadas")){
                                String s = data.child("experiencias_completadas").getValue().toString();

                                if(!s.isEmpty()){
                                    experiencias = Integer.parseInt(s);
                                }
                            }

                            Object valor = data.child("desafios").getValue();
                            if(data.hasChild("desafios")){
                                desafios = (Map<String, DesafioUsuario>) valor;
                            }

                            listaUsuarios.add(new User(
                                    data.child("id").getValue().toString(),
                                    data.child("username").getValue(String.class),
                                    data.child("email").getValue(String.class),
                                    "",
                                    data.child("foto_perfil").getValue(String.class),
                                    data.child("grupos").getValue().toString(),
                                    data.child("amigos").getValue().toString(),
                                    experiencias,
                                    desafios
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

    public MutableLiveData<DesafioUsuario> getExperiencias() {
        return experienciasLiveData;
    }

    public MutableLiveData<Desafio> getDesafioLiveData() {
        return desafioLiveData;
    }

    public static Desafio getDesafio() {
        return desafio;
    }
}
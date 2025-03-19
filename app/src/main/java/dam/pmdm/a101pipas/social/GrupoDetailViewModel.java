package dam.pmdm.a101pipas.social;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

public class GrupoDetailViewModel extends ViewModel {
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("grupos");
    private DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("usuarios");

    private static Grupo grupo;

    public Task<Boolean> cargarDatosGrupo(String idGrupo) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        dbRef.child(idGrupo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nombreGrupo = snapshot.child("titulo").getValue(String.class);
                String fotoGrupo = snapshot.child("foto_grupo").getValue(String.class);
                String creador = snapshot.child("creador").getValue().toString();
                String miembros = snapshot.child("miembros").getValue().toString();
                String desafio = snapshot.child("desafio").getValue().toString();

                grupo = new Grupo(Integer.parseInt(creador), Integer.parseInt(desafio), "", fotoGrupo, Integer.parseInt(idGrupo), miembros, nombreGrupo);

                if(snapshot.hasChild("contrasena")){
                    grupo.setContrasena(snapshot.child("contrasena").getValue().toString());
                }

                taskCompletionSource.setResult(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public Task<Boolean> aniadirGrupoUser(String userID, String grupoID) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        dbUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int experiencias = 0;
                Map<String, DesafioUsuario> desafios = null;

                if(snapshot.hasChild("experiencias_completadas")){
                    experiencias = Integer.parseInt(snapshot.child("experiencias_completadas").getValue().toString());
                }
                Object valor = snapshot.child("desafios").getValue();
                if(snapshot.hasChild("desafios")){
                    desafios = (Map<String, DesafioUsuario>) valor;
                }

                User usuario = new User(
                        snapshot.child("id").getValue().toString(),
                        snapshot.child("username").getValue(String.class),
                        snapshot.child("email").getValue(String.class),
                        "",
                        snapshot.child("foto_perfil").getValue(String.class),
                        snapshot.child("grupos").getValue().toString(),
                        snapshot.child("amigos").getValue().toString(),
                        experiencias,
                        desafios
                );

                if(usuario.getGrupos() == null || usuario.getGrupos().trim().isEmpty()){
                    usuario.setGrupos(grupoID);
                } else {
                    usuario.setGrupos(usuario.getGrupos() + "," + grupoID);
                }

                grupo.setMiembros(grupo.getMiembros() + "," + userID);
                dbRef.child(grupoID).setValue(grupo);

                dbUser.child(userID).setValue(usuario);

                taskCompletionSource.setResult(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> isPrivado(String grupoID) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        dbRef.child(grupoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("contrasena")){
                    taskCompletionSource.setResult(true);
                } else {
                    taskCompletionSource.setResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return taskCompletionSource.getTask();
    }
}

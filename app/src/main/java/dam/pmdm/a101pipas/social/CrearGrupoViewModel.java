package dam.pmdm.a101pipas.social;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.a101pipas.desafios.descubrir.TarjetaDesafioDescubrirFragment;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

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

    public void crearGrupo(String key, Grupo grupo, Uri imageUri) {

        String id = grupo.getTitulo() + String.valueOf(grupo.getId());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("grupos_images");
        StorageReference imageRef = storageReference.child(id + ".jpg");

        // Subimos la imagen a Storage
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Obtenemos la URL
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // Se la asignamos al grupo
                        grupo.setFoto_grupo(uri.toString());

                        // Creamos el grupo
                        refGrupos.child(key).setValue(grupo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    grupoCreadoLiveData.setValue(true);
                                    refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dsUsuario : snapshot.getChildren()) {
//                                                User user = dsUsuario.getValue(User.class);
                                                Map<String, DesafioUsuario> desafios = null;
                                                int experiencias = 0;
                                                if(snapshot.hasChild("experiencias_completadas")){
                                                    experiencias = Integer.parseInt(dsUsuario.child("experiencias_completadas").getValue().toString());
                                                }

                                                Object valor = snapshot.child("desafios").getValue();
                                                if(snapshot.hasChild("desafios")){
                                                    desafios = (Map<String, DesafioUsuario>) valor;
                                                }

                                                User user = new User(
                                                        dsUsuario.child("id").getValue().toString(),
                                                        dsUsuario.child("username").getValue().toString(),
                                                        dsUsuario.child("email").getValue().toString(),
                                                        "",
                                                        dsUsuario.child("foto_perfil").getValue().toString(),
                                                        dsUsuario.child("grupos").getValue().toString(),
                                                        dsUsuario.child("amigos").getValue().toString(),
                                                        experiencias,
                                                        desafios
                                                );

                                                String pwd = "";
                                                if (snapshot.hasChild("contrasenia")) {
                                                    pwd = snapshot.child("contrasenia").getValue().toString();
                                                    user.setContrasenia(pwd);
                                                }

                                                String userName = user.getEmail().split("@")[0].replace(".", "");
                                                if (grupo.getMiembros().contains(userName)) {
                                                    refUsuarios.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String grupos = snapshot.child("grupos").getValue().toString();
                                                            grupos += "," + grupo.getId();
                                                            Map<String, Object> mapGrupos = new HashMap<>();
                                                            mapGrupos.put("grupos", grupos);
                                                            refUsuarios.child(userName).updateChildren(mapGrupos);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {
                                    grupoCreadoLiveData.setValue(false);
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    // Obtener los desafíos
    private MutableLiveData<List<TarjetaDesafioDescubrirFragment>> desafiosLiveData = new MutableLiveData<>();

    public LiveData<List<TarjetaDesafioDescubrirFragment>> getDesafiosLiveData() {
        return desafiosLiveData;
    }

    public void cargarFragments() {

        Log.d("CrearGrupoViewModel", "Empieza 'cargarFragments'");

        refUsuarios.child(
                mAuth.getCurrentUser().getEmail()
                .split("@")[0].replace(".", "")
        ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                GenericTypeIndicator<HashMap<String, Object>> typeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> desafios = snapshot.child("desafios").getValue(typeIndicator);

                Log.d("CrearGrupoViewModel", "Se obtienen los desafíos del usuario: ");

                for (String key : desafios.keySet()) {
                    Log.d("CrearGrupoViewModel", "- " + key);
                }

                Log.d("CrearGrupoViewModel", "Fin de los desafíos");

                refDesafios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d("CrearGrupoViewModel", "Empieza a buscar los desafios (todos)");

                        List<TarjetaDesafioDescubrirFragment> fragments = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot desafio : snapshot.getChildren()) {

                                String id = desafio.getKey();

                                if (desafios.containsKey(id)) {
                                    Log.d("CrearGrupoViewModel", "'desafios' contiene " + id);

                                    String key = desafio.getKey();
                                    String titulo = desafio.child("titulo").getValue(String.class);
                                    String ciudad = desafio.child("ciudad").getValue(String.class);

                                    Log.d("FirebaseData", "Key: " + key + ", Titulo: " + titulo + ", Ciudad: " + ciudad);

                                    TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, key);
                                    fragments.add(fragment);
                                    Log.d("CrearGrupoViewModel", "Tarjeta (fragmento) añadida");
                                }
                            }

                            desafiosLiveData.setValue(fragments);

                            if (desafiosLiveData == null) {
                                Log.d("CrearGrupoViewModel", "El live data está vacío");
                            } else {
                                Log.d("CrearGrupoViewModel", "El live data está lleno");
                            }

                        }

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

    // Obtener el ID del usuario actual
    private MutableLiveData<String> idUsuarioLiveData = new MutableLiveData<>();

    public LiveData<String> getIdUsuarioLiveData() {return idUsuarioLiveData;}

    public void getIdUsuario(String usuario) {
        refUsuarios.child(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getid() != null) {
                        idUsuarioLiveData.postValue(user.getid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void limpiarIdUsuarioLiveData() {
        idUsuarioLiveData.setValue(null);
    }
}
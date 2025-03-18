package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentListaGruposBinding;
import dam.pmdm.a101pipas.models.Amigos;


public class ListaGruposFragment extends Fragment {

    private GruposAdapter gruposAdapter;
    private List<Grupo> gruposList;
    private DatabaseReference dbRef, dbUser;
    private static String grupos;

    private FirebaseAuth mAuth;

    private FragmentListaGruposBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListaGruposBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("grupos");
        dbUser = FirebaseDatabase.getInstance().getReference("usuarios");

        gruposList = new ArrayList<>();
        gruposAdapter = new GruposAdapter(gruposList, this);
        binding.rvListaGrupos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListaGrupos.setAdapter(gruposAdapter);

        getGruposUsuario().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    if(task.getResult()){
                        cargarListaGruposDesdeFirebase();
                    }
                }
            }
        });

        // Botón para volver atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_social));

        binding.barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);

                if(gruposList.isEmpty()){
                    binding.tvMensajeGruposVacio.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMensajeGruposVacio.setVisibility(View.GONE);
                }

                return true;
            }
        });
    }

    private Task<Boolean> getGruposUsuario() {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String id = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".","");
        dbUser.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grupos = snapshot.child("grupos").getValue(String.class);
                taskCompletionSource.setResult(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }

    private void buscar(String newText) {
        ArrayList<Grupo> auxGrupos = new ArrayList<>();
        for (Grupo grupo : gruposList) {
            if(grupo.getNombreGrupo().toLowerCase().startsWith(newText.toLowerCase())){
                if(!grupos.contains(grupo.getIdGrupo())){
                    auxGrupos.add(grupo);
                }
            }
        }

        if(auxGrupos.isEmpty()){
            binding.tvMensajeGruposVacio.setVisibility(View.VISIBLE);
        }

        binding.rvListaGrupos.setAdapter(new GruposAdapter(auxGrupos, this));

    }

    private void cargarListaGruposDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gruposList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    // Obtener el ID del grupo (clave del nodo)
                    String idGrupo = data.getKey();
                    String nombreGrupo = data.child("titulo").getValue(String.class);
                    String fotoGrupo = data.child("foto_grupo").getValue(String.class);


                    if (fotoGrupo == null || fotoGrupo.isEmpty()) {
                        fotoGrupo = "https://example.com/default_group.png"; // URL de imagen por defecto
                    }

                    if (nombreGrupo != null) {
                        if(grupos != null){
                            if(!grupos.contains(idGrupo)){
                                gruposList.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo)); // Pasamos el ID al modelo
                            }
                        }
                    }
                }

                if(gruposList.isEmpty()){
                    binding.tvMensajeGruposVacio.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMensajeGruposVacio.setVisibility(View.GONE);
                }

                gruposAdapter.setGruposList(gruposList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package dam.pmdm.a101pipas.social;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentSocialBinding;
import dam.pmdm.a101pipas.models.Amigos;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;

    private SocialAdapter amigosAdapter, gruposAdapter;
    private List<Amigos> amigosList, todosAmigos;
    private List<Grupo> gruposList, todosGrupos;
    private FirebaseAuth mAuth;
    private DatabaseReference amigosRef, gruposRef, usuarioRef;
    String amigosUser, gruposUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".","");

        // Referencias en Firebase
        amigosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        usuarioRef = FirebaseDatabase.getInstance().getReference("usuarios").child(user);
        gruposRef = FirebaseDatabase.getInstance().getReference("grupos");

        buscarAmigosYGrupos();

        // Inicializar listas
        amigosList = new ArrayList<>();
        gruposList = new ArrayList<>();
        todosAmigos = new ArrayList<>();
        todosGrupos = new ArrayList<>();

        // Inicializar adaptadores
        amigosAdapter = new SocialAdapter(amigosList, this);
        gruposAdapter = new SocialAdapter(gruposList, this);

        // Configurar RecyclerViews
        binding.rvAmigos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvGrupos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        binding.rvAmigos.setAdapter(amigosAdapter);
        binding.rvGrupos.setAdapter(gruposAdapter);

        // Cargar datos desde Firebase
        cargarTodosGrupos();
        cargarTodosAmigos();
        cargarAmigos();
        cargarGrupos();

        // Botón para ver más amigos
        binding.btnMasAmigos.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_lista_amigos));

        // Botón para ver más grupos
        binding.btnMasGrupos.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_lista_grupos));

        binding.btnCrearGrupo.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_crear_grupo));

        binding.btnGrupoDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), GroupDetail.class);
            intent.putExtra("groupId", "1");
            startActivity(intent);
        });

        binding.barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                if(newText.isEmpty()){
                    binding.rvAmigosBuscador.setVisibility(View.GONE);
                    binding.rvGruposBuscador.setVisibility(View.GONE);
                    binding.tvAmigosBuscador.setVisibility(View.GONE);
                    binding.tvGruposBuscador.setVisibility(View.GONE);
                    binding.rvGrupos.setVisibility(View.VISIBLE);
                    binding.rvAmigos.setVisibility(View.VISIBLE);
                    binding.btnMasAmigos.setVisibility(View.VISIBLE);
                    binding.btnMasGrupos.setVisibility(View.VISIBLE);
                    binding.btnGrupoDetalles.setVisibility(View.VISIBLE);
                    binding.btnCrearGrupo.setVisibility(View.VISIBLE);
                    binding.tvAmigos.setVisibility(View.VISIBLE);
                    binding.tvGrupos.setVisibility(View.VISIBLE);
                } else {
                    binding.rvAmigosBuscador.setVisibility(View.VISIBLE);
                    binding.rvGruposBuscador.setVisibility(View.VISIBLE);
                    binding.tvGruposBuscador.setVisibility(View.VISIBLE);
                    binding.tvAmigosBuscador.setVisibility(View.VISIBLE);
                    binding.tvNoGrupos.setVisibility(View.GONE);
                    binding.tvNoAmigos.setVisibility(View.GONE);
                    binding.rvGrupos.setVisibility(View.GONE);
                    binding.rvAmigos.setVisibility(View.GONE);
                    binding.btnMasAmigos.setVisibility(View.GONE);
                    binding.btnMasGrupos.setVisibility(View.GONE);
                    binding.btnGrupoDetalles.setVisibility(View.GONE);
                    binding.btnCrearGrupo.setVisibility(View.GONE);
                    binding.tvAmigos.setVisibility(View.GONE);
                    binding.tvGrupos.setVisibility(View.GONE);
                }
                return true;
            }
        });

    }

    private void buscarAmigosYGrupos() {
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gruposUser = snapshot.child("grupos").getValue(String.class);
                amigosUser = snapshot.child("amigos").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscar(String newText) {
        ArrayList<Grupo> auxGrupo = new ArrayList<>();
        ArrayList<Amigos> auxAmigos = new ArrayList<>();

        for (Amigos amigo : todosAmigos) {
            if(amigo.getUsername().toLowerCase().startsWith(newText.toLowerCase())){
                auxAmigos.add(amigo);
            }
        }

        for (Grupo grupo : todosGrupos) {
            if(grupo.getNombreGrupo().toLowerCase().startsWith(newText.toLowerCase())){
                auxGrupo.add(grupo);
            }
        }

        binding.rvAmigosBuscador.setAdapter(new SocialAdapter(auxAmigos, this));
        binding.rvGruposBuscador.setAdapter(new SocialAdapter(auxGrupo, this));

    }

    private void cargarAmigos() {
        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if(amigosUser.contains(data.child("id").getValue().toString())){
                        String username = data.child("username").getValue(String.class);
                        String fotoPerfil = data.child("foto_perfil").getValue(String.class);
                        amigosList.add(new Amigos(username, fotoPerfil != null ? fotoPerfil : ""));
                    }
                }


                // CONTROLAR VISIBILIDAD
                if (amigosList.isEmpty()) {
                    binding.tvNoAmigos.setVisibility(View.VISIBLE);
                    binding.rvAmigos.setVisibility(View.GONE);
                } else {
                    amigosAdapter.setListaSocial(amigosList);
                    binding.tvNoAmigos.setVisibility(View.GONE);
                    binding.rvAmigos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar amigos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarGrupos() {
        gruposRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gruposList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if(gruposUser.contains(data.child("id").getValue().toString())){
                        String idGrupo = data.getKey();
                        String nombreGrupo = data.child("titulo").getValue(String.class);
                        String fotoGrupo = data.child("foto_grupo").getValue(String.class);
                        gruposList.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo));
                    }
                }

                // CONTROLAR VISIBILIDAD
                if (gruposList.isEmpty()) {
                    binding.tvNoGrupos.setVisibility(View.VISIBLE);
                    binding.rvGrupos.setVisibility(View.GONE);
                } else {
                    gruposAdapter.setListaSocial(gruposList);
                    binding.tvNoGrupos.setVisibility(View.GONE);
                    binding.rvGrupos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar grupos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTodosAmigos() {
        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todosAmigos.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);
                    todosAmigos.add(new Amigos(username, fotoPerfil != null ? fotoPerfil : ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar amigos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTodosGrupos() {
        gruposRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todosGrupos.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String idGrupo = data.getKey();
                    String nombreGrupo = data.child("titulo").getValue(String.class);
                    String fotoGrupo = data.child("foto_grupo").getValue(String.class);

                    todosGrupos.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar grupos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package dam.pmdm.a101pipas.social;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private List<Amigos> amigosList;
    private List<Grupo> gruposList;

    private DatabaseReference amigosRef, gruposRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias en Firebase
        amigosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        gruposRef = FirebaseDatabase.getInstance().getReference("grupos");

        // Inicializar listas
        amigosList = new ArrayList<>();
        gruposList = new ArrayList<>();

        // Inicializar adaptadores
        amigosAdapter = new SocialAdapter(amigosList, this);
        gruposAdapter = new SocialAdapter(gruposList, this);

        // Configurar RecyclerViews
        binding.rvAmigos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvGrupos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        binding.rvAmigos.setAdapter(amigosAdapter);
        binding.rvGrupos.setAdapter(gruposAdapter);

        // Cargar datos desde Firebase
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
    }

    private void cargarAmigos() {
        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);
                    amigosList.add(new Amigos(username, fotoPerfil != null ? fotoPerfil : ""));
                }
                amigosAdapter.setListaSocial(amigosList);
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

                    String idGrupo = data.getKey();
                    String nombreGrupo = data.child("titulo").getValue(String.class);
                    String fotoGrupo = data.child("foto_grupo").getValue(String.class);

                    gruposList.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo));

                }
                gruposAdapter.setListaSocial(gruposList);
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
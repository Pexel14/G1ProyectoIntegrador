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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentListaAmigosBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasFragment;
import dam.pmdm.a101pipas.models.Amigos;

public class ListaAmigosFragment extends Fragment {

    private static String amigos;

    private AmigosAdapter amigosAdapter;
    private List<Amigos> amigosList;
    private DatabaseReference dbRef;

    private FirebaseAuth mAuth;

    private FragmentListaAmigosBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListaAmigosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference("usuarios"); // Cambiamos Firestore por Realtime Database

        amigosList = new ArrayList<>();
        amigosAdapter = new AmigosAdapter(amigosList, this);
        binding.rvListaAmigos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListaAmigos.setAdapter(amigosAdapter);

        // Cargar amigos desde Firebase Realtime Database
        getAmigosUsuario();
        cargarListaAmigosDesdeFirebase();

        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_social));

        binding.barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);

                if(amigosList.isEmpty()){
                    binding.tvMensajeAmigosVacio.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });
    }

    private void getAmigosUsuario() {
        String id = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".","");
        dbRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigos = snapshot.child("amigos").getValue(String.class);
                amigos += "," + snapshot.child("id").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscar(String newText) {
        if(!newText.isEmpty()){
            ArrayList<Amigos> auxAmigos = new ArrayList<>();
            for (Amigos amigo : amigosList) {
                if(amigo.getUsername().toLowerCase().startsWith(newText.toLowerCase())){
                    if(!amigos.contains(amigo.getId())){
                        auxAmigos.add(amigo);
                    }
                }
            }
            if(auxAmigos.isEmpty()){
                binding.tvMensajeAmigosVacio.setVisibility(View.VISIBLE);
            } else {
                binding.tvMensajeAmigosVacio.setVisibility(View.GONE);
            }

            binding.rvListaAmigos.setAdapter(new AmigosAdapter(auxAmigos, this));
        } else {
            amigosAdapter.setAmigosList(amigosList);
            binding.rvListaAmigos.setAdapter(amigosAdapter);
        }

    }

    private void cargarListaAmigosDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear(); // Limpiar lista antes de agregar datos nuevos

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String email = data.child("email").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);
                    String codigo = data.child("id").getValue().toString();
                    String id = data.getKey();
                    if (username != null && fotoPerfil != null) {
                        if(amigos != null){
                            if(!amigos.contains(codigo)){
                                amigosList.add(new Amigos(id, username, fotoPerfil, email));
                            }
                        }
                    }
                }

                if(amigosList.isEmpty()){
                    binding.tvMensajeAmigosVacio.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMensajeAmigosVacio.setVisibility(View.GONE);
                }

                amigosAdapter.setAmigosList(amigosList); // Actualizar adaptador con la lista de amigos
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

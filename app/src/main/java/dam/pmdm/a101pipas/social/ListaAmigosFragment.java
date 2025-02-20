package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentListaAmigosBinding;
import dam.pmdm.a101pipas.models.Amigos;

public class ListaAmigosFragment extends Fragment {

    private AmigosAdapter amigosAdapter;
    private List<Amigos> amigosList;
    private DatabaseReference dbRef;

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

        dbRef = FirebaseDatabase.getInstance().getReference("usuarios"); // Cambiamos Firestore por Realtime Database

        amigosList = new ArrayList<>();
        amigosAdapter = new AmigosAdapter(amigosList);
        binding.rvListaAmigos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListaAmigos.setAdapter(amigosAdapter);

        // Cargar amigos desde Firebase Realtime Database
        cargarListaAmigosDesdeFirebase();

        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_social));
    }

    private void cargarListaAmigosDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear(); // Limpiar lista antes de agregar datos nuevos

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);

                    if (username != null && fotoPerfil != null) {
                        amigosList.add(new Amigos(username, fotoPerfil));
                    }
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

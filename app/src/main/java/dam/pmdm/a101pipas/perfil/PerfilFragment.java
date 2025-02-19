package dam.pmdm.a101pipas.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentGeolocalizacionBinding;
import dam.pmdm.a101pipas.databinding.FragmentPerfilBinding;
import dam.pmdm.a101pipas.models.Desafio;

public class PerfilFragment extends Fragment {

    // TODO: Colocar texto info usuario del singleton

    // Inicalizamos RecyclerView
    private RecyclerView recyclerView;

    // Creamos un adaptador
    private DesafioPerfilRecyclerAdapter adapter;

    // Creamos la lista de usuarios
    private List<Desafio> desafioList;

    // Inicializamos una instancia de DatabaseReference
    private DatabaseReference ref;

    private FirebaseUser usuarioActual;

    private String usuarioId;

    private FirebaseAuth mAuth;

    private String usuarioId;

    private FragmentPerfilBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioActual != null ? usuarioActual.getUid() : null;

        binding.txtNombre.setText(usuarioActual.getDisplayName());

        binding.ibtnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_ajustes);
            }
        });

        binding.rvDesafios.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lista de elementos del RV
        desafioList = new ArrayList<>();

        // Configuramos el adaptador y asignamos al RV
        adapter = new DesafioPerfilRecyclerAdapter(new ArrayList<>()); // Se inicializa vacío
        binding.rvDesafios.setAdapter(adapter);

        binding.tbtnDesafiosCompletados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarDesafiosCompletados();
            }
        });

        // Obtenemos los datos de Firebase RealtimeDatabase
        ref = FirebaseDatabase.getInstance().getReference("desafios");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                desafioList.clear();
                List<Desafio> desafiosFiltrados = new ArrayList<>(); // Lista para mostrar solo los filtrados dependiendo del botón pulsado
                String title;
                Desafio desafio;

                for (DataSnapshot data : snapshot.getChildren()) {
                    title = data.child("titulo").getValue(String.class);

                    // Generar un número random entre 99 y 100 (50/50 de que esté completado)
                    int num = (int) (Math.random() * (101-99)) + 99;

                    desafio = new Desafio(title, num);
                    desafioList.add(desafio); // Se guarda en la lista completa

                    // Filtrar según el botón seleccionado
                    if (binding.tbtnDesafiosEmpezados.isChecked() && num != 100) {
                        desafiosFiltrados.add(desafio);
                    } else if (binding.tbtnDesafiosCompletados.isChecked() && num == 100) {
                        desafiosFiltrados.add(desafio);
                    }
                }

                // Actualizar el adaptador con la lista filtrada
                adapter.actualizarLista(desafiosFiltrados);

                // Dependiendo del botón pulsado, se muestra una cosa u otra
                if (desafiosFiltrados.isEmpty()) {
                    if (tbtnDesafiosEmpezados.isChecked()) {
                        tvNoDesafiosComenzados.setVisibility(View.VISIBLE);
                    } else {
                        tvNoDesafiosTerminados.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoDesafiosComenzados.setVisibility(View.GONE);
                    tvNoDesafiosTerminados.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error rv perfil desafios" + error.getMessage());
            }
        });
    }


    private void seleccionarDesafiosEmpezados() {

        tbtnDesafiosEmpezados.setChecked(true);
        tbtnDesafiosCompletados.setChecked(false);

        tvNoDesafiosTerminados.setVisibility(View.GONE);

        obtenerDesafios();
    }

    private void seleccionarDesafiosCompletados() {

        tbtnDesafiosCompletados.setChecked(true);
        tbtnDesafiosEmpezados.setChecked(false);

        tvNoDesafiosComenzados.setVisibility(View.GONE);

        obtenerDesafios();

    }

    private void personalizarNick() {
        // Añadir el usuario actual a 'miembros'
        if (mAuth.getCurrentUser() != null) {
            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                String id = usuario.split("@")[0].replace(".", "");
                tvNick.setText(id);
                tvNombre.setText("@" + id);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
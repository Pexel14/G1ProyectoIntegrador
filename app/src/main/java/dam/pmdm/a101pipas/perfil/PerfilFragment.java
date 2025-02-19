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
import dam.pmdm.a101pipas.models.Desafio;

public class PerfilFragment extends Fragment {

    // TODO: Colocar texto info usuario del singleton

    // Inicalizamos RecyclerView
    RecyclerView recyclerView;

    // Creamos un adaptador
    DesafioPerfilRecyclerAdapter adapter;

    // Creamos la lista de desafíos
    List<Desafio> desafioList;

    // Inicializamos una instancia de DatabaseReference
    DatabaseReference ref;

    FirebaseUser usuarioActual;

    FirebaseAuth mAuth;

    String usuarioId;

    TextView tvNick, tvNombre;

    ImageButton ibtnAjustes;

    ToggleButton tbtnDesafiosEmpezados, tbtnDesafiosCompletados;

    TextView tvNoDesafiosComenzados, tvNoDesafiosTerminados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioActual != null ? usuarioActual.getUid() : null;

        tvNick = view.findViewById(R.id.txt_nick);
        tvNombre = view.findViewById(R.id.txt_nombre);

        mAuth = FirebaseAuth.getInstance();

        personalizarNick();

        ibtnAjustes = view.findViewById(R.id.ibtnAjustes);

//        tvNick.setText(usuarioActual.getDisplayName());

        ibtnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AjustesActivity.class);
                startActivity(i);
            }
        });

        recyclerView = view.findViewById(R.id.rv_desafios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setVisibility(View.GONE);

        tvNoDesafiosComenzados = view.findViewById(R.id.tvNoDesafiosComenzados);
        tvNoDesafiosTerminados = view.findViewById(R.id.tvNoDesafiosTerminados);

        tbtnDesafiosEmpezados = view.findViewById(R.id.tbtnDesafiosEmpezados);
        tbtnDesafiosEmpezados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarDesafiosEmpezados();
            }
        });

        tbtnDesafiosCompletados = view.findViewById(R.id.tbtnDesafiosCompletados);
        tbtnDesafiosCompletados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarDesafiosCompletados();
            }
        });

        seleccionarDesafiosEmpezados();

        return view;

    }

    private void obtenerDesafios() {
        // Lista de elementos del RV
        desafioList = new ArrayList<>();

        // Configuramos el adaptador y asignamos al RV
        adapter = new DesafioPerfilRecyclerAdapter(new ArrayList<>()); // Se inicializa vacío
        recyclerView.setAdapter(adapter);

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
                    if (tbtnDesafiosEmpezados.isChecked() && num != 100) {
                        desafiosFiltrados.add(desafio);
                    } else if (tbtnDesafiosCompletados.isChecked() && num == 100) {
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

//        if (desafioList.isEmpty()) {
//            tvNoDesafiosComenzados.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//        } else {
//            tvNoDesafiosComenzados.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//        }

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

}
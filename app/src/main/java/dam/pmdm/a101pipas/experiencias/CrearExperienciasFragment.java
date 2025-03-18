package dam.pmdm.a101pipas.experiencias;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

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
import dam.pmdm.a101pipas.databinding.FragmentCrearExperienciasBinding;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.Experiencia;

public class CrearExperienciasFragment extends Fragment {

    private List<TarjetaExperienciaFragment> listaDeFragments = new ArrayList<>();
    private FragmentManager fragmentManager;
    private FragmentCrearExperienciasBinding binding;

    private Long ultimoIdExp = 0L;

    private DatabaseReference refDesafios, refExperiencias;
    private Desafio desafio;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearExperienciasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Inicializar el FragmentManager
        fragmentManager = getChildFragmentManager();

        // 🔹 Inicializar Firebase
        refDesafios = FirebaseDatabase.getInstance().getReference("desafios");
        refExperiencias = FirebaseDatabase.getInstance().getReference("experiencias");

        // 🔹 Obtener objeto Desafio del Intent
        // Recuperar el objeto del Bundle
        if (getArguments() != null) {
            desafio = (Desafio) getArguments().getSerializable("Desafio");
        }

        // 🔹 Añadir un fragmento inicial
        agregarNuevoFragment();

        binding.btnAtrasCrearExperiencias.setOnClickListener(v -> confirmarVolverAtras(view));

        binding.btnAniadirExperienciaCrearDesafio.setOnClickListener(v -> {
            int ultimoFragment = listaDeFragments.size() - 1;
            if (ultimoFragment >= 0 && listaDeFragments.get(ultimoFragment).isEmpty()) {
                Toast.makeText(getContext(), R.string.crear_experiencias_rellena_todos_los_campos, Toast.LENGTH_SHORT).show();
            } else {
                agregarNuevoFragment();
            }
        });

        binding.btnCrearDesafio.setOnClickListener(v -> {

            // Primero obtenemos el último id de experiencias
            refExperiencias.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dsExpereincia : snapshot.getChildren()) {
                        Experiencia exp = dsExpereincia.getValue(Experiencia.class);
                        if (exp.getId() > ultimoIdExp) {
                            ultimoIdExp = exp.getId();
                        }
                    }

                    ultimoIdExp += 1;

                    obtenerIdDesafio(view);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void obtenerIdDesafio(@NonNull View view) {

        // Luego se obtiene el id para el desafío
        refDesafios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Long minId = 0L;

                for (DataSnapshot dsDesafio : snapshot.getChildren()) {
                    Desafio des = dsDesafio.getValue(Desafio.class);
                    if (des.getId() > minId) {
                        minId = des.getId();
                    }
                }

                minId += 1;
                desafio.setId(minId);

                guardarDesafioEnFirebase(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * 🔹 Método para confirmar si el usuario quiere salir sin guardar.
     */
    private void confirmarVolverAtras(View view) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.crear_experiencias_no_se_van_a_guardar_exp)
                .setPositiveButton(R.string.crear_experiencias_si, (dialogInterface, i) -> Navigation.findNavController(view).popBackStack())
                .setNegativeButton(R.string.crear_experiencias_no, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    /**
     * 🔹 Método para manejar el botón de retroceso del sistema.
     */
    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        confirmarVolverAtras(getView());
    }

    /**
     * 🔹 Método para agregar un nuevo fragmento de experiencia.
     */
    private void agregarNuevoFragment() {
        TarjetaExperienciaFragment fragment = TarjetaExperienciaFragment.newInstance("", "");
        listaDeFragments.add(fragment);
        fragmentManager
                .beginTransaction()
                .add(R.id.llExperienciasCrearExperiencias, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 🔹 Método para guardar el desafío en Firebase.
     */
    private void guardarDesafioEnFirebase(View view) {
        if (listaDeFragments.isEmpty()) {
            Toast.makeText(getContext(), R.string.crear_experiencias_debe_incluir_una_exp, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Experiencia> listaExperiencias = new ArrayList<>();

        String experiencias = "";
        for (TarjetaExperienciaFragment fragment : listaDeFragments) {
            if (fragment.isEmpty()) {
                Toast.makeText(getContext(), "Hay experiencias sin completar.", Toast.LENGTH_SHORT).show();
                return;
            }
            Experiencia ex = new Experiencia(ultimoIdExp, fragment.getTitulo(), fragment.getDescripcion());

            listaExperiencias.add(ex);

            if (!experiencias.isEmpty()) {
                experiencias += ",";
            }

            experiencias += String.valueOf(ultimoIdExp);

            ultimoIdExp += 1;
        }

        desafio.setExperiencias(experiencias);

        String keyDesafio = desafio.getTitulo();

        refDesafios.child(keyDesafio).setValue(desafio)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.crear_experiencias_desafio_creado, Toast.LENGTH_SHORT).show();
                        crearExperiencias(view, listaExperiencias);
                    } else {
                        Toast.makeText(getContext(), R.string.crear_experiencias_error_al_crear_desafio, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearExperiencias(View view, List<Experiencia> listaExp) {

        for (Experiencia exp : listaExp) {
            refExperiencias.child(String.valueOf(exp.getId())).setValue(exp);
        }

        Navigation.findNavController(view).navigate(R.id.navigation_inicio);
    }
}

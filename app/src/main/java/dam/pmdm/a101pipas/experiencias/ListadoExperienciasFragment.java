package dam.pmdm.a101pipas.experiencias;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentListadoExperienciasBinding;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionViewModel;
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperienciasFragment extends Fragment {

    private GeolocalizacionViewModel geolocalizacionViewModel;
    private ListadoExperienciasViewModel viewModel;

    private static int volverAtras;

    public static void setVolverAtras(int volverAtras) {
        ListadoExperienciasFragment.volverAtras = volverAtras;
    }

    private ExperienciasListAdapter adapter;
    private List<Experiencia> experienciaList;

    private FragmentListadoExperienciasBinding binding;

    private boolean[] desafioEmpezado = {false};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListadoExperienciasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ListadoExperienciasViewModel.class);
        geolocalizacionViewModel = new ViewModelProvider(requireActivity()).get(GeolocalizacionViewModel.class);

        binding.rvExperiencias.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Inicializar lista y adaptador
        experienciaList = new ArrayList<>();
        adapter = new ExperienciasListAdapter(experienciaList, experiencia -> {
            if (experiencia.getLatLng() != null) {
                geolocalizacionViewModel.setDestinoExperiencia(experiencia.getLatLng());

                Navigation.findNavController(view).navigate(R.id.navigation_geolocalizacion);
            }
        });

        binding.rvExperiencias.setAdapter(adapter);

        viewModel.getExperiencias().observe(getViewLifecycleOwner(), experiencias -> {
            if (!experiencias.isEmpty()) {
                adapter.setExperiencias(experiencias);
            }
        });

        viewModel.getProgreso().observe(getViewLifecycleOwner(), this::actualizarProgreso);
        actualizarTitulo(viewModel.getTituloDesafio());

        binding.imgVolverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(volverAtras == 0){
                    Navigation.findNavController(view).navigate(R.id.navigation_inicio);
                } else if(volverAtras == 1){
                    Navigation.findNavController(view).navigate(R.id.navigation_descubrir);
                }

            }
        });

        desafioEmpezado();

//        binding.btnMeterseADesafio.setOnClickListener(v -> {
//            viewModel.aniadirDesafioAUsuario();
//            desafioEmpezado(); // Vuelve a comprobar si el desafio está empezado para mostrar el progreso
//        });

        binding.btnMeterseADesafio.setOnClickListener(v -> {
            viewModel.comenzarDesafioEnUsuario(new ListadoExperienciasViewModel.OnResultListener() {
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        desafioEmpezado();
                    }
                }
            });
        });


    }

    private void desafioEmpezado() {
        viewModel.desafioEmpezado(new ListadoExperienciasViewModel.OnResultListener() {
            @Override
            public void onResult(boolean resultado) {
                // Asignamos el resultado al objeto Boolean
                desafioEmpezado[0] = resultado;

                // Si el desafío está empezado, muestra el progreso, y sino, muestra el botón para unirse
                if (desafioEmpezado[0]) {
                    binding.tvProgress.setVisibility(View.VISIBLE);
                    binding.btnMeterseADesafio.setVisibility(View.GONE);
                } else {
                    binding.btnMeterseADesafio.setVisibility(View.VISIBLE);
                    binding.tvProgress.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean[] getDesafioEmpezado() {
        return desafioEmpezado;
    }

    private void actualizarProgreso(Integer progreso) {
        binding.progressBar.setProgress(progreso);
        binding.tvProgress.setText(getString(R.string.listado_experiencias_tvProgress_1) + " " + progreso + "%");
    }

    private void actualizarTitulo(String titulo) {
        binding.tvTitle.setText(titulo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
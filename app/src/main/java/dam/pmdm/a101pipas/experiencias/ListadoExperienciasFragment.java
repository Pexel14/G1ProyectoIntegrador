package dam.pmdm.a101pipas.experiencias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    private ExperienciasListAdapter adapter;
    private List<Experiencia> experienciaList;
    private FragmentListadoExperienciasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListadoExperienciasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModels();
        setupRecyclerView();

        binding.imgVolverAtras.setOnClickListener(v -> navigateToPreviousScreen());

        // Manejo del botón para unirse al desafío
        binding.btnMeterseADesafio.setOnClickListener(v -> {
            viewModel.aniadirDesafioAUsuario(this::onDesafioAdded);
        });

        // Verifica si el desafío ha comenzado
        verificarDesafioEmpezado();
    }

    private void initializeViewModels() {
        viewModel = new ViewModelProvider(requireActivity()).get(ListadoExperienciasViewModel.class);
        geolocalizacionViewModel = new ViewModelProvider(requireActivity()).get(GeolocalizacionViewModel.class);
    }

    private void setupRecyclerView() {
        experienciaList = new ArrayList<>();
        adapter = new ExperienciasListAdapter(experienciaList, experiencia, tituloDesafio -> {
            if (experiencia.getLatLng() != null) {
                geolocalizacionViewModel.setDestinoExperiencia(experiencia.getLatLng());
                Navigation.findNavController(getView()).navigate(R.id.navigation_geolocalizacion);
            }
        });

        binding.rvExperiencias.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvExperiencias.setAdapter(adapter);

        viewModel.getExperiencias().observe(getViewLifecycleOwner(), experiencias -> {
            if (!experiencias.isEmpty()) {
                adapter.setExperiencias(experiencias);
            }
        });

        viewModel.getProgreso().observe(getViewLifecycleOwner(), this::actualizarProgreso);
        actualizarTitulo(viewModel.getTituloDesafio());
    }

    private void navigateToPreviousScreen() {
        int destinationId = volverAtras == 0 ? R.id.navigation_inicio : R.id.navigation_descubrir;
        Navigation.findNavController(binding.imgVolverAtras).navigate(destinationId);
    }

    private void onDesafioAdded(boolean resultado) {
        if (resultado) {
            verificarDesafioEmpezado(); // Actualiza la UI si el desafío fue agregado correctamente
        }
    }

    private void verificarDesafioEmpezado() {
        viewModel.desafioEmpezado(resultado -> {
            // Actualiza la visibilidad del progreso y el botón para unirse
            binding.tvProgress.setVisibility(resultado ? View.VISIBLE : View.GONE);
            binding.btnMeterseADesafio.setVisibility(resultado ? View.GONE : View.VISIBLE);
        });
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

    public static void setVolverAtras(int volverAtras) {
        ListadoExperienciasFragment.volverAtras = volverAtras;
    }
}
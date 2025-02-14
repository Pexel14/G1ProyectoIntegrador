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
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperienciasFragment extends Fragment {

    private ListadoExperienciasViewModel viewModel;

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

        viewModel = new ViewModelProvider(requireActivity()).get(ListadoExperienciasViewModel.class);

        binding.rvExperiencias.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar lista y adaptador
        experienciaList = new ArrayList<>();
        adapter = new ExperienciasListAdapter(experienciaList);
        binding.rvExperiencias.setAdapter(adapter);

        viewModel.getExperiencias().observe(getViewLifecycleOwner(), experiencias -> {
            adapter.setExperiencias(experiencias);
        });
        viewModel.getProgreso().observe(getViewLifecycleOwner(), this::actualizarProgreso);
        viewModel.getTituloDesafio().observe(getViewLifecycleOwner(), this::actualizarTitulo);

        binding.imgVolverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_inicio);
            }
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
}
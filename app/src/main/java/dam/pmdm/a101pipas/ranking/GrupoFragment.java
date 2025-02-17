package dam.pmdm.a101pipas.ranking;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dam.pmdm.a101pipas.databinding.FragmentGrupoBinding;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionViewModel;

public class GrupoFragment extends Fragment {

    private GrupoViewModel viewModel;

    private FragmentGrupoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrupoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);

        viewModel.getGrupo().observe(getViewLifecycleOwner(), grupo -> {
            if (grupo != null) {
                binding.imgCompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        StringBuilder message = new StringBuilder();
                        message.append("¡Únete a mi grupo en 101!\n");

                        message.append("Nombre del grupo: ").append(grupo.getTitulo()).append("\n");

                        if (grupo.getContrasena() != null) {
                            message.append("Contraseña: ").append(grupo.getContrasena());
                        }

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message.toString());

                        startActivity(Intent.createChooser(shareIntent, "Compartir a través de"));
                    }
                });
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
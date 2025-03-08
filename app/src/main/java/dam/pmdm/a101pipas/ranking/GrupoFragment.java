package dam.pmdm.a101pipas.ranking;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dam.pmdm.a101pipas.R;
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
            Log.d("Compartir","TOY AQUI 2");
            if (grupo != null) {
                binding.imgCompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Compartir","TOY AQUI");
                        StringBuilder message = new StringBuilder();

                        message.append(getString(R.string.grupo_fragment_mensaje_invitacion_1))
                                .append("\n");

                        message.append(getString(R.string.grupo_fragment_mensaje_invitacion_2))
                                .append(grupo.getTitulo())
                                .append(getString(R.string.grupo_fragment_mensaje_invitacion_3))
                                .append("\n");

                        if (grupo.getContrasena() != null) {
                            message.append(getString(R.string.grupo_fragment_mensaje_invitacion_4))
                                    .append(grupo.getContrasena())
                                    .append("\n");
                        }

                        message.append(getString(R.string.grupo_fragment_mensaje_invitacion_5));

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message.toString());

                        startActivity(Intent.createChooser(shareIntent, getString(R.string.grupo_fragment_compartir_a_traves)));
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
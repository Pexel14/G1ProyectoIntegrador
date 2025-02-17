package dam.pmdm.a101pipas.social;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentGrupoBinding;
import dam.pmdm.a101pipas.databinding.FragmentSocialBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasViewModel;
import dam.pmdm.a101pipas.ranking.GrupoViewModel;


public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GrupoViewModel grupoViewModel
                = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);

        binding.btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grupoViewModel.setIdGrupo(1);
                Navigation.findNavController(view).navigate(R.id.navigation_grupo);
            }
        });
    }
}
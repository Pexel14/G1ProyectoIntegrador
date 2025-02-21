package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentPerfilAmigoBinding;
import dam.pmdm.a101pipas.models.Amigos;

public class PerfilAmigoFragment extends Fragment {

    private FragmentPerfilAmigoBinding binding;
    private AmigosViewModel amigosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilAmigoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        amigosViewModel = new ViewModelProvider(requireActivity()).get(AmigosViewModel.class);

        amigosViewModel.getAmigosList().observe(getViewLifecycleOwner(), amigos -> {
            Amigos amigo = amigos.get(0);
            binding.txtNombre.setText(amigo.getUsername());
            Picasso.get().load(amigo.getFotoPerfil()).into(binding.imgAvatar);
        });

        binding.ibtnAtras.setOnClickListener(v-> {
            Navigation.findNavController(view).navigate(R.id.navigation_social);
        });
    }
}

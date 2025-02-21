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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentSocialBinding;
import dam.pmdm.a101pipas.models.Amigos;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;
    private SocialAdapter socialAdapter;
    private List<Amigos> amigosList;
    private AmigosViewModel amigosViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        amigosList = new ArrayList<>();
        socialAdapter = new SocialAdapter(amigosList); // Usamos SocialAdapter en lugar de AmigosAdapter

        // Configurar RecyclerView con SocialAdapter
        binding.rvAmigos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAmigos.setAdapter(socialAdapter);

        // Obtener ViewModel
        amigosViewModel = new ViewModelProvider(requireActivity()).get(AmigosViewModel.class);

        // Observar cambios en la lista de amigos y actualizar SocialAdapter
        amigosViewModel.getAmigosList().observe(getViewLifecycleOwner(), amigos -> {
            socialAdapter.setListaSocial(amigos); // Pasamos la lista de amigos a SocialAdapter
        });

        // Botón para ver más amigos
        binding.btnMasAmigos.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_lista_amigos));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

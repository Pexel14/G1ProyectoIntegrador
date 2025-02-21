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

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentListaAmigosBinding;

public class ListaAmigosFragment extends Fragment {

    private FragmentListaAmigosBinding binding;
    private AmigosAdapter amigosAdapter;
    private AmigosViewModel amigosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListaAmigosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        amigosAdapter = new AmigosAdapter();
        binding.rvListaAmigos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListaAmigos.setAdapter(amigosAdapter);

        amigosViewModel = new ViewModelProvider(requireActivity()).get(AmigosViewModel.class);

        amigosViewModel.getAmigosList().observe(getViewLifecycleOwner(), amigos -> {
            amigosAdapter.setAmigosList(amigos);
        });

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.navigation_social));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

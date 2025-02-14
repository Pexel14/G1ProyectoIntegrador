package dam.pmdm.a101pipas.ranking;

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

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentRankingGlobalBinding;

public class RankingGlobalFragment extends Fragment {

    private RankingRecyclerAdapter adapter;

    private FragmentRankingGlobalBinding binding;
    private RankingGlobalViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRankingGlobalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvRanking.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RankingRecyclerAdapter(new ArrayList<>());
        binding.rvRanking.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(RankingGlobalViewModel.class);
        viewModel.usuarios.observe(getViewLifecycleOwner(), listaUsuarios -> {
            adapter.actualizarRanking(listaUsuarios);
        });

        binding.imgBackRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_inicio);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


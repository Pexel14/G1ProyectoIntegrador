package dam.pmdm.a101pipas.desafios.descubrir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.databinding.FragmentDescubrirBinding;
import dam.pmdm.a101pipas.models.Desafio;

public class DescubrirFragment extends Fragment {

    private FragmentDescubrirBinding binding;

    private DescubrirAdapter adapterFiltro1, adapterFiltro2, adapterFiltro3, adapterFiltro4, adapterTodos;
    private DescubrirViewModel descubrirViewModel;

    private List<Desafio> listaTodosDesafios, listaDesafios1, listaDesafios2, listaDesafios3, listaDesafios4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDescubrirBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvDescubrirTodos.setVisibility(View.GONE);
        
        listaTodosDesafios = new ArrayList<>();
        listaDesafios1 = new ArrayList<>();
        listaDesafios2 = new ArrayList<>();
        listaDesafios3 = new ArrayList<>();
        listaDesafios4 = new ArrayList<>();

        adapterTodos = new DescubrirAdapter(listaTodosDesafios, this);
        adapterFiltro1 = new DescubrirAdapter(listaDesafios1, this);
        adapterFiltro2 = new DescubrirAdapter(listaDesafios2, this);
        adapterFiltro3 = new DescubrirAdapter(listaDesafios3, this);
        adapterFiltro4 = new DescubrirAdapter(listaDesafios4, this);

        binding.rvDescubrirTodos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvDescubrir1.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvDescubrir2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvDescubrir3.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvDescubrir4.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        binding.rvDescubrirTodos.setAdapter(adapterTodos);
        binding.rvDescubrir1.setAdapter(adapterFiltro1);
        binding.rvDescubrir2.setAdapter(adapterFiltro2);
        binding.rvDescubrir3.setAdapter(adapterFiltro3);
        binding.rvDescubrir4.setAdapter(adapterFiltro4);

        descubrirViewModel = new ViewModelProvider(requireActivity()).get(DescubrirViewModel.class);

        descubrirViewModel.getFragmentosList().observe(getViewLifecycleOwner(), this::cargarDesafios);
        descubrirViewModel.cargarFragmentos();

        binding.barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                if(!newText.isEmpty()){
                    binding.rvDescubrir1.setVisibility(View.GONE);
                    binding.rvDescubrir2.setVisibility(View.GONE);
                    binding.rvDescubrir3.setVisibility(View.GONE);
                    binding.rvDescubrir4.setVisibility(View.GONE);
                    binding.tvFiltro1Descubrir.setVisibility(View.GONE);
                    binding.tvFiltro2Descubrir.setVisibility(View.GONE);
                    binding.tvFiltro3Descubrir.setVisibility(View.GONE);
                    binding.tvFiltro4Descubrir.setVisibility(View.GONE);
                    binding.rvDescubrirTodos.setVisibility(View.VISIBLE);
                } else {
                    binding.rvDescubrir1.setVisibility(View.VISIBLE);
                    binding.rvDescubrir2.setVisibility(View.VISIBLE);
                    binding.rvDescubrir3.setVisibility(View.VISIBLE);
                    binding.rvDescubrir4.setVisibility(View.VISIBLE);
                    binding.tvFiltro1Descubrir.setVisibility(View.VISIBLE);
                    binding.tvFiltro2Descubrir.setVisibility(View.VISIBLE);
                    binding.tvFiltro3Descubrir.setVisibility(View.VISIBLE);
                    binding.tvFiltro4Descubrir.setVisibility(View.VISIBLE);
                    binding.rvDescubrirTodos.setVisibility(View.GONE);
                }
                return true;
            }
        });

    }

    private void buscar(String newText) {
        ArrayList<Desafio> auxDesafios = new ArrayList<>();

        for (Desafio desafio : listaTodosDesafios){
            if(desafio.getTitulo().toLowerCase().contains(newText.toLowerCase())){
                auxDesafios.add(desafio);
            }
        }

        binding.rvDescubrirTodos.setAdapter(new DescubrirAdapter(auxDesafios, this));

    }

    private void cargarDesafios(List<Desafio> desafios) {
        listaDesafios1.clear();
        listaDesafios2.clear();
        listaDesafios3.clear();
        listaDesafios4.clear();
        listaTodosDesafios.clear();
        if(!desafios.isEmpty()){
            for(Desafio desafio : desafios){
                listaTodosDesafios.add(desafio);
                if(desafio.getEtiquetas().contains(binding.tvFiltro1Descubrir.getText().toString())){
                    listaDesafios1.add(desafio);
                }

                 if(desafio.getEtiquetas().contains(binding.tvFiltro2Descubrir.getText().toString())){
                    listaDesafios2.add(desafio);
                }

                 if(desafio.getEtiquetas().contains(binding.tvFiltro3Descubrir.getText().toString())){
                    listaDesafios3.add(desafio);
                }

                 if(desafio.getEtiquetas().contains(binding.tvFiltro4Descubrir.getText().toString())){
                    listaDesafios4.add(desafio);
                }
            }

            binding.rvDescubrir1.setAdapter(new DescubrirAdapter(listaDesafios1, this));
            binding.rvDescubrir2.setAdapter(new DescubrirAdapter(listaDesafios2, this));
            binding.rvDescubrir3.setAdapter(new DescubrirAdapter(listaDesafios3, this));
            binding.rvDescubrir4.setAdapter(new DescubrirAdapter(listaDesafios4, this));

        }

    }

}

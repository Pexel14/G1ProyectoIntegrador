package dam.pmdm.a101pipas.desafios.inicio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentInicioBinding;
import dam.pmdm.a101pipas.desafios.CrearDesafioActivity;
import dam.pmdm.a101pipas.models.Desafio;

public class InicioFragment extends Fragment {

    private DatabaseReference refDesafiosUsuario;
    private DatabaseReference refDesafios;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;

    private FragmentInicioBinding binding;
    private InicioAdapter adapter;
    private InicioViewModel inicioViewModel;

    private List<Desafio> listaDesafios;

    private String usuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicioViewModel = new ViewModelProvider(this).get(InicioViewModel.class);

        // TODO: INTENTAR SACARLO DE UN VIEWMODEL NO DE UN SHAREDPREFERENCES
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        usuario = sharedPreferences.getString("usuario", "usuario");

        firebase = FirebaseDatabase.getInstance(); // Inicializa Firebase correctamente
        refDesafiosUsuario = firebase.getReference("usuarios").child(usuario).child("desafios"); // Apunta a los desafíos del usuario
        refDesafios = firebase.getReference("desafios");

        listaDesafios = new ArrayList<>();
        adapter = new InicioAdapter(listaDesafios, this);

        binding.rvDesafiosInicio.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvDesafiosInicio.setAdapter(adapter);

        inicioViewModel.getDesafiosLiveData().observe(getViewLifecycleOwner(), this::cargarDesafios);
        inicioViewModel.cargarFragmentosDesafiosDesdeFirebase(refDesafiosUsuario, refDesafios);

        binding.btnAniadirDesafioInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CrearDesafioActivity.class);
                startActivity(i);
            }
        });

        binding.svBuscarInicio.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                return true;
            }
        });

    }

    private void buscar(String newText) {
        if(!newText.isEmpty()){
            ArrayList<Desafio> auxDesafio = new ArrayList<>();

            for (Desafio desafio : listaDesafios) {
                if(desafio.getTitulo().toLowerCase().contains(newText.toLowerCase())){
                    auxDesafio.add(desafio);
                }
            }

            binding.rvDesafiosInicio.setAdapter(new InicioAdapter(auxDesafio, this));

            if(auxDesafio.isEmpty()){
                mostrarMensajeCeroDesafios(false);
            } else {
                mostrarMensajeCeroDesafios(true);
            }
        } else {
            adapter.setListaDesafios(listaDesafios);
            binding.rvDesafiosInicio.setAdapter(adapter);
        }
    }

    private void cargarDesafios(List<Desafio> fragmentos) {
        if(!fragmentos.isEmpty()){
            listaDesafios = fragmentos;
            adapter.setListaDesafios(listaDesafios);
        } else {
            mostrarMensajeCeroDesafios(false);
        }
    }

    private void mostrarMensajeCeroDesafios(boolean hay) {
        if (!hay) {
            binding.tvMensajeCeroFragmentsInicio.setVisibility(View.VISIBLE);
        } else {
            binding.tvMensajeCeroFragmentsInicio.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
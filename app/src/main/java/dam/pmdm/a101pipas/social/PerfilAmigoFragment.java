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

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentPerfilAmigoBinding;
import dam.pmdm.a101pipas.models.Amigos;
import dam.pmdm.a101pipas.perfil.DesafioPerfilAdapter;
import dam.pmdm.a101pipas.perfil.EditarPerfilFragment;
import dam.pmdm.a101pipas.perfil.PerfilViewModel;

public class PerfilAmigoFragment extends Fragment {

    private FragmentPerfilAmigoBinding binding;
    private DesafioPerfilAdapter adapter;
    private PerfilViewModel viewModel;

    private static int tipo;

    public static void setTipo(int tipo) {
        PerfilAmigoFragment.tipo = tipo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilAmigoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

        adapter = new DesafioPerfilAdapter(new ArrayList<>());
        binding.rvDesafios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDesafios.setAdapter(adapter);

        binding.btnAtrasAmigo.setOnClickListener(v -> {
            if(tipo == 0){
                Navigation.findNavController(v).navigate(R.id.navigation_social);
            } else if (tipo == 1){
                Navigation.findNavController(v).navigate(R.id.navigation_lista_amigos);
            }
        });

        viewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.txtNick.setText(user.getUsername());

                if(!user.getFoto_perfil().isEmpty()){
                    Picasso.get()
                            .load(user.getFoto_perfil())
                            .placeholder(R.drawable.perfil_por_defecto)
                            .fit().centerCrop()
                            .into(binding.imgAvatar);
                }

                binding.txtNombre.setText("@" + user.getEmail().split("@")[0].replace(".", ""));
            }
        });

        viewModel.getDesafiosLiveData().observe(getViewLifecycleOwner(), desafios -> {
            adapter.actualizarLista(desafios);
            actualizarVisibilidad(desafios.isEmpty());
        });

        binding.tbtnDesafiosCompletados.setOnClickListener(v -> viewModel.seleccionarDesafiosCompletados());
        binding.tbtnDesafiosEmpezados.setOnClickListener(v -> viewModel.seleccionarDesafiosEmpezados());

    }

    private void actualizarVisibilidad(boolean listaVacia) {
        binding.rvDesafios.setVisibility(listaVacia ? View.GONE : View.VISIBLE);
        binding.tvNoDesafiosComenzados.setVisibility(
                listaVacia && binding.tbtnDesafiosEmpezados.isChecked() ? View.VISIBLE : View.GONE
        );
        binding.tvNoDesafiosTerminados.setVisibility(
                listaVacia && binding.tbtnDesafiosCompletados.isChecked() ? View.VISIBLE : View.GONE
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

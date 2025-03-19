package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import dam.pmdm.a101pipas.models.User;
import dam.pmdm.a101pipas.perfil.DesafioPerfilAdapter;
import dam.pmdm.a101pipas.perfil.EditarPerfilFragment;
import dam.pmdm.a101pipas.perfil.PerfilViewModel;

public class PerfilAmigoFragment extends Fragment {

    private FragmentPerfilAmigoBinding binding;
    private DesafioPerfilAdapter adapter;
    private PerfilViewModel viewModel;

    private static int tipo;
    private FirebaseAuth mAuth;
    private User amigo;

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
        mAuth = FirebaseAuth.getInstance();
        String usuarioActual = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");

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
                amigo = user;
                binding.txtNick.setText(user.getUsername());

                if(!user.getFoto_perfil().isEmpty()){
                    Picasso.get()
                            .load(user.getFoto_perfil())
                            .placeholder(R.drawable.perfil_por_defecto)
                            .fit().centerCrop()
                            .into(binding.imgAvatar);
                }

                binding.txtNombre.setText("@" + user.getEmail().split("@")[0].replace(".", ""));

                viewModel.getEsAmigoLiveData().observe(getViewLifecycleOwner(), esAmigo -> {
                    if (esAmigo) {
                        Log.d("PerfilAmigoFragment", "Es amigo");
                        binding.ibAgregarAmigo.setVisibility(View.GONE);
                    } else {
                        Log.d("PerfilAmigoFragment", "No es amigo");
                    }
                });

                Log.d("PerfilAmigoFragment", "amigo: " + user.getEmail().split("@")[0].replace(".", ""));
                viewModel.esAmigo(usuarioActual, user.getEmail().split("@")[0].replace(".", ""));
            }
        });

        viewModel.getDesafiosLiveData().observe(getViewLifecycleOwner(), desafios -> {
            adapter.actualizarLista(desafios);
            actualizarVisibilidad(desafios.isEmpty());
        });

        binding.tbtnDesafiosCompletados.setOnClickListener(v -> viewModel.seleccionarDesafiosCompletados());
        binding.tbtnDesafiosEmpezados.setOnClickListener(v -> viewModel.seleccionarDesafiosEmpezados());

        binding.ibAgregarAmigo.setOnClickListener(v -> {
            viewModel.getAgregadoLiveData().observe(getViewLifecycleOwner(), agregado -> {
                if (agregado) {
                    Toast.makeText(getContext(), "Amigo agregado", Toast.LENGTH_SHORT).show();
                    binding.ibAgregarAmigo.setVisibility(View.GONE);
                }
            });

            viewModel.agregarAmigo(usuarioActual, amigo.getEmail().split("@")[0].replace(".", ""));
        });

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

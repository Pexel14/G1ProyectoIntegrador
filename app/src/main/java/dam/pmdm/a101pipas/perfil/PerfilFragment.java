package dam.pmdm.a101pipas.perfil;

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
import dam.pmdm.a101pipas.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;
    private DesafioPerfilAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

        adapter = new DesafioPerfilAdapter(new ArrayList<>());
        binding.rvDesafios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDesafios.setAdapter(adapter);

        viewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvNick.setText(user.getUsername());

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
        binding.btnEditar.setOnClickListener(v ->  {
            EditarPerfilFragment editBottomSheet = new EditarPerfilFragment();
            editBottomSheet.setOnProfileUpdatedListener(this::refresh);
            editBottomSheet.show(getParentFragmentManager(), editBottomSheet.getTag());
        });
        binding.ibtnAjustes.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_ajustes));
    }

    private void refresh() {
        viewModel.cargarUsuario();
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
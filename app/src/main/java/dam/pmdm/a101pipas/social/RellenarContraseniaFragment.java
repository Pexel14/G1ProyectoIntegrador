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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentRellenarContraseniaBinding;
import dam.pmdm.a101pipas.ranking.GrupoViewModel;

public class RellenarContraseniaFragment extends BottomSheetDialogFragment {

    private FragmentRellenarContraseniaBinding binding;

    private String contrasenia;
    private String idGrupo;
    private Fragment fragment;
    private GrupoDetailViewModel viewModel;

    private FirebaseAuth mAuth;

    public RellenarContraseniaFragment(String contrasenia, String idGrupo, Fragment fragment, GrupoDetailViewModel viewModel) {
        this.contrasenia = contrasenia;
        this.idGrupo = idGrupo;
        this.fragment = fragment;
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRellenarContraseniaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.btnGuardarCambios.setOnClickListener(v -> {
            String cont = binding.etUsername.getText().toString().trim();

            if(cont.equals(contrasenia)){
                viewModel.aniadirGrupoUser(mAuth.getCurrentUser().getEmail().split("@")[0].replace(".",""), idGrupo).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()){
                                GrupoViewModel grupoViewModel = new ViewModelProvider(fragment.requireActivity()).get(GrupoViewModel.class);
                                grupoViewModel.setIdGrupo(Integer.parseInt(idGrupo));
                                dismiss();
                                Toast.makeText(getContext(), R.string.grupo_detail_fragment_unirse_exitoso, Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(fragment.requireView()).navigate(R.id.navigation_grupo);
                            }
                        }
                    }
                });

            } else {
                Toast.makeText(getContext(), R.string.rellenar_contrasenia_fragment_contrasenia_incorrecta, Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
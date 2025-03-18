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
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentGrupoDetailBinding;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.ranking.GrupoViewModel;

public class GrupoDetailFragment extends Fragment {

    private static String grupoID;

    public static void setGrupoID(String grupoID) {
        GrupoDetailFragment.grupoID = grupoID;
    }

    private FragmentGrupoDetailBinding binding;

    private GrupoDetailViewModel viewModel;

    private static boolean correcto;

    public static void setCorrecto(boolean correcto) {
        GrupoDetailFragment.correcto = correcto;
    }

    private FirebaseAuth mAuth;
    private Grupo grupo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrupoDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        viewModel = new ViewModelProvider(requireActivity()).get(GrupoDetailViewModel.class);

        viewModel.cargarDatosGrupo(grupoID).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    grupo = viewModel.getGrupo();

                    if(grupo != null){
                        binding.tvCreador.setText(grupo.getCreador() + "");
                        binding.tvNombreGrupo.setText(grupo.getTitulo() + "");
                        binding.tvMiembros.setText(grupo.getMiembros().split(",").length + getString(R.string.grupo_detail_fragment_aventureros));

                        if(!grupo.getFoto_grupo().isEmpty()){
                            Picasso.get()
                                    .load(grupo.getFoto_grupo())
                                    .placeholder(R.drawable.perfil_por_defecto)
                                    .into(binding.imgGrupo);
                        }

                        binding.tvDesafio.setText(grupo.getDesafio() + "");
                    }

                }
            }
        });

        binding.btnUnirse.setOnClickListener(v -> {

            viewModel.isPrivado(grupoID).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if(task.isSuccessful()){
                        if(task.getResult()){
                            RellenarContraseniaFragment rellenarContraseniaFragment = new RellenarContraseniaFragment(grupo.getContrasena(), grupoID, GrupoDetailFragment.this, viewModel);
                            rellenarContraseniaFragment.show(getParentFragmentManager(), rellenarContraseniaFragment.getTag());
                        } else {
                            viewModel.aniadirGrupoUser(mAuth.getCurrentUser().getEmail().split("@")[0].replace(".",""), grupoID).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> task) {
                                    if (task.isSuccessful()){
                                        if(task.getResult()){
                                            Toast.makeText(getContext(), R.string.grupo_detail_fragment_unirse_exitoso, Toast.LENGTH_SHORT).show();
                                            GrupoViewModel grupoViewModel = new ViewModelProvider(requireActivity()).get(GrupoViewModel.class);
                                            grupoViewModel.setIdGrupo(Integer.parseInt(grupoID));
                                            Navigation.findNavController(v).navigate(R.id.navigation_grupo);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
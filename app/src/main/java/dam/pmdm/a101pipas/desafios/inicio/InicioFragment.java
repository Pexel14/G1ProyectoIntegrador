package dam.pmdm.a101pipas.desafios.inicio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentInicioBinding;

public class InicioFragment extends Fragment {

    private DatabaseReference refDesafiosUsuario;
    private DatabaseReference refDesafios;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;

    private FragmentInicioBinding binding;

    private InicioViewModel inicioViewModel;

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

        inicioViewModel.getDesafiosLiveData().observe(getViewLifecycleOwner(), this::cargarDesafios);

        inicioViewModel.cargarFragmentosDesafiosDesdeFirebase(refDesafiosUsuario, refDesafios);

    }

    private void cargarDesafios(List<TarjetaDesafioInicioFragment> fragmentos) {
        limpiarFragmentos();

        if (fragmentos.isEmpty()) {
            mostrarMensajeCeroDesafios();
        } else {
            for (TarjetaDesafioInicioFragment fragment : fragmentos) {
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.contenedorFragmentsInicio, fragment)
                        .commit();
            }
        }
    }

    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
//    private void cargarFragmentos() {
//        listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                limpiarFragmentos();
//
//                if (snapshot.exists()) {
//                    String[] desafiosID = snapshot.getValue().toString().split(",");
//                    cargarDesafiosPorId(desafiosID);
//                } else {
//                    Toast.makeText(getContext(), R.string.inicio_fragment_no_desafios_iniciados, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", getString(R.string.inicio_fragment_error_consulta) + error.getMessage());
//            }
//        };
//
//        refDesafiosUsuario.addValueEventListener(listener);
//    }

//    private void cargarDesafiosPorId(String[] desafiosId) {
//        listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot desafio : snapshot.getChildren()) {
//                    for (int i=0; i<desafiosId.length; i++) {
//                        String desafioId = desafio.child("id").getValue().toString();
//                        if (desafioId.equals(desafiosId[i])) { // Si el campo 'id' del desafío está en la lista de desafíos del usuario
//                            Log.d("Firebase", usuario + getString(R.string.inicio_fragment_log_desafio_1) + desafiosId[i] + getString(R.string.inicio_fragment_log_desafio_2) + desafio.child("titulo").getValue() + getString(R.string.inicio_fragment_coma));
//                            String titulo = desafio.child("titulo").getValue(String.class);
//                            String[] etiquetas = desafio.child("etiquetas").getValue(String.class).split(",");
//                            String descripcion = desafio.child("descripcion").getValue(String.class);
//                            String ciudad = desafio.child("ciudad").getValue(String.class);
//
//                            TarjetaDesafioInicioFragment fragment = TarjetaDesafioInicioFragment.newInstance(titulo, etiquetas, descripcion, ciudad, desafio.getKey());
//                            getChildFragmentManager()
//                                    .beginTransaction()
//                                    .add(R.id.contenedorFragmentsInicio, fragment)
//                                    .commit();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", R.string.inicio_fragment_error_consulta + error.getMessage());
//            }
//
//        };
//        refDesafios.addValueEventListener(listener);
//    }

    private void mostrarMensajeCeroDesafios() {
        // Si no hay fragments, muestra un mensaje
        TextView tvMensajeCeroFragments;
        tvMensajeCeroFragments = binding.tvMensajeCeroFragmentsInicio;
        if (getChildFragmentManager().getFragments().isEmpty()) {
            tvMensajeCeroFragments.setVisibility(View.VISIBLE);
        } else {
            tvMensajeCeroFragments.setVisibility(View.GONE);
        }
    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioInicioFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
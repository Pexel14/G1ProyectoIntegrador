package dam.pmdm.a101pipas.desafios.descubrir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentDescubrirBinding;

public class DescubrirFragment extends Fragment {

    private FragmentDescubrirBinding binding;

    private DescubrirViewModel descubrirViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDescubrirBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descubrirViewModel = new ViewModelProvider(requireActivity()).get(DescubrirViewModel.class);

        descubrirViewModel.getFragmentosList().observe(getViewLifecycleOwner(), new Observer<List<TarjetaDesafioDescubrirFragment>>() {
            @Override
            public void onChanged(List<TarjetaDesafioDescubrirFragment> fragmentos) {
                // Limpia los fragmentos previos
                limpiarFragmentos();

                // Agregar los nuevos fragmentos al contenedor según las condiciones
                for (TarjetaDesafioDescubrirFragment fragment : fragmentos) {
                    if (fragment.getArguments() != null) {
                        String ciudad = fragment.getArguments().getString("ubicacion");
                        String[] etiquetas = fragment.getArguments().getStringArray("etiquetas");

                        // Filtrar por ciudad
                        if (ciudad != null && ciudad.equals(binding.tvFiltro1Descubrir.getText().toString())) {
                            getChildFragmentManager().beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir, fragment)
                                    .commit();
                        }

                        // Filtrar por etiquetas (por ejemplo, "Gastronomía")
                        else if (etiquetas != null && containsEtiqueta(etiquetas, "Gastronomía")) {
                            getChildFragmentManager().beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir2, fragment)
                                    .commit();
                        }

                        // Filtrar por etiquetas (por ejemplo, "Cultura")
                        else if (etiquetas != null && containsEtiqueta(etiquetas, "Cultura")) {
                            getChildFragmentManager().beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir3, fragment)
                                    .commit();
                        }
                    }
                }
            }
        });

        String filtro1Text = binding.tvFiltro1Descubrir.getText().toString();
        descubrirViewModel.cargarFragmentos(filtro1Text);

    }

    private boolean containsEtiqueta(String[] etiquetas, String etiqueta) {
        for (String e : etiquetas) {
            if (e.equalsIgnoreCase(etiqueta)) {
                return true;
            }
        }
        return false;
    }

    // Método para limpiar los fragmentos previos de la vista
    private void limpiarFragmentos() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
    // De momento éste método son pruebas manuales
    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
//    private void cargarFragmentos() {
//
//        listener = new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                limpiarFragmentos();
//
//                Log.d("Firebase", "Listener registrado");
//
//                if (snapshot.exists()) { // 'snapshot!=null' siempre es 'true'
//
//                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
//                        String nodeName = nodeSnapshot.getKey();  // Obtén el nombre del nodo (nodo1, nodo2)
//                        Log.d("Firebase", "Nodo: " + nodeName);
//
//                        String titulo = nodeSnapshot.child("titulo").getValue(String.class);
//                        String sEtiquetas = nodeSnapshot.child("etiquetas").getValue(String.class);
//                        String[] etiquetas = sEtiquetas.split(",");
//                        String descripcion = nodeSnapshot.child("descripcion").getValue(String.class);
//                        String ciudad = nodeSnapshot.child("ciudad").getValue(String.class);
//
//                        // Los fragments los creo dentro de los if's porque un fragment no se puede poner en dos contenedores, asi que si un desafío cumple
//                        // dos condiciones, crea dos idénticos y asigna cada uno a su filtro
//                        if (ciudad.equals(filtro1.getText())) {
//                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, nodeSnapshot.getKey());
//
//                            getChildFragmentManager()
//                                    .beginTransaction()
//                                    .add(R.id.contenedorFragmentsDescubrir, fragment)
//                                    .commit();
//                        }
//
//                        if (sEtiquetas.contains("Gastronomía")) {
//                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, nodeSnapshot.getKey());
//
//                            getChildFragmentManager()
//                                    .beginTransaction()
//                                    .add(R.id.contenedorFragmentsDescubrir2, fragment)
//                                    .commit();
//                        }
//
//                        if (sEtiquetas.contains("Cultura")) {
//                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, nodeSnapshot.getKey());
//
//                            getChildFragmentManager()
//                                    .beginTransaction()
//                                    .add(R.id.contenedorFragmentsDescubrir3, fragment)
//                                    .commit();
//                        }
//
//                    }
//
//                } else {
//                    Toast.makeText(getContext(), R.string.descubrir_fragmentos_desafio_no_existe, Toast.LENGTH_SHORT).show();
//                }
//
//                // Aseguramos que las transacciones de fragments se procesen antes de actualizar el mensaje
//                getChildFragmentManager().executePendingTransactions();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", getString(R.string.descubrir_fragment_error_consulta) + error.getMessage());
//            }
//
//        };
//
//        ref.addValueEventListener(listener); // Antes estábamos intentando meter el listener mientras lo creábamos
//
//    }

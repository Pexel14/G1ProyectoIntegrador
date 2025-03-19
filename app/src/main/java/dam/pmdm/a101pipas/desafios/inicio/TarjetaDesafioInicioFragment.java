package dam.pmdm.a101pipas.desafios.inicio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioInicioBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasViewModel;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionViewModel;

public class TarjetaDesafioInicioFragment extends Fragment {

    private FragmentTarjetaDesafioInicioBinding binding;
    private Long idReal;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    // FALTA AÑADIR LA IMAGEN
    public static TarjetaDesafioInicioFragment newInstance(String titulo, String[] etiquetas, String descripcion, String ubicacion, String key) {
        TarjetaDesafioInicioFragment fragment = new TarjetaDesafioInicioFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putStringArray("etiquetas", etiquetas);
        args.putString("descripcion", descripcion);
        args.putString("ubicacion", ubicacion);
        args.putString("key", key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTarjetaDesafioInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Recuperar datos pasados al fragmento
        String titulo = getArguments().getString("titulo");
        String[] etiquetas = getArguments().getStringArray("etiquetas");
        String descripcion = getArguments().getString("descripcion");
        String ubicacion = getArguments().getString("ubicacion");
        String key = getArguments().getString("key");

        // Mostrar datos en la interfaz del fragment
        binding.tvTituloTarjetaInicio.setText(titulo);
        binding.tvDescripcionTarjetaInicio.setText(descripcion);
        binding.tvUbicacionTarjetaDescubrir.setText(ubicacion);

        if (etiquetas != null && etiquetas.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < etiquetas.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(etiquetas[i]);
            }
            binding.tvEtiquetasTarjetaInicio.setText(sb.toString());
        }

        GeolocalizacionViewModel geolocalizacionViewModel = new ViewModelProvider(requireActivity()).get(GeolocalizacionViewModel.class);

        binding.imgTarjetaInicio.setOnClickListener(v -> {
                geolocalizacionViewModel.setDesafioId(key);
                Navigation.findNavController(view).navigate(R.id.navigation_geolocalizacion);
        });


        ListadoExperienciasViewModel listadoExperienciasViewModel
                = new ViewModelProvider(requireActivity()).get(ListadoExperienciasViewModel.class);

        view.setOnClickListener(v -> {
            listadoExperienciasViewModel.setIdDesafio(titulo);
            Navigation.findNavController(view).navigate(R.id.navigation_listado_experiencias);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

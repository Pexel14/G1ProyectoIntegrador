package dam.pmdm.a101pipas.desafios.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioInicioBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperiencias;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionFragment;
import dam.pmdm.a101pipas.viewModelCompartidos.DesafioViewModel;

public class TarjetaDesafioInicioFragment extends Fragment {

    private FragmentTarjetaDesafioInicioBinding binding;

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


        // TODO: click tarjeta intent con key desafio a clara (nombre intent = id_desafio)
        // TODO: Replicar en descubrir con la imagen y la tarjeta


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

        DesafioViewModel desafioViewModel = new ViewModelProvider(requireActivity()).get(DesafioViewModel.class);

        binding.imgTarjetaInicio.setOnClickListener(v -> {
                desafioViewModel.setDesafioActual(key);
                Navigation.findNavController(view).navigate(R.id.navigation_geolocalizacion);});


        ImageView imgTarjeta = view.findViewById(R.id.imgTarjetaInicio);
        imgTarjeta.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeolocalizacionFragment.class);
            intent.putExtra("id_desafio", key);
            startActivity(intent);
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListadoExperiencias.class);
            intent.putExtra("id_desafio", key);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

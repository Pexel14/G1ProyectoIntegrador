package dam.pmdm.a101pipas.desafios.descubrir;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioDescubrirBinding;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioInicioBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperiencias;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionFragment;
import dam.pmdm.a101pipas.viewModelCompartidos.DesafioViewModel;

public class TarjetaDesafioDescubrirFragment extends Fragment {

    private FragmentTarjetaDesafioDescubrirBinding binding;

    public static TarjetaDesafioDescubrirFragment newInstance(String titulo, String ubicacion, String[] etiquetas, String key) {
        TarjetaDesafioDescubrirFragment fragment = new TarjetaDesafioDescubrirFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("ubicacion", ubicacion);
        args.putStringArray("etiquetas", etiquetas);
        args.putString("key", key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTarjetaDesafioDescubrirBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar datos pasados al fragmento
        String titulo = getArguments().getString("titulo");
        String ubicacion = getArguments().getString("ubicacion");
        String key = getArguments().getString("key");

        // Mostrar datos en la interfaz del fragment
        TextView tvTitulo = view.findViewById(R.id.tvTituloTarjetaDescubrir);
        TextView tvUbicacion = view.findViewById(R.id.tvUbicacionTarjetaDescubrir);

        tvTitulo.setText(titulo);
        tvUbicacion.setText(ubicacion);

        DesafioViewModel desafioViewModel = new ViewModelProvider(requireActivity()).get(DesafioViewModel.class);

        binding.imgTarjetaDesafio.setOnClickListener(v -> {
            desafioViewModel.setDesafioId(key);
            Navigation.findNavController(view).navigate(R.id.navigation_geolocalizacion);});

//        ImageView imgTarjeta = view.findViewById(R.id.imgTarjetaDesafio);
//        imgTarjeta.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), GeolocalizacionFragment.class);
//            intent.putExtra("id_desafio", key);
//            startActivity(intent);
//        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListadoExperiencias.class);
            intent.putExtra("id_desafio", key);
            startActivity(intent);
        });
    }
}
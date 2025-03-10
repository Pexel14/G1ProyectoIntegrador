package dam.pmdm.a101pipas.desafios.descubrir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioDescubrirBinding;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasViewModel;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionViewModel;
import dam.pmdm.a101pipas.social.CrearGrupoFragment;

public class TarjetaDesafioDescubrirFragment extends Fragment {

    private static String key;

    private FragmentTarjetaDesafioDescubrirBinding binding;
    private boolean seleccionado = false;
    public static TarjetaDesafioDescubrirFragment newInstance(String titulo, String ubicacion, String key) {
        TarjetaDesafioDescubrirFragment fragment = new TarjetaDesafioDescubrirFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("ubicacion", ubicacion);
        args.putString("key", key);
        TarjetaDesafioDescubrirFragment.key = key;
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

        GeolocalizacionViewModel geolocalizacionViewModel = new ViewModelProvider(requireActivity()).get(GeolocalizacionViewModel.class);

        binding.imgTarjetaDesafio.setOnClickListener(v -> {
            geolocalizacionViewModel.setDesafioId(key);
            Navigation.findNavController(view).navigate(R.id.navigation_geolocalizacion);
        });

        ListadoExperienciasViewModel listadoExperienciasViewModel
                = new ViewModelProvider(requireActivity()).get(ListadoExperienciasViewModel.class);



        if (getParentFragment() instanceof DescubrirFragment) {
            view.setOnClickListener(v -> {
                listadoExperienciasViewModel.setIdDesafio(key);
                Navigation.findNavController(view).navigate(R.id.navigation_listado_experiencias);
            });
        } else if (getParentFragment() instanceof CrearGrupoFragment) {
            view.setOnClickListener(v -> {
                if (seleccionado) {
                    cambiarColor(R.color.blue);
                    seleccionado = false;
                } else {
                    cambiarColor(R.color.color_principal);
                    seleccionado = true;
                }
            });
        }

    }
    public boolean isSeleccionado(){
        return seleccionado;
    }

    public void cambiarColor(int color) {
        binding.vColorTarjetaDescubrir.setBackgroundColor(getResources().getColor(color));
    }

    public String getKey() {
        return key;
    }

}
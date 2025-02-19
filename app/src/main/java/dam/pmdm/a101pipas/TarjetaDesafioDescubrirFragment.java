package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TarjetaDesafioDescubrirFragment extends Fragment {

    public static TarjetaDesafioDescubrirFragment newInstance(String titulo, String ubicacion) {
        TarjetaDesafioDescubrirFragment fragment = new TarjetaDesafioDescubrirFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("ubicacion", ubicacion);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tarjeta_desafio_descubrir, container, false);

        // Recuperar datos pasados al fragmento
        String titulo = getArguments().getString("titulo");
        String ubicacion = getArguments().getString("ubicacion");

        // Mostrar datos en la interfaz del fragment
        TextView tvTitulo = view.findViewById(R.id.tvTituloTarjetaDescubrir);
        TextView tvUbicacion = view.findViewById(R.id.tvUbicacionTarjetaDescubrir);

        tvTitulo.setText(titulo);
        tvUbicacion.setText(ubicacion);

        return view;

    }
}
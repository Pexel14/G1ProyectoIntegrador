package dam.pmdm.a101pipas.desafios;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.experiencias.ListadoExperiencias;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionActivity;
import dam.pmdm.a101pipas.social.CrearGrupoFragment;

public class TarjetaDesafioDescubrirFragment extends Fragment {

    private View vColorTarjetaDescubrir;
    private boolean seleccionado = false;

    public static TarjetaDesafioDescubrirFragment newInstance(String titulo, String ubicacion, String key) {
        TarjetaDesafioDescubrirFragment fragment = new TarjetaDesafioDescubrirFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("ubicacion", ubicacion);
        args.putString("key", key);
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
        String key = getArguments().getString("key");

        // Mostrar datos en la interfaz del fragment
        TextView tvTitulo = view.findViewById(R.id.tvTituloTarjetaDescubrir);
        TextView tvUbicacion = view.findViewById(R.id.tvUbicacionTarjetaDescubrir);

        vColorTarjetaDescubrir = view.findViewById(R.id.vColorTarjetaDescubrir);

        tvTitulo.setText(titulo);
        tvUbicacion.setText(ubicacion);

        ImageView imgTarjeta = view.findViewById(R.id.imgTarjetaDesafio);
        imgTarjeta.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeolocalizacionActivity.class);
            intent.putExtra("id_desafio", key);
            startActivity(intent);
        });

//        view.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), ListadoExperiencias.class);
//            intent.putExtra("id_desafio", key);
//            startActivity(intent);
//        });

        if (getParentFragment() instanceof DescubrirFragment) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ListadoExperiencias.class);
                intent.putExtra("id_desafio", key);
                startActivity(intent);
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

        return view;

    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void cambiarColor(int color) {
        vColorTarjetaDescubrir.setBackgroundColor(getResources().getColor(color));
    }

}
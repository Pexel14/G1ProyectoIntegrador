package dam.pmdm.a101pipas;

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

public class TarjetaDesafioInicioFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_tarjeta_desafio_inicio, container, false);

        // Recuperar datos pasados al fragmento
        String titulo = getArguments().getString("titulo");
        String[] etiquetas = getArguments().getStringArray("etiquetas");
        String descripcion = getArguments().getString("descripcion");
        String ubicacion = getArguments().getString("ubicacion");
        String key = getArguments().getString("key");

        // Mostrar datos en la interfaz del fragment
        TextView tvTitulo = view.findViewById(R.id.tvTituloTarjetaInicio);
        TextView tvEtiquietas = view.findViewById(R.id.tvEtiquetasTarjetaInicio);
        TextView tvDescripción = view.findViewById(R.id.tvDescripcionTarjetaInicio);
        TextView tvUbicacion = view.findViewById(R.id.tvUbicacionTarjetaInicio);

        tvTitulo.setText(titulo);
        tvDescripción.setText(descripcion);
        tvUbicacion.setText(ubicacion);

        if (etiquetas != null) {
            for (int i=0; i<etiquetas.length; i++) {
                if (i==0) {
                    tvEtiquietas.setText(etiquetas[i]);
                } else {
                    tvEtiquietas.setText(tvEtiquietas.getText().toString() + ", " + etiquetas[i]);
                }
            }
        }

        ImageView imgTarjeta = view.findViewById(R.id.imgTarjetaInicio);
        imgTarjeta.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeolocalizacionActivity.class);
            intent.putExtra("id_desafio", key);
            startActivity(intent);
        });

        // TODO: click tarjeta intent con key desafio a clara (nombre intent = id_desafio)
        // TODO: Replicar en descubrir con la imagen y la tarjeta

        return view;

    }

}

package dam.pmdm.a101pipas.experiencias;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import dam.pmdm.a101pipas.R;

public class TarjetaExperienciaFragment extends Fragment {

    private EditText titulo, descripcion;

    public static TarjetaExperienciaFragment newInstance(String titulo, String descripcion) {
        TarjetaExperienciaFragment fragment = new TarjetaExperienciaFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("descripcion", descripcion);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarjeta_experiencia, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titulo = view.findViewById(R.id.etTituloCrearExperiencia);
        descripcion = view.findViewById(R.id.etDescripcionCrearExperiencia);
    }

    public boolean isEmpty() {
        if (!titulo.getText().toString().isEmpty() && !descripcion.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String getTitulo() {
        return titulo.getText().toString();
    }

    public String getDescripcion() {
        return descripcion.getText().toString();
    }
}
package dam.pmdm.a101pipas.desafios;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentCrearDesafioBinding;
import dam.pmdm.a101pipas.models.Desafio;

public class CrearDesafioFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FragmentCrearDesafioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearDesafioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);databaseReference = FirebaseDatabase.getInstance().getReference("desafios");

        binding.btnInsertarExperiencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (datosIncompletos()) {
                    Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    String titulo, ciudad, descripcion, experiencias = "";
                    titulo = binding.etNombreDesafioCrearDesafio.getText().toString();
                    ciudad = binding.etCiudadCrearDesafio.getText().toString();
                    descripcion = binding.etDescripcionCrearDesafio.getText().toString();

                    // Agrega a "experiencias" las experiencias activas (checkboxes)
                    for (int i = 0; i < binding.clCheckboxesCrearDesafio.getChildCount(); i++) {
                        View v = binding.clCheckboxesCrearDesafio.getChildAt(i);

                        if (v instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) v;

                            // Si el checkbox está seleccionado, añades su texto al StringBuilder
                            if (checkBox.isChecked()) {
                                experiencias += checkBox.getText().toString() + ",";
                            }
                        }
                    }

                    // Elimina la última coma
                    if (experiencias.endsWith(",")) {
                        experiencias = experiencias.substring(0, experiencias.length() - 1);
                    }



                    Desafio desafio = new Desafio(titulo, ciudad, descripcion, experiencias); // Crear el objeto
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Desafio", desafio);


                    Navigation.findNavController(view).navigate(R.id.crear_experiencias, bundle);

                }

            }
        });

        binding.btnAtrasCrearDesafio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private boolean datosIncompletos() {
        if (!binding.etNombreDesafioCrearDesafio.getText().toString().isEmpty() &&
            !binding.etCiudadCrearDesafio.getText().toString().isEmpty() &&
            !binding.etDescripcionCrearDesafio.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
package dam.pmdm.a101pipas.experiencias;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaExperienciaBinding;

public class TarjetaExperienciaFragment extends Fragment {

    private FragmentTarjetaExperienciaBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;

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
        binding = FragmentTarjetaExperienciaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(binding.ivImagenExperiencia);
                    }
                }
        );

        binding.btnSubirFotoExperiencia.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setPackage("com.google.android.apps.photos");
            imagePickerLauncher.launch(i);
        });

    }

    public boolean isEmpty() {
        if (!binding.etTituloCrearExperiencia.getText().toString().isEmpty() &&
                !binding.etDescripcionCrearExperiencia.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String getTitulo() {
        return binding.etTituloCrearExperiencia.getText().toString();
    }

    public String getDescripcion() {
        return binding.etDescripcionCrearExperiencia.getText().toString();
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
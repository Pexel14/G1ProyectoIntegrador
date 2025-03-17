package dam.pmdm.a101pipas.perfil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentEditarPerfilBinding;
import dam.pmdm.a101pipas.models.User;


public class EditarPerfilFragment extends BottomSheetDialogFragment {

    private FragmentEditarPerfilBinding binding;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String claveUser, avatar, username;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditarPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("imagenesFotoPerfil");

        loadUserData();

        binding.btnGuardarCambios.setOnClickListener(v -> {

            username = binding.etUsername.getText().toString().trim();

            if (validarCampos()) {
                if (imageUri != null) {
                    subirImagenYActualizar();
                } else {
                    actualizarUsuarioFirebase(avatar);
                }
            }
        });

        binding.imgAvatar.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selecciona_una_imagen)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).fit().centerCrop().into(binding.imgAvatar);
            //viewModel.uploadImage(imageUri);
        }
    }

    private void subirImagenYActualizar() {
        if (imageUri == null) return;

        StorageReference fileReference = storageReference.child(claveUser + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    actualizarUsuarioFirebase(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    private void actualizarUsuarioFirebase(String avatarUrl) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios").child(claveUser);
        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("username", username);
        actualizaciones.put("foto_perfil", avatarUrl);

        reference.updateChildren(actualizaciones)
                .addOnSuccessListener(aVoid -> dismiss())
                .addOnFailureListener( e -> Toast.makeText(getContext(), R.string.ha_ocurrido_un_error_al_actualizar_el_perfil, Toast.LENGTH_SHORT).show());
    }

    private boolean validarCampos() {
        return !username.isEmpty();
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() != null) {
            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                claveUser = usuario.split("@")[0].replace(".", "");
            }
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(claveUser);
        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                User userProfile = dataSnapshot.getValue(User.class);
                if (userProfile != null) {
                    avatar = userProfile.getFoto_perfil();
                    if (!avatar.isEmpty()) {
                        Picasso.get().load(avatar).fit().centerCrop().into(binding.imgAvatar);
                    }
                    binding.etUsername.setText(userProfile.getUsername());
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
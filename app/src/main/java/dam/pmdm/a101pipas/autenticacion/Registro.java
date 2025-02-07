package dam.pmdm.a101pipas.autenticacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.ActivityRegistroBinding;
import dam.pmdm.a101pipas.models.User;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el View Binding
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        auth = FirebaseAuth.getInstance();

        // Botón de registro
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String contrasenia = binding.etPassword.getText().toString().trim();
                String confirmPassword = binding.etConfirmPassword.getText().toString().trim();


                if (username.isEmpty() || email.isEmpty() || contrasenia.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Registro.this, R.string.registro_rellena_todos_los_campos, Toast.LENGTH_SHORT).show();
                } else if (!contrasenia.equals(confirmPassword)) {
                    binding.tvPasswordError.setVisibility(View.VISIBLE);
                    binding.tvPasswordError.setText(R.string.registro_contrasenias_no_coinciden_2);
                } else if (contrasenia.length() < 6) {
                    binding.tvPasswordError.setVisibility(View.VISIBLE);
                    binding.tvPasswordError.setText(R.string.registro_contrasenia_6_caracteres);
                } else {
                    binding.tvPasswordError.setVisibility(View.GONE);

                    // Registrar usuario en Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, contrasenia)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();
                                        guardarUsuarioEnDatabase(username, email, contrasenia);
                                    }
                                } else {
                                    Toast.makeText(Registro.this, getString(R.string.registro_error_registro) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

    }

    private void guardarUsuarioEnDatabase(String username, String email, String contrasenia) {

        String id = email.split("@")[0].replace(".", "");
        User user = new User(id, username, email, contrasenia);

        // Guardar en Firebase Realtime Database
        databaseReference.child(id).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Registro.this, R.string.registro_usuario_registrado, Toast.LENGTH_SHORT).show();

                binding.etEmail.setText("");
                binding.etPassword.setText("");
                binding.etConfirmPassword.setText("");
                binding.etUsername.setText("");
            } else {
                Toast.makeText(Registro.this, R.string.registro_guardar_usuario_db_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Clic en iniciar sesion
    public void volverAtras(View view) {
        Intent intent = new Intent(Registro.this, Login.class);
        startActivity(intent);
        finish();
    }
}

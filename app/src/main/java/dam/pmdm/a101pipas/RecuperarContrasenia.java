package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.databinding.ActivityRecuperarContraseniaBinding;

public class RecuperarContrasenia extends AppCompatActivity {
    private ActivityRecuperarContraseniaBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar View Binding
        binding = ActivityRecuperarContraseniaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar FirebaseAuth y FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("usuarios"); // Ruta donde guardas los usuarios en Firebase Database

        // Configurar el botón de enviar correo
        binding.btnEnviarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    binding.tvErrorMessage.setText("El correo no puede estar vacío");
                    binding.tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                binding.tvErrorMessage.setVisibility(View.GONE);

                // Verificar si el usuario está en la base de datos antes de enviar el correo
                verificarUsuarioEnBBDD(email);
            }
        });
    }

    private void verificarUsuarioEnBBDD(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // El usuario está en la base de datos, enviar el correo de recuperación
                    enviarCorreoRecuperacion(email);
                } else {
                    // El usuario NO existe en la base de datos
                    binding.tvErrorMessage.setText("Cuenta no encontrada en la base de datos.");
                    binding.tvErrorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecuperarContrasenia.this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarCorreoRecuperacion(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RecuperarContrasenia.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        binding.tvErrorMessage.setText("Este correo no está registrado en Firebase Auth.");
                    } else {
                        binding.tvErrorMessage.setText("Error al enviar el correo.");
                    }
                    binding.tvErrorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}


//falta botón de volver

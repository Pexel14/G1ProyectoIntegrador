package dam.pmdm.a101pipas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    static final String TAG = "Login";
    public static final String[] PROHIBIDO_FIREBASE = {"$", "/", ".", "]", "#"};
    FirebaseDatabase firebase;
    SignInButton btnGoogleSignIn;
    EditText etCorreo, etPassword;
    Button btnInicioSesion, btnRegistro;
    DatabaseReference ref;
    TextView tvLoginCorreoError, tvLoginContraseniaError, tvContraseniaOlvidada;

    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Inicio de sesion COMPLETADO " + mAuth.getCurrentUser().getDisplayName() + " - " + mAuth.getCurrentUser().getDisplayName());
                                guardarCorreo();
                                Intent intent = new Intent(getApplicationContext(), Inicio.class);
                                String usuario = mAuth.getCurrentUser().getEmail();
                                Log.d(TAG, "Usuario: " + usuario);
                                String id = usuario.split("@")[0].replace(".", "");
                                intent.putExtra("usuario", id);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "Inicio de sesion FALLIDO: " + task.getException());
                                Toast.makeText(Login.this, R.string.login_google_fallido, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.d(TAG, "Resultado: " + result.getResultCode());
            }
        }
    });

    private void guardarCorreo() {
        if (mAuth.getCurrentUser() != null) {
            String emailCortado = mAuth.getCurrentUser().getEmail().split("@")[0];
            if (emailCortado != null) {
                for (int i = 0; i < PROHIBIDO_FIREBASE.length; i++) {
                    if (emailCortado.contains(PROHIBIDO_FIREBASE[i])) {
                        emailCortado.replaceAll(PROHIBIDO_FIREBASE[i], "");
                    }
                }
//                ref = FirebaseDatabase.getInstance().getReference("usuarios");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String usuario = currentUser.getEmail();
            if (usuario != null) {
                String id = usuario.split("@")[0].replace(".", "");
                Log.d(TAG, "ID: " + id);
                Intent intent = new Intent(getApplicationContext(), Descubrir.class);
                intent.putExtra("usuario", id);
                startActivity(intent);
                finish(); // Cierra la actividad de login para que no se pueda volver atrás
            }
        } else {
            Log.d(TAG, "No hay usuario autenticado");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.getInstance("[DEFAULT]");

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        btnRegistro = findViewById(R.id.btnRegistro);
        tvLoginContraseniaError = findViewById(R.id.tvLoginContraseniaError);
        tvLoginCorreoError = findViewById(R.id.tvLoginCorreoError);
        tvContraseniaOlvidada = findViewById(R.id.tvContraseniaOlvidada);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContrasenia);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(intent);
            }
        });

        tvContraseniaOlvidada.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecuperarContrasenia.class);
            startActivity(intent);
        });

        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLoginCorreoError.setVisibility(View.INVISIBLE);
                tvLoginContraseniaError.setVisibility(View.INVISIBLE);
                String email = String.valueOf(etCorreo.getText());
                String password = String.valueOf(etPassword.getText());

                // Comprobar si los campos están vacíos
                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "signInWithEmail:success");
                                            Intent intent = new Intent(getApplicationContext(), Inicio.class);
                                            String id = email.split("@")[0].replace(".", "");
                                            intent.putExtra("usuario", id);
                                            startActivity(intent);
                                        } else {
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            tvLoginCorreoError.setVisibility(View.VISIBLE);
                                            tvLoginCorreoError.setText(R.string.login_correo_incorrecto);

                                            tvLoginContraseniaError.setVisibility(View.VISIBLE);
                                            tvLoginContraseniaError.setText(R.string.login_contrasenia_incorrecta);
                                        }
                                    }
                                });
                    } else {
                        tvLoginContraseniaError.setVisibility(View.VISIBLE);
                        tvLoginContraseniaError.setText(R.string.login_contrasenia_vacia);
                    }

                } else {
                    tvLoginCorreoError.setVisibility(View.VISIBLE);
                    tvLoginCorreoError.setText(R.string.login_correo_vacio);
                }
            }
        });

    }


}
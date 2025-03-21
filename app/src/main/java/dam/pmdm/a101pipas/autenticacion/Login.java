package dam.pmdm.a101pipas.autenticacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import dam.pmdm.a101pipas.MainActivity;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.User;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    static final String TAG = "Login";

    SignInButton btnGoogleSignIn;
    EditText etCorreo, etPassword;
    Button btnInicioSesion;
    DatabaseReference ref;
    TextView tvLoginCorreoError, tvLoginContraseniaError, tvContraseniaOlvidada, tvRegistrateLogin;
    private static String idUltimo = "0";
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Log.d(TAG, "PASA POR AQUI 0");
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                Log.d(TAG, "PASA POR AQUI 1");
                try {
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    Log.d(TAG, "PASA POR AQUI 2");

                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "PASA POR AQUI 3");
                            if (task.isSuccessful()) {
                                String usuario = mAuth.getCurrentUser().getEmail();
                                String id = usuario.split("@")[0].replace(".", "");
                                usuarioExiste(id).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Boolean> task) {
                                        if(task.isSuccessful()){
                                            if(task.getResult()){
                                                Log.d(TAG, "Usuario: " + usuario);
                                                Log.d(TAG, "Inicio de sesion COMPLETADO " + mAuth.getCurrentUser().getDisplayName() + " - " + mAuth.getCurrentUser().getDisplayName());
                                            } else {
                                                guardarCorreo(id, usuario);
                                            }
                                            mandarUsuarioInicio(id);
                                        }
                                    }
                                });
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

    private void guardarCorreo(String id, String correo) {
        if (mAuth.getCurrentUser() != null) {
            if (id != null) {
                idUltimo = String.valueOf(Integer.parseInt(idUltimo) + 1);
                String nombre = mAuth.getCurrentUser().getDisplayName();
                ref.child(id).setValue(new User(idUltimo, nombre, correo, 0, "", "", "")).addOnCompleteListener(command -> {
                    Toast.makeText(this, R.string.login_bienvenido_app, Toast.LENGTH_SHORT).show();
                });
            }

        }
    }

    private void buscarID() {

        ref.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idAux = "";
                for (DataSnapshot usuarios : snapshot.getChildren()) {
                    idAux = usuarios.child("id").getValue().toString();

                    if(Integer.parseInt(idAux) > Integer.parseInt(idUltimo)){
                        idUltimo = idAux;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Task<Boolean> usuarioExiste(String id) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean encontrado = false;
                for (DataSnapshot subNodos : snapshot.getChildren()) {
                    if(id.equals(subNodos.getKey())){
                        encontrado = true;
                        taskCompletionSource.setResult(true);
                    }
                }
                if(!encontrado){
                    taskCompletionSource.setResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                String id = usuario.split("@")[0].replace(".", "");
                Log.d(TAG, "ID: " + id);

                mandarUsuarioInicio(id);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        FirebaseApp.getInstance("[DEFAULT]");

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        tvRegistrateLogin = findViewById(R.id.tvRegistrateLogin);
        tvLoginContraseniaError = findViewById(R.id.tvLoginContraseniaError);
        tvLoginCorreoError = findViewById(R.id.tvLoginCorreoError);
        tvContraseniaOlvidada = findViewById(R.id.tvContraseniaOlvidada);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContrasenia);

        ref = FirebaseDatabase.getInstance().getReference("usuarios");
        buscarID();
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        tvRegistrateLogin.setOnClickListener(new View.OnClickListener() {
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
                                            String id = email.split("@")[0].replace(".", "");
                                            if (id.equals("prueba")) {
                                                id = "usuario";
                                            }
                                            mandarUsuarioInicio(id);
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

    public void mandarUsuarioInicio(String idUsuario) {

        SharedPreferences sharedPreferences = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", idUsuario);
        editor.apply();


        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
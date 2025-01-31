package dam.pmdm.a101pipas;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleSignatureVerifier;
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

import dam.pmdm.a101pipas.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    static final String TAG = "Login";

    SignInButton btnGoogleSignIn;
    EditText etCorreo, etPassword;
    Button btnInicioSesion;
    TextView tvLoginCorreoError, tvLoginContraseniaError;

    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try{
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mAuth = FirebaseAuth.getInstance();
                                Log.d(TAG, "Inicio de sesion COMPLETADO " + mAuth.getCurrentUser().getDisplayName() + " - " + mAuth.getCurrentUser().getDisplayName());
                            } else {
                                Log.d(TAG, "Inicio de sesion FALLIDO: " + task.getException());
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
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser usuario = mAuth.getCurrentUser();
//
//        if (usuario != null){
//            Intent intent = new Intent(getApplicationContext(), Inicio.class);
//            intent.putExtra("usuario", usuario);
//            startActivity(intent);
//        }
//
//    }

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

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        tvLoginContraseniaError = findViewById(R.id.tvLoginContraseniaError);
        tvLoginCorreoError = findViewById(R.id.tvLoginCorreoError);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContrasenia);

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

//        binding.tvContraseniaOlvidada.setOnClickListener(v->{
//            Intent intent = new Intent(getApplicationContext(), ContraseniaOlvidada.class);
//        });

        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLoginCorreoError.setVisibility(View.INVISIBLE);
                tvLoginContraseniaError.setVisibility(View.INVISIBLE);
                String email = String.valueOf(etCorreo.getText());
                String password = String.valueOf(etPassword.getText());

                // Comprobar si los campos están vacíos
                if (email.isEmpty()){
                    tvLoginCorreoError.setVisibility(View.VISIBLE);
                    tvLoginCorreoError.setText(R.string.login_correo_vacio);
                }

                if (password.isEmpty()){
                    tvLoginContraseniaError.setVisibility(View.VISIBLE);
                    tvLoginContraseniaError.setText(R.string.login_contrasenia_vacia);
                }

                // Parte central, signInWithEmailAndPassword obtenido de la documentación de Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    tvLoginCorreoError.setVisibility(View.VISIBLE);
                                    tvLoginCorreoError.setText(R.string.login_correo_incorrecto);

                                    tvLoginContraseniaError.setVisibility(View.VISIBLE);
                                    tvLoginContraseniaError.setText(R.string.login_contrasenia_incorrecta);
                                }
                            }
                        });
            }
        });

    }


}
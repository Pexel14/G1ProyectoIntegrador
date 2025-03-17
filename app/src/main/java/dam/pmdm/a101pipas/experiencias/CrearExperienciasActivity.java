package dam.pmdm.a101pipas.experiencias;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.a101pipas.MainActivity;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.Experiencia;

public class CrearExperienciasActivity extends AppCompatActivity {

    private List<TarjetaExperienciaFragment> listaDeFragments = new ArrayList<>();
    private FragmentManager fragmentManager;

    private ImageButton btnAtrasCrearExperiencias;
    private Button btnAniadirExperienciaCrearDesafio, btnCrearDesafio;
    private LinearLayout llExperienciasCrearExperiencias;

    private DatabaseReference databaseReference;
    private Desafio desafio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_experiencias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔹 Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("desafios");

        // 🔹 Obtener objeto Desafio del Intent
        if (getIntent().hasExtra("desafio")) {
            desafio = (Desafio) getIntent().getSerializableExtra("desafio");
        } else {
            Toast.makeText(this, "Error: No se ha recibido un desafío válido.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fragmentManager = getSupportFragmentManager();
        llExperienciasCrearExperiencias = findViewById(R.id.llExperienciasCrearExperiencias);

        // 🔹 Añadir un fragmento inicial
        agregarNuevoFragment();

        btnAtrasCrearExperiencias = findViewById(R.id.btnAtrasCrearExperiencias);
        btnAtrasCrearExperiencias.setOnClickListener(view -> confirmarVolverAtras());

        btnAniadirExperienciaCrearDesafio = findViewById(R.id.btnAniadirExperienciaCrearDesafio);
        btnAniadirExperienciaCrearDesafio.setOnClickListener(view -> {
            int ultimoFragment = listaDeFragments.size() - 1;
            if (ultimoFragment >= 0 && listaDeFragments.get(ultimoFragment).isEmpty()) {
                Toast.makeText(this, R.string.crear_experiencias_rellena_todos_los_campos, Toast.LENGTH_SHORT).show();
            } else {
                agregarNuevoFragment();
            }
        });

        btnCrearDesafio = findViewById(R.id.btnCrearDesafio);
        btnCrearDesafio.setOnClickListener(view -> guardarDesafioEnFirebase());
    }

    /**
     * 🔹 Método para confirmar si el usuario quiere salir sin guardar.
     */
    private void confirmarVolverAtras() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.crear_experiencias_no_se_van_a_guardar_exp)
                .setPositiveButton(R.string.crear_experiencias_si, (dialogInterface, i) -> finish())
                .setNegativeButton(R.string.crear_experiencias_no, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    /**
     * 🔹 Método para manejar el botón de retroceso del sistema.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        confirmarVolverAtras();
    }

    /**
     * 🔹 Método para agregar un nuevo fragmento de experiencia.
     */
    private void agregarNuevoFragment() {
        TarjetaExperienciaFragment fragment = TarjetaExperienciaFragment.newInstance("", "");
        listaDeFragments.add(fragment);
        fragmentManager
                .beginTransaction()
                .add(R.id.llExperienciasCrearExperiencias, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 🔹 Método para guardar el desafío en Firebase.
     */
    private void guardarDesafioEnFirebase() {
        if (listaDeFragments.isEmpty()) {
            Toast.makeText(this, R.string.crear_experiencias_debe_incluir_una_exp, Toast.LENGTH_SHORT).show();
            return;
        }

        String experiencias = "";
        for (TarjetaExperienciaFragment fragment : listaDeFragments) {
            if (fragment.isEmpty()) {
                Toast.makeText(this, "Hay experiencias sin completar.", Toast.LENGTH_SHORT).show();
                return;
            }
            Experiencia ex = new Experiencia(fragment.getTitulo(), fragment.getDescripcion());
//            mapExperiencias.put(ex.getTitulo(), ex);
        }

//        desafio.setExperiencias(mapExperiencias);
        String keyDesafio = desafio.getTitulo();

        databaseReference.child(keyDesafio).setValue(desafio)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.crear_experiencias_desafio_creado, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.crear_experiencias_error_al_crear_desafio, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

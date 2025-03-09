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

    private List<TarjetaExperienciaFragment> listaDeFragments = new ArrayList<TarjetaExperienciaFragment>();
    private FragmentManager fragmentManager;

    private ImageButton btnAtrasCrearExperiencias;
    private Button btnAniadirExperienciaCrearDesafio, btnCrearDesafio;
    private LinearLayout llExperienciasCrearExperiencias;

    private DatabaseReference databaseReference;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("desafios");

        fragmentManager = getSupportFragmentManager();
        llExperienciasCrearExperiencias = findViewById(R.id.llExperienciasCrearExperiencias);

        // Añadimos un fragment inicial
        agregarNuevoFragment();

        btnAtrasCrearExperiencias = findViewById(R.id.btnAtrasCrearExperiencias);
        btnAtrasCrearExperiencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarVolverAtras();
            }
        });

        btnAniadirExperienciaCrearDesafio = findViewById(R.id.btnAniadirExperienciaCrearDesafio);
        btnAniadirExperienciaCrearDesafio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ultimoFragment = listaDeFragments.size() - 1;
                if (listaDeFragments.get(ultimoFragment).isEmpty()) {
                    Toast.makeText(CrearExperienciasActivity.this, R.string.crear_experiencias_rellena_todos_los_campos, Toast.LENGTH_SHORT).show();
                } else {
                    agregarNuevoFragment();
                }
            }
        });

        btnCrearDesafio = findViewById(R.id.btnCrearDesafio);
        btnCrearDesafio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listaDeFragments.isEmpty()) {
                    Toast.makeText(CrearExperienciasActivity.this, R.string.crear_experiencias_debe_incluir_una_exp, Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Experiencia> mapExperiencias = new HashMap<>();

                    for (TarjetaExperienciaFragment fragment : listaDeFragments) {
                        Experiencia ex = new Experiencia(fragment.getTitulo(), fragment.getDescripcion());
                        mapExperiencias.put(ex.getTitulo(), ex);
                    }

                    Desafio desafio = (Desafio) getIntent().getSerializableExtra("desafio");
                    desafio.setExperiencias(mapExperiencias);

                    String keyDesafio = desafio.getTitulo();

//                    // Añadir ciudad
//                    databaseReference.child(keyDesafio)
//                            .child("ciudad")
//                            .setValue(desafio.getCiudad());
//
//                    // Añadir título
//                    databaseReference.child(keyDesafio)
//                            .child("titulo")
//                            .setValue(desafio.getTitulo());

                    databaseReference.child(keyDesafio)
//                            .child("experiencias")
//                            .setValue(mapExperiencias)
                            .setValue(desafio)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CrearExperienciasActivity.this, R.string.crear_experiencias_desafio_creado, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CrearExperienciasActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(CrearExperienciasActivity.this, R.string.crear_experiencias_error_al_crear_desafio, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void confirmarVolverAtras() {
        new AlertDialog.Builder(CrearExperienciasActivity.this)
                .setTitle("")
                .setMessage(R.string.crear_experiencias_no_se_van_a_guardar_exp)
                .setPositiveButton(R.string.crear_experiencias_si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.crear_experiencias_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @SuppressLint("MissingSuperCall") // Usar el "super" del onBackPressed provoca un fallo visual
    @Override
    public void onBackPressed() {
        confirmarVolverAtras();
    }

    private void agregarNuevoFragment() {
        TarjetaExperienciaFragment fragment = TarjetaExperienciaFragment.newInstance("", "");
        listaDeFragments.add(fragment);
        fragmentManager
                .beginTransaction()
                .add(R.id.llExperienciasCrearExperiencias, fragment)
                .addToBackStack(null)
                .commit();
    }
}
package dam.pmdm.a101pipas.desafios;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.experiencias.CrearExperienciasActivity;
import dam.pmdm.a101pipas.models.Desafio;

public class CrearDesafioActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private Button btnInsertarExperiencias;
    private ImageButton btnAtrasCrearDesafio;
    private EditText etNombreDesafioCrearDesafio, etCiudadCrearDesafio, etDescripcionCrearDesafio;
    private ConstraintLayout clCheckboxesCrearDesafio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_desafio);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        databaseReference = FirebaseDatabase.getInstance().getReference("desafios");

        clCheckboxesCrearDesafio = findViewById(R.id.clCheckboxesCrearDesafio);

        btnInsertarExperiencias = findViewById(R.id.btnInsertarExperiencias);
        btnInsertarExperiencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (datosIncompletos()) {
                    Toast.makeText(CrearDesafioActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    String titulo, ciudad, descripcion, experiencias = "";
                    titulo = etNombreDesafioCrearDesafio.getText().toString();
                    ciudad = etCiudadCrearDesafio.getText().toString();
                    descripcion = etDescripcionCrearDesafio.getText().toString();

                    // Agrega a "experiencias" las experiencias activas (checkboxes)
                    for (int i = 0; i < clCheckboxesCrearDesafio.getChildCount(); i++) {
                        View v = clCheckboxesCrearDesafio.getChildAt(i);

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

                    int id = Integer.parseInt(databaseReference.push().getKey()); // Se supone que genera una key nueva

                    Desafio desafio = new Desafio(titulo, ciudad, descripcion, experiencias, id);

                    Intent i = new Intent(CrearDesafioActivity.this, CrearExperienciasActivity.class);
                    i.putExtra("desafio", desafio);
                    startActivity(i);
                }

            }
        });

        btnAtrasCrearDesafio = findViewById(R.id.btnAtrasCrearDesafio);
        btnAtrasCrearDesafio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etNombreDesafioCrearDesafio = findViewById(R.id.etNombreDesafioCrearDesafio);
        etCiudadCrearDesafio = findViewById(R.id.etCiudadCrearDesafio);
        etDescripcionCrearDesafio = findViewById(R.id.etDescripcionCrearDesafio);
    }

    private boolean datosIncompletos() {
        if (!etNombreDesafioCrearDesafio.getText().toString().isEmpty() &&
            !etCiudadCrearDesafio.getText().toString().isEmpty() &&
            !etDescripcionCrearDesafio.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
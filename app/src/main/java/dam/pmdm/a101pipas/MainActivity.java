package dam.pmdm.a101pipas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnIrAInicioMain, btnIrADescubrirMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIrAInicioMain = findViewById(R.id.btnIrAInicioMain);
        btnIrADescubrirMain = findViewById(R.id.btnIrADescubrirMain);

        // PRUEBAS
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("desafioId")) {
            String key = intent.getStringExtra("desafioId");
            Toast.makeText(this, "VIENES DE " + key, Toast.LENGTH_SHORT).show();
        }

        btnIrAInicioMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Inicio.class);
                startActivity(i);
            }
        });

        btnIrADescubrirMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Descubrir.class);
                startActivity(i);
            }
        });

    }
}
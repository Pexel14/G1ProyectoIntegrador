package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import dam.pmdm.a101pipas.R;

public class GroupDetail extends AppCompatActivity {

    private ImageView imgGrupo, btnBack;
    private TextView tvNombreGrupo, tvCreador, tvFechaCreacion, tvAventureros, tvDesafio;
    private Button btnUnirse;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // Inicializar vistas
        imgGrupo = findViewById(R.id.imgGrupo);
        btnBack = findViewById(R.id.btnBack);
        tvNombreGrupo = findViewById(R.id.tvNombreGrupo);
        tvCreador = findViewById(R.id.tvCreador);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        tvAventureros = findViewById(R.id.tvMiembros);
        tvDesafio = findViewById(R.id.tvDesafio);
        btnUnirse = findViewById(R.id.btnUnirse);

        // Obtener el ID del grupo desde el intent
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, R.string.group_detail_id_no_recibido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Referencia a la base de datos en Firebase


        // Cargar datos del grupo desde Firebase
        // Botón de regreso
        btnBack.setOnClickListener(v -> finish());

        // Acción de unirse al grupo (puede ser personalizada)
        btnUnirse.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.group_detail_te_has_unido_a) + tvNombreGrupo.getText(), Toast.LENGTH_SHORT).show();
        });
    }


}

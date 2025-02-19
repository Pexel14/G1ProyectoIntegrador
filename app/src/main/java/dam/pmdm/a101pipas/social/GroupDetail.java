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
            Toast.makeText(this, "Error: ID de grupo no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Referencia a la base de datos en Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("grupos").child(groupId);

        // Cargar datos del grupo desde Firebase
        cargarDatosGrupo();

        // Botón de regreso
        btnBack.setOnClickListener(v -> finish());

        // Acción de unirse al grupo (puede ser personalizada)
        btnUnirse.setOnClickListener(v -> {
            // Aquí puedes agregar la lógica para unirse al grupo
            Toast.makeText(this, "Te has unido al grupo " + tvNombreGrupo.getText(), Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarDatosGrupo() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(GroupDetail.this, "Grupo no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Obtener los valores desde Firebase
                String nombreGrupo = snapshot.child("titulo").getValue(String.class);
                String fotoGrupo = snapshot.child("foto_grupo").getValue(String.class);
                String creador = snapshot.child("creador").getValue(String.class);
                String fechaCreacion = snapshot.child("fecha_creacion").getValue(String.class);
                Long numeroIntegrantes = snapshot.child("numero_integrantes").getValue(Long.class);
                String desafio = snapshot.child("desafio").getValue(String.class);

                // Asignar valores a las vistas (evitar valores null)
                tvNombreGrupo.setText(nombreGrupo != null ? nombreGrupo : "Sin título");
                tvCreador.setText(creador != null ? "Creador: " + creador : "Creador desconocido");
                tvFechaCreacion.setText(fechaCreacion != null ? "Creado el: " + fechaCreacion : "Fecha no disponible");
                tvAventureros.setText(numeroIntegrantes != null ? numeroIntegrantes + " Aventureros" : "0 Aventureros");
                tvDesafio.setText(desafio != null ? "Desafío: " + desafio : "Desafío no disponible");

                // Cargar imagen con Picasso, si la imagen no existe, usa una por defecto
                if (fotoGrupo != null && !fotoGrupo.isEmpty()) {
                    Picasso.get().load(fotoGrupo).placeholder(R.drawable.perfil_por_defecto).into(imgGrupo);
                } else {
                    imgGrupo.setImageResource(R.drawable.perfil_por_defecto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupDetail.this, "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

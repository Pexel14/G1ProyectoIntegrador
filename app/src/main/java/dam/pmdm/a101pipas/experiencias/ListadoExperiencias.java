package dam.pmdm.a101pipas.experiencias;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.desafios.inicio.InicioFragment;
import dam.pmdm.a101pipas.models.Experiencia;

public class ListadoExperiencias extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvTitle;
    ExperienciasListAdapter adapter;
    List<Experiencia> experienciaList;
    DatabaseReference ref;
    DatabaseReference refDesafios;
    FirebaseDatabase firebase;
    ValueEventListener listener;
    String keyDesafio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_experiencias);

        recyclerView = findViewById(R.id.rvExperiencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista y adaptador
        experienciaList = new ArrayList<>();
        adapter = new ExperienciasListAdapter(experienciaList);
        recyclerView.setAdapter(adapter);

        // Referencia a la barra de progreso y texto
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tvProgress = findViewById(R.id.tvProgress);

        // Referencia a Firebase
        firebase = FirebaseDatabase.getInstance();
        keyDesafio = getIntent().getStringExtra("id_desafio");
        ref = FirebaseDatabase.getInstance().getReference("desafios").child(keyDesafio).child("experiencias");

        // Para conseguir el nombre del desafío
        refDesafios = firebase.getReference("desafios");

        // Nombre del desafío
        tvTitle = findViewById(R.id.tvTitle);
        conseguirNombreDesafio();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                experienciaList.clear();
                int totalExperiencias = 0;
                int experienciasCompletadas = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String titulo = data.getKey(); // Nombre de la experiencia
                    String descripcion = data.child("descripcion").getValue(String.class);
                    String link = data.child("link").getValue(String.class);
                    String mapa = data.child("mapa").getValue(String.class);
                    String imgExperiencia = data.child("imgExperiencia").getValue(String.class);
                    Boolean completadaValue = data.child("completada").getValue(Boolean.class);
                    boolean completada = completadaValue != null && completadaValue;

                    if (titulo != null && descripcion != null) {
                        Experiencia experiencia = new Experiencia(
                                titulo, descripcion,
                                link != null ? link : "",
                                mapa != null ? mapa : "",
                                imgExperiencia != null ? imgExperiencia : "",
                                completada
                        );
                        experienciaList.add(experiencia);


                        totalExperiencias++;
                        if (completada) experienciasCompletadas++;
                    }
                }


                int porcentaje = (totalExperiencias > 0) ? (experienciasCompletadas * 100 / totalExperiencias) : 0;
                progressBar.setProgress(porcentaje);
                tvProgress.setText(getString(R.string.listado_experiencias_tvProgress_1) + experienciasCompletadas + " / " + totalExperiencias + getString(R.string.listado_experiencias_tvProgress_2));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error al cargar los datos: " + error.getMessage());
            }
        });
    }

    private String conseguirNombreDesafio() {
        final String[] nombreDesafio = {null};
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot desafio : snapshot.getChildren()) {
                    if (desafio.getKey().equals(keyDesafio)) {
                        Log.d("Firebase", getString(R.string.listado_experiencias_log_1) + desafio.child("titulo").getValue());
                        nombreDesafio[0] = String.valueOf(desafio.child("titulo").getValue());
                        tvTitle.setText(nombreDesafio[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", getString(R.string.listado_experiencias_error_consulta) + error.getMessage());
            }

        };
        refDesafios.addValueEventListener(listener);

        return nombreDesafio[0];
    }

    // Clic en la flecha atrás
    public void volverAtras(View view) {
        Intent intent = new Intent(ListadoExperiencias.this, InicioFragment.class);
        startActivity(intent);
        finish();
    }
}
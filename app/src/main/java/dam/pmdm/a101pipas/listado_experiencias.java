package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.util.Log;
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

public class listado_experiencias extends AppCompatActivity {

    RecyclerView recyclerView;
    ExperienciasListAdapter adapter;
    List<Experiencias> experienciaList;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_experiencias);

        // Nombre del desafío
        String nombreDesafio = "101Croquetas";

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
        ref = FirebaseDatabase.getInstance().getReference("desafios").child(nombreDesafio).child("experiencias");

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
                        Experiencias experiencia = new Experiencias(
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
                tvProgress.setText("Progreso: " + experienciasCompletadas + " / " + totalExperiencias + " experiencias completadas");

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error al cargar los datos: " + error.getMessage());
            }
        });
    }
}



/*
Pasar desafio
Intent intent = new Intent(this, listado_experiencias.class);
intent.putExtra("desafioId", "Desafio1"); // Cambia "Desafio1" por el ID dinámico del desafío
startActivity(intent);
*/
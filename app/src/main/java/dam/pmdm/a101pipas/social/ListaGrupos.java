package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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


public class ListaGrupos extends AppCompatActivity {

    private RecyclerView rvListaGrupos;
    private GruposAdapter gruposAdapter;
    private List<Grupo> gruposList;
    private EditText searchBar;
    private ImageView btnBack;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_grupos);

        // Vincular elementos del layout
        rvListaGrupos = findViewById(R.id.rv_lista_amigos); // Asegúrate que el ID coincida en XML
        searchBar = findViewById(R.id.search_bar);
        btnBack = findViewById(R.id.btn_back);
        dbRef = FirebaseDatabase.getInstance().getReference("grupos"); // Referencia a "grupos" en Firebase

        gruposList = new ArrayList<>();
        gruposAdapter = new GruposAdapter(gruposList);
        rvListaGrupos.setLayoutManager(new LinearLayoutManager(this));
        rvListaGrupos.setAdapter(gruposAdapter);

        // Cargar los grupos desde Firebase
        cargarListaGruposDesdeFirebase();

        // Botón para volver atrás
        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarListaGruposDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gruposList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    // Obtener el ID del grupo (clave del nodo)
                    String idGrupo = data.getKey(); // Esto tomará "0", "1", "migrupo", etc.
                    String nombreGrupo = data.child("titulo").getValue(String.class);
                    String fotoGrupo = data.child("foto_grupo").getValue(String.class);

                    // Si la imagen es null o vacía, asignamos una imagen por defecto
                    if (fotoGrupo == null || fotoGrupo.isEmpty()) {
                        fotoGrupo = "https://example.com/default_group.png"; // URL de imagen por defecto
                    }

                    if (nombreGrupo != null) {
                        gruposList.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo)); // Pasamos el ID al modelo
                    }
                }

                gruposAdapter.setGruposList(gruposList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaGrupos.this, "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

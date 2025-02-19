package dam.pmdm.a101pipas.social;

import android.os.Bundle;
import android.view.View;
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
import dam.pmdm.a101pipas.models.Amigos;

public class ListaAmigos extends AppCompatActivity {

    private RecyclerView rvListaAmigos;
    private AmigosAdapter amigosAdapter;
    private List<Amigos> amigosList;
    private EditText searchBar;
    private ImageView btnBack;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        rvListaAmigos = findViewById(R.id.rv_lista_amigos);
        searchBar = findViewById(R.id.search_bar);
        btnBack = findViewById(R.id.btn_back);
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios"); // Cambiamos Firestore por Realtime Database

        amigosList = new ArrayList<>();
        amigosAdapter = new AmigosAdapter(amigosList);
        rvListaAmigos.setLayoutManager(new LinearLayoutManager(this));
        rvListaAmigos.setAdapter(amigosAdapter);

        // Cargar amigos desde Firebase Realtime Database
        cargarListaAmigosDesdeFirebase();

        // Botón de regreso
        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarListaAmigosDesdeFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear(); // Limpiar lista antes de agregar datos nuevos

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);

                    if (username != null && fotoPerfil != null) {
                        amigosList.add(new Amigos(username, fotoPerfil));
                    }
                }

                amigosAdapter.setAmigosList(amigosList); // Actualizar adaptador con la lista de amigos
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaAmigos.this, "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

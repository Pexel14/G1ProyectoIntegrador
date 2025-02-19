package dam.pmdm.a101pipas.social;

import android.content.Intent;
import android.os.Bundle;
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

public class SocialPrincipal extends AppCompatActivity {

    private RecyclerView rvAmigos, rvGrupos;
    private SocialAdapter amigosAdapter, gruposAdapter;
    private List<Amigos> amigosList;
    private List<Grupo> gruposList;
    private DatabaseReference amigosRef, gruposRef;
    private ImageView btnMasAmigos, btnMasGrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_principal);

        // Vincular elementos del layout
        rvAmigos = findViewById(R.id.rv_amigos);
        rvGrupos = findViewById(R.id.rv_grupos);
        btnMasAmigos = findViewById(R.id.btn_mas_amigos);
        btnMasGrupos = findViewById(R.id.btn_mas_grupos);

        // Referencias en Firebase
        amigosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        gruposRef = FirebaseDatabase.getInstance().getReference("grupos");

        // Inicializar listas
        amigosList = new ArrayList<>();
        gruposList = new ArrayList<>();

        // Inicializar adaptadores
        amigosAdapter = new SocialAdapter(amigosList);
        gruposAdapter = new SocialAdapter(gruposList);

        // Configurar RecyclerViews
        rvAmigos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGrupos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvAmigos.setAdapter(amigosAdapter);
        rvGrupos.setAdapter(gruposAdapter);

        // Cargar datos desde Firebase
        cargarAmigos();
        cargarGrupos();

        // Botón para ver más amigos
        btnMasAmigos.setOnClickListener(v -> startActivity(new Intent(SocialPrincipal.this, ListaAmigos.class)));

        // Botón para ver más grupos
        btnMasGrupos.setOnClickListener(v -> startActivity(new Intent(SocialPrincipal.this, ListaGrupos.class)));
    }

    private void cargarAmigos() {
        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amigosList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String fotoPerfil = data.child("foto_perfil").getValue(String.class);
                    amigosList.add(new Amigos(username, fotoPerfil != null ? fotoPerfil : ""));
                }
                amigosAdapter.setListaSocial(amigosList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SocialPrincipal.this, "Error al cargar amigos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarGrupos() {
        gruposRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gruposList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String idGrupo = data.getKey();
                    String nombreGrupo = data.child("titulo").getValue(String.class);
                    String fotoGrupo = data.child("foto_grupo").getValue(String.class);

                    gruposList.add(new Grupo(idGrupo, nombreGrupo, fotoGrupo));

                }
                gruposAdapter.setListaSocial(gruposList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SocialPrincipal.this, "Error al cargar grupos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

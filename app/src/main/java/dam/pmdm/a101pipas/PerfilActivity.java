package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.models.Desafio;

public class PerfilActivity extends AppCompatActivity {

    // TODO: Colocar texto info usuario del singleton

    // Inicalizamos RecyclerView
    RecyclerView recyclerView;

    // Creamos un adaptador
    DesafioPerfilRecyclerAdapter adapter;

    // Creamos la lista de usuarios
    List<Desafio> desafioList;

    // Inicializamos una instancia de DatabaseReference
    DatabaseReference ref;

    FirebaseUser usuarioActual;

    String usuarioId;

    TextView tvNick, tvNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioActual != null ? usuarioActual.getUid() : null;

        tvNick = findViewById(R.id.txt_nombre);

        tvNick.setText(usuarioActual.getDisplayName());

        recyclerView = findViewById(R.id.rv_desafios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de elementos del RV
        desafioList = new ArrayList<>();

        // Configuramos el adaptador y asignamos al RV
        adapter = new DesafioPerfilRecyclerAdapter(desafioList);
        recyclerView.setAdapter(adapter);

        // Obtenemos los datos de Firebase RealtimeDatabase
        ref = FirebaseDatabase.getInstance().getReference("desafios");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                desafioList.clear();
                String title, id;
                Desafio desafio;
                for(DataSnapshot data : snapshot.getChildren()){
                    title = data.child("titulo").getValue(String.class);

                    desafio = new Desafio(title, 10);
                    desafioList.add(desafio);

                }

                // Método para redibujar cada vez que se detecte un cambio
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error rv perfil desafios" + error.getMessage());
            }
        });
    }
}
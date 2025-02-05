package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dam.pmdm.a101pipas.models.User;

public class RankingGlobal extends AppCompatActivity {
    private static final String TAG = "Ranking";
    RecyclerView recyclerView;
    RankingRecyclerAdapter adapter;
    FirebaseDatabase firebase;
    ArrayList<User> listaUsuarios;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking_global);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rvRanking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();

        adapter = new RankingRecyclerAdapter(listaUsuarios);
        recyclerView.setAdapter(adapter);

        ref = firebase.getInstance().getReference("usuarios");
        ref.orderByChild("experiencias_completadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                String nombre;
                Integer completadas;

                for (DataSnapshot subNodo : snapshot.getChildren()){
                    nombre = subNodo.child("username").getValue(String.class);
                    completadas = subNodo.child("experiencias_completadas").getValue(Integer.class);
                    if(completadas != null){
                        listaUsuarios.add(new User(completadas, nombre));
                    }
                }
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error al acceder a la base de datos");
            }
        });

    }
}
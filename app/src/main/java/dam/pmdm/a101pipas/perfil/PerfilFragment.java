package dam.pmdm.a101pipas.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;

public class PerfilFragment extends Fragment {

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

    ImageButton ibtnAjustes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioActual != null ? usuarioActual.getUid() : null;

        tvNick = view.findViewById(R.id.txt_nombre);
        ibtnAjustes = view.findViewById(R.id.ibtnAjustes);

        tvNick.setText(usuarioActual.getDisplayName());

        ibtnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AjustesActivity.class);
                startActivity(i);
            }
        });

        recyclerView = view.findViewById(R.id.rv_desafios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return view;

    }

}
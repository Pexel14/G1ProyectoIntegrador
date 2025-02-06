package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Inicio extends AppCompatActivity {

    DatabaseReference refDesafiosUsuario;
    DatabaseReference refDesafios;
    FirebaseDatabase firebase;
    ValueEventListener listener;

    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        usuario = getIntent().getStringExtra("usuario");
        firebase = FirebaseDatabase.getInstance(); // Inicializa Firebase correctamente
        refDesafiosUsuario = firebase.getReference("usuarios").child(usuario).child("desafios"); // Apunta a los desafíos del usuario
        refDesafios = firebase.getReference("desafios");

        limpiarFragmentos();
        cargarFragmentos();
    }

    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
    private void cargarFragmentos() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                limpiarFragmentos();

                if (snapshot.exists()) {
                    String[] desafiosID = snapshot.getValue().toString().split(",");
                    cargarDesafiosPorId(desafiosID);
                } else {
                    Toast.makeText(Inicio.this, "No tienes desafíos iniciados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error en la consulta: " + error.getMessage());
            }
        };

        refDesafiosUsuario.addValueEventListener(listener);
    }

    private void cargarDesafiosPorId(String[] desafiosId) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot desafio : snapshot.getChildren()) {
                    for (int i=0; i<desafiosId.length; i++) {
                        String desafioId = desafio.child("id").getValue().toString();
                        if (desafioId.equals(desafiosId[i])) { // Si el campo 'id' del desafío está en la lista de desafíos del usuario
                            Log.d("Firebase", usuario + " tiene está en el desafío con ID " + desafiosId[i] + ", cuyo nombre es '" + desafio.child("titulo").getValue() + "'");
                            String titulo = desafio.child("titulo").getValue(String.class);
                            String[] etiquetas = desafio.child("etiquetas").getValue(String.class).split(",");
                            String descripcion = desafio.child("descripcion").getValue(String.class);
                            String ciudad = desafio.child("ciudad").getValue(String.class);

                            TarjetaDesafioInicioFragment fragment = TarjetaDesafioInicioFragment.newInstance(titulo, etiquetas, descripcion, ciudad, desafio.getKey());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.contenedorFragmentsInicio, fragment)
                                    .commit();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error en la consulta: " + error.getMessage());
            }

        };
        refDesafios.addValueEventListener(listener);
    }

    private void mostrarMensajeCeroDesafios() {
        // Si no hay fragments, muestra un mensaje
        TextView tvMensajeCeroFragments;
        tvMensajeCeroFragments = findViewById(R.id.tvMensajeCeroFragmentsInicio);
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            tvMensajeCeroFragments.setVisibility(View.VISIBLE);
        } else {
            tvMensajeCeroFragments.setVisibility(View.GONE);
        }
    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioInicioFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
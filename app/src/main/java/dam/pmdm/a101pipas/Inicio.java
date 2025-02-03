package dam.pmdm.a101pipas;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import dam.pmdm.a101pipas.databinding.ActivityInicioBinding;
import dam.pmdm.a101pipas.databinding.FragmentTarjetaDesafioInicioBinding;

public class Inicio extends AppCompatActivity {

//    FragmentTarjetaDesafioInicioBinding binding;
    DatabaseReference ref;
    FirebaseDatabase firebase;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        firebase = FirebaseDatabase.getInstance(); // Inicializa Firebase correctamente
        ref = firebase.getReference("desafios"); // Apunta a "desafios"

        limpiarFragmentos();
        cargarFragmentos();
    }

//    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente

    private void cargarFragmentos() {

        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                limpiarFragmentos();

                Log.d("Firebase", "Listener registrado");

                if (snapshot.exists()) { // 'snapshot!=null' siempre es 'true'

                    for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                        String nodeName = nodeSnapshot.getKey();  // Obtén el nombre del nodo (nodo1, nodo2)
                        Log.d("Firebase", "Nodo: " + nodeName);

                        String titulo = nodeSnapshot.child("titulo").getValue(String.class);
                        String[] etiquetas = nodeSnapshot.child("etiquetas").getValue(String.class).split(",");
                        String descripcion = nodeSnapshot.child("descripcion").getValue(String.class);
                        String ciudad = nodeSnapshot.child("ciudad").getValue(String.class);

                        TarjetaDesafioInicioFragment fragment = TarjetaDesafioInicioFragment.newInstance(titulo, etiquetas, descripcion, ciudad);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.contenedorFragmentsInicio, fragment)
                                .commit();

                    }

                    Toast.makeText(Inicio.this, "Lectura correcta", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Inicio.this, "El desafio no existe", Toast.LENGTH_SHORT).show();
                }

                // Aseguramos que las transacciones de fragments se procesen antes de actualizar el mensaje
                getSupportFragmentManager().executePendingTransactions();

                mostrarMensajeCeroDesafios();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error en la consulta: " + error.getMessage());
            }

        };

        ref.addValueEventListener(listener); // Antes estábamos intentando meter el listener mientras lo creábamos

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
package dam.pmdm.a101pipas.desafios;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.R;

public class InicioFragment extends Fragment {

    private DatabaseReference refDesafiosUsuario;
    private DatabaseReference refDesafios;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;

    private String usuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        usuario = sharedPreferences.getString("usuario", "usuario");

        firebase = FirebaseDatabase.getInstance(); // Inicializa Firebase correctamente
        refDesafiosUsuario = firebase.getReference("usuarios").child(usuario).child("desafios"); // Apunta a los desafíos del usuario
        refDesafios = firebase.getReference("desafios");

        limpiarFragmentos();
        cargarFragmentos();

        return view;
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
                    Toast.makeText(getContext(), R.string.inicio_fragment_no_desafios_iniciados, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", getString(R.string.inicio_fragment_error_consulta) + error.getMessage());
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
                            Log.d("Firebase", usuario + getString(R.string.inicio_fragment_log_desafio_1) + desafiosId[i] + getString(R.string.inicio_fragment_log_desafio_2) + desafio.child("titulo").getValue() + getString(R.string.inicio_fragment_coma));
                            String titulo = desafio.child("titulo").getValue(String.class);
                            String[] etiquetas = desafio.child("etiquetas").getValue(String.class).split(",");
                            String descripcion = desafio.child("descripcion").getValue(String.class);
                            String ciudad = desafio.child("ciudad").getValue(String.class);

                            TarjetaDesafioInicioFragment fragment = TarjetaDesafioInicioFragment.newInstance(titulo, etiquetas, descripcion, ciudad, desafio.getKey());
                            getChildFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.contenedorFragmentsInicio, fragment)
                                    .commit();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", R.string.inicio_fragment_error_consulta + error.getMessage());
            }

        };
        refDesafios.addValueEventListener(listener);
    }

    private void mostrarMensajeCeroDesafios(View view) {
        // Si no hay fragments, muestra un mensaje
        TextView tvMensajeCeroFragments;
        tvMensajeCeroFragments = view.findViewById(R.id.tvMensajeCeroFragmentsInicio);
        if (getChildFragmentManager().getFragments().isEmpty()) {
            tvMensajeCeroFragments.setVisibility(View.VISIBLE);
        } else {
            tvMensajeCeroFragments.setVisibility(View.GONE);
        }
    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioInicioFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
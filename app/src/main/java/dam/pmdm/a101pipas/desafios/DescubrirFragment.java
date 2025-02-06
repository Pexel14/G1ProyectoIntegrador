package dam.pmdm.a101pipas.desafios;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.R;

public class DescubrirFragment extends Fragment {

    private DatabaseReference ref;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;

    private TextView filtro1;
    private TextView filtro2;
    private TextView filtro3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descubrir, container, false);

        firebase = FirebaseDatabase.getInstance(); // Inicializa Firebase correctamente
        ref = firebase.getReference("desafios"); // Apunta a "desafios"

        filtro1 = view.findViewById(R.id.tvFiltro1Descubrir);
        filtro2 = view.findViewById(R.id.tvFiltro2Descubrir);
        filtro3 = view.findViewById(R.id.tvFiltro3Descubrir);

        limpiarFragmentos();
        cargarFragmentos();

        return view;
    }

    // De momento éste método son pruebas manuales
    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
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
                        String sEtiquetas = nodeSnapshot.child("etiquetas").getValue(String.class);
                        String[] etiquetas = sEtiquetas.split(",");
                        String descripcion = nodeSnapshot.child("descripcion").getValue(String.class);
                        String ciudad = nodeSnapshot.child("ciudad").getValue(String.class);

                        // Los fragments los creo dentro de los if's porque un fragment no se puede poner en dos contenedores, asi que si un desafío cumple
                        // dos condiciones, crea dos idénticos y asigna cada uno a su filtro
                        if (ciudad.equals(filtro1.getText())) {
                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad);

                            getChildFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir, fragment)
                                    .commit();
                        }

                        if (sEtiquetas.contains("Gastronomía")) {
                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad);

                            getChildFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir2, fragment)
                                    .commit();
                        }

                        if (sEtiquetas.contains("Cultura")) {
                            TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad);

                            getChildFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.contenedorFragmentsDescubrir3, fragment)
                                    .commit();
                        }

                    }

                } else {
                    Toast.makeText(getContext(), "El desafio no existe", Toast.LENGTH_SHORT).show();
                }

                // Aseguramos que las transacciones de fragments se procesen antes de actualizar el mensaje
                getChildFragmentManager().executePendingTransactions();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error en la consulta: " + error.getMessage());
            }

        };

        ref.addValueEventListener(listener); // Antes estábamos intentando meter el listener mientras lo creábamos

    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

}
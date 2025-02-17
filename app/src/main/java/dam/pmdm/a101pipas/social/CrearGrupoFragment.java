package dam.pmdm.a101pipas.social;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.MainActivity;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.desafios.TarjetaDesafioDescubrirFragment;

public class CrearGrupoFragment extends Fragment {

    private DatabaseReference ref;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;

    private HorizontalScrollView hsvElegirDesafioCrearGrupo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_grupo, container, false);

        firebase = FirebaseDatabase.getInstance();
        ref = firebase.getReference("desafios");

        limpiarFragments();
        cargarFragments();

        return view;

    }

    private void cargarFragments() {
        limpiarFragments();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                limpiarFragments();

                if (snapshot.exists()) {
                    for (DataSnapshot grupo : snapshot.getChildren()) {

                        String key = grupo.getKey().toString();
                        String titulo = grupo.child("titulo").getValue(String.class);
                        String ciudad = grupo.child("ciudad").getValue(String.class);

                        Log.d("FirebaseData", "Key: " + key + ", Titulo: " + titulo + ", Ciudad: " + ciudad);

                        TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, key);

                        getChildFragmentManager()
                                .beginTransaction()
                                .add(R.id.contenedorFragmentsCrearGrupo, fragment)
                                .commit();

                    }
                } else {
                    Toast.makeText(getContext(), "No existen desafíos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", getString(R.string.descubrir_fragment_error_consulta) + error.getMessage());
            }
        };

        ref.addValueEventListener(listener);
    }

    private void limpiarFragments() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

}
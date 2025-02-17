package dam.pmdm.a101pipas.ranking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Grupo;

public class GrupoViewModel extends ViewModel {

    private MutableLiveData<Grupo> grupoLiveData;
    private int idGrupo;

    public GrupoViewModel() {
        grupoLiveData = new MutableLiveData<>();
    }

    public LiveData<Grupo> getGrupo() {
        return grupoLiveData;
    }

    public void setIdGrupo(int id) {
        this.idGrupo = id;
        cargarGrupo();
    }

    // Método para cargar los datos del grupo desde Firebase usando el ID
    private void cargarGrupo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("grupos").child(String.valueOf(idGrupo));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String titulo = dataSnapshot.child("titulo").getValue(String.class);
                    String contrasena = dataSnapshot.child("contraseña").getValue(String.class);

                    Grupo grupo = new Grupo(titulo, contrasena);
                    grupoLiveData.setValue(grupo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(R.string.error_al_leer_el_grupo + error.getMessage());
            }
        });
    }
}
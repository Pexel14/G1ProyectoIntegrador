package dam.pmdm.a101pipas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        limpiarFragmentos();
        cargarFragmentos();
    }

    // De momento éste método son pruebas manuales
    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
    private void cargarFragmentos() {
        String[] etiquetas = {"+18", "epico"};
        String[] etiquetas2 = {};
        String[] etiquetas3 = {"Bueno", "Bonito", "Balatro", "Mbappe"};

        TarjetaDesafioFragment fragment = TarjetaDesafioFragment.newInstance("101 croquetas", etiquetas, "El desafío más chulo de la historia", "Madrid");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment).commit();

        TarjetaDesafioFragment fragment2 = TarjetaDesafioFragment.newInstance("101 partidos", etiquetas2, "El desafío más chachipistachi de Alcobendas", "Alcobendas");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment2).commit();

        TarjetaDesafioFragment fragment3 = TarjetaDesafioFragment.newInstance("101 cositas", etiquetas3, "No se ni q poner ya xd", "Napoleón Bonaparte");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment3).commit();
    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
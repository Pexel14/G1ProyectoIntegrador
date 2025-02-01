package dam.pmdm.a101pipas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

        // Crear los fragments (de momento crea fragments falsos)
        String[] etiquetas = {"Gastronomía"};
        String[] etiquetas2 = {"+18", "Ocio", "Humor"};
        String[] etiquetas3 = {"Cultura", "Arte"};

        TarjetaDesafioInicioFragment fragment = TarjetaDesafioInicioFragment.newInstance("101 croquetas", etiquetas, "Ricas y caseras, como las de tu abuela", "Asturias");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment).commit();

        TarjetaDesafioInicioFragment fragment2 = TarjetaDesafioInicioFragment.newInstance("101 monólogos", etiquetas2, "El desafío más tronchante.", "Madrid");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment2).commit();

        TarjetaDesafioInicioFragment fragment3 = TarjetaDesafioInicioFragment.newInstance("101 cuadros", etiquetas3, "Los cuadros más importantes e impactantes de La Rioja", "La Rioja");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsInicio, fragment3).commit();

        // Si no hay fragments, muestra un mensaje
        TextView tvMensajeCeroFragments;
        tvMensajeCeroFragments = findViewById(R.id.tvMensajeCeroFragmentsInicio);
        if (getSupportFragmentManager().getFragments().size() == 0) {
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
package dam.pmdm.a101pipas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Descubrir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descubrir);
        limpiarFragmentos();
        cargarFragmentos();
    }

    // De momento éste método son pruebas manuales
    // Tras crear el fragment, basado en la condición del desafío usar un .setBackground para poner el color correspondiente
    private void cargarFragmentos() {

        // Crear los fragments (de momento crea fragments falsos)
            // Madrid (no gastronomía o cultura)
        TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance("101 recreativos", "Madrid");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir, fragment).commit();

        TarjetaDesafioDescubrirFragment fragment2 = TarjetaDesafioDescubrirFragment.newInstance("101 monólogos", "Madrid");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir, fragment2).commit();

        TarjetaDesafioDescubrirFragment fragment3 = TarjetaDesafioDescubrirFragment.newInstance("101 spas", "Madrid");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir, fragment3).commit();

            // Gastronomía (no Madrid)
        TarjetaDesafioDescubrirFragment fragment4 = TarjetaDesafioDescubrirFragment.newInstance("101 hamburguesas", "Navarra");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir2, fragment4).commit();

        TarjetaDesafioDescubrirFragment fragment5 = TarjetaDesafioDescubrirFragment.newInstance("101 vinos", "Murcia");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir2, fragment5).commit();

        TarjetaDesafioDescubrirFragment fragment6 = TarjetaDesafioDescubrirFragment.newInstance("101 croquetas", "Asturias");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir2, fragment6).commit();

            // Cultura (no Madrid)
        TarjetaDesafioDescubrirFragment fragment7 = TarjetaDesafioDescubrirFragment.newInstance("101 estatuas","Cantabria");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir3, fragment7).commit();

        TarjetaDesafioDescubrirFragment fragment8 = TarjetaDesafioDescubrirFragment.newInstance("101 cuadros","La Rioja");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir3, fragment8).commit();

        TarjetaDesafioDescubrirFragment fragment9 = TarjetaDesafioDescubrirFragment.newInstance("101 lugares emblemáticos","Asturias");
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsDescubrir3, fragment9).commit();

    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

}
package dam.pmdm.a101pipas;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RutaRespuestaGoogleMaps {
    @SerializedName("routes")
    private List<Ruta> rutas;

    public List<Ruta> getRutas() {
        return rutas;
    }

    public static class Ruta {
        @SerializedName("overview_polyline")
        private PolylineRuta polyline;

        public PolylineRuta getPolyline() {
            return polyline;
        }
    }

    public static class PolylineRuta {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }
    }
}

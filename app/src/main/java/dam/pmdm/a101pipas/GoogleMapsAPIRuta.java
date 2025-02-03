package dam.pmdm.a101pipas;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsAPIRuta {
    @GET("maps/api/directions/json")
    Call<RutaRespuestaGoogleMaps> obtenerRuta(
            @Query("origin") String origen,
            @Query("destination") String destino,
            @Query("mode") String modo,
            @Query("key") String apiKey
    );
}

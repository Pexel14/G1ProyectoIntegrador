package dam.pmdm.a101pipas.geolocalizacion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.List;

import dam.pmdm.a101pipas.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeolocalizacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String desafioAct; // Lo inicializo en onCreate

    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private LatLng coordenadasAct;
    private Marker markerUsuario;
    private LatLngBounds.Builder areaMarcadores;
    private LatLngBounds.Builder areaUsuarioExpCerca;

    private Polyline rutaMasCercana;
    private LatLng experienciaMasCercana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_geolocalizacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        desafioAct = getIntent().getStringExtra("id_desafio");

        areaMarcadores = new LatLngBounds.Builder();
        areaUsuarioExpCerca = new LatLngBounds.Builder();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Asocio fragment con mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fgMap);
        if (mapFragment != null) {
            // CUando esté listo llama al método onMapReady
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton btnMiUbicacion = findViewById(R.id.btnMiUbicacion);
        FloatingActionButton btnTodasUbicaciones = findViewById(R.id.btnTodasUbicaciones);

        btnTodasUbicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    zoomCamaraGeneral();

                }
            }
        });

        btnMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarPermisoUbicacion()) {
                    if (coordenadasAct != null) {
                        zoomCamaraUsuarioExpMasCercana();
                    } else {
                        Toast.makeText(GeolocalizacionActivity.this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pedirPermisoUbicacion();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (!comprobarPermisoUbicacion()) {
            pedirPermisoUbicacion();
        } else {
            getUbicacionActual();  // Si ya tiene permiso, obtiene la ubicación directamente
        }

        cargarExperiencias();

        // Como llegar en Google Maps andando
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                LatLng position = marker.getPosition();
                String uri = "google.navigation:q=" + position.latitude + "," + position.longitude + "&mode=w";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

    }


    private void getUbicacionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisoUbicacion();
            return;
        }

        Task<Location> localizacionActual = fusedLocationClient.getLastLocation();
        localizacionActual.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    coordenadasAct = new LatLng(location.getLatitude(), location.getLongitude());

                    // Eliminar el marcador anterior del usuario si existe
                    if (markerUsuario != null) {
                        markerUsuario.remove();
                    }

                    markerUsuario = mMap.addMarker(new MarkerOptions().position(coordenadasAct).title("Aventurero").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizacion_usuario)));

                    areaMarcadores.include(coordenadasAct);
                    areaUsuarioExpCerca.include(coordenadasAct);

                    zoomCamaraUsuarioExpMasCercana();

                    cargarExperiencias();

                } else {
                    Toast.makeText(GeolocalizacionActivity.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean comprobarPermisoUbicacion() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermisoUbicacion() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                getUbicacionActual();
            } else {
                Toast.makeText(this, "Se recomienda conceder permisos de ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarExperiencias() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("desafios").child(desafioAct).child("experiencias");

        // Método para cargar los datos una vez y no volver a hacerlo ni seguir escuchando cambios
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String titulo;
                String coordenadas;
                String[] aCoordenadas;
                double distanciaMinima = Double.MAX_VALUE;
                for (DataSnapshot experiencia : snapshot.getChildren()) {
                    titulo = experiencia.child("titulo").getValue(String.class);
                    coordenadas = experiencia.child("coordenadas").getValue(String.class);

                    if (coordenadas != null) {
                        aCoordenadas = coordenadas.split(",");
                        LatLng posicion = new LatLng(Double.parseDouble(aCoordenadas[0]), Double.parseDouble(aCoordenadas[1]));
                        areaMarcadores.include(posicion);

                        mMap.addMarker(new MarkerOptions()
                                        .position(posicion)
                                        .title(titulo)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                                .showInfoWindow();

                        if (coordenadasAct != null) {
                            double distancia = calcularDistancia(coordenadasAct, posicion);
                            if (distancia < distanciaMinima) {
                                distanciaMinima = distancia;
                                experienciaMasCercana = posicion;
                            }
                        }

                    }


                }

                if (experienciaMasCercana != null) {
                    areaUsuarioExpCerca.include(experienciaMasCercana);
                }

                if (coordenadasAct != null) {
                    areaMarcadores.include(coordenadasAct);
                    areaUsuarioExpCerca.include(coordenadasAct);
                }

                if (!comprobarPermisoUbicacion()) {
                    zoomCamaraGeneral();
                } else {
                    zoomCamaraUsuarioExpMasCercana();
                }


                if (coordenadasAct != null && experienciaMasCercana != null) {
                    trazarRuta(coordenadasAct, experienciaMasCercana);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error al leer experiencias bbdd:\n" + error.getMessage());
            }
        });
    }

    private void zoomCamaraUsuarioExpMasCercana() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(areaUsuarioExpCerca.build(), 100));
    }

    private void zoomCamaraGeneral() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(areaMarcadores.build(), 100));
    }

    private double calcularDistancia(LatLng posUsuario, LatLng posExp) {
        float[] results = new float[1];
        Location.distanceBetween(posUsuario.latitude, posUsuario.longitude, posExp.latitude, posExp.longitude, results);
        return results[0]; // Retorno la distancia que está almacenada en el espacio 0
    }

    private void trazarRuta(LatLng origen, LatLng destino) {

        String origenStr = origen.latitude + "," + origen.longitude;
        String destinoStr = destino.latitude + "," + destino.longitude;
        String apiKeyGoogle = "AIzaSyAdPtmRtCHZXetR4pBDpn5Y2uukwocQUr0";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsAPIRuta servicioGoogle = retrofit.create(GoogleMapsAPIRuta.class);
        Call<RutaRespuestaGoogleMaps> llamada = servicioGoogle.obtenerRuta(origenStr, destinoStr, "walking", apiKeyGoogle);

        llamada.enqueue(new Callback<RutaRespuestaGoogleMaps>() {
            @Override
            public void onResponse(Call<RutaRespuestaGoogleMaps> call, Response<RutaRespuestaGoogleMaps> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<RutaRespuestaGoogleMaps.Ruta> rutas = response.body().getRutas();
                    if (!rutas.isEmpty()) {
                        String puntosCodificados = rutas.get(0).getPolyline().getPoints();
                        List<LatLng> puntosRuta = PolyUtil.decode(puntosCodificados);

                        if (rutaMasCercana != null) {
                            rutaMasCercana.remove();
                        }

                        List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(10));

                        rutaMasCercana = mMap.addPolyline(new PolylineOptions()
                                .addAll(puntosRuta).width(20).color(Color.BLUE).pattern(pattern));
                    }
                } else {
                    Toast.makeText(GeolocalizacionActivity.this, "No se pudo obtener la ruta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RutaRespuestaGoogleMaps> call, Throwable t) {
                System.out.println("Error conseguir ruta más cercana con GoogleMaps" + t.getMessage());
            }
        });
    }

}
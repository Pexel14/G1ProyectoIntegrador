package dam.pmdm.a101pipas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GeolocalizacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference dbRef;
    // TODO: desafioAct sacar el nombre del desafío con el intent
    //String desafioAct = getIntent().getStringExtra("nombreDesafio");

    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    LatLng coordenadasAct;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Asocio fragment con mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fgMap);
        if (mapFragment != null) {
            // CUando esté listo llama al método onMapReady
            mapFragment.getMapAsync(this);
        }

        if (!comprobarPermisoUbicacion()) {
            pedirPermisoUbicacion();
        } else {
            getUbicacionActual();  // Si ya tiene permiso, obtiene la ubicación directamente
        }

        FloatingActionButton btnMiUbicacion = findViewById(R.id.btnMiUbicacion);

        btnMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarPermisoUbicacion()) {
                    // Si ya tienes permiso, obtener la ubicación actual
                    getUbicacionActual();
                } else {
                    // Si no tienes permiso, solicita el permiso
                    pedirPermisoUbicacion();
                }
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

                    System.out.println("Ha movido camara");

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadasAct, 13f));

                    mMap.addMarker(new MarkerOptions().position(coordenadasAct).title("Aventurero").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizacion_usuario)));
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
                Toast.makeText(this, "Si quiere mejorar su experiencia conceda permisos de ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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

    private void cargarExperiencias() {
        // TODO: cambiar por desafioAct
        dbRef = FirebaseDatabase.getInstance().getReference("desafios").child("101Monologos").child("experiencias");
        // Método para cargar los datos una vez y no volver a hacerlo ni seguir escuchando cambios
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String titulo;
                String coordenadas;
                String[] aCoordenadas;
                for (DataSnapshot experiencia: snapshot.getChildren()) {
                    titulo = experiencia.child("titulo").getValue(String.class);
                    coordenadas = experiencia.child("coordenadas").getValue(String.class);

                    if (coordenadas != null) {
                        aCoordenadas = coordenadas.split(",");
                        LatLng posicion = new LatLng(Double.parseDouble(aCoordenadas[0]), Double.parseDouble(aCoordenadas[1]));

                        mMap.addMarker(new MarkerOptions()
                                        .position(posicion)
                                        .title(titulo)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                                .showInfoWindow();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error al leer experiencias bbdd:\n" + error.getMessage());
            }
        });
    }
}
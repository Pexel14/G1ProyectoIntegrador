package dam.pmdm.a101pipas.geolocalizacion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentGeolocalizacionBinding;
import dam.pmdm.a101pipas.models.Experiencia;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeolocalizacionFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private LatLng coordenadasAct;
    private Marker markerUsuario;
    private LatLngBounds.Builder areaMarcadores;
    private LatLngBounds.Builder areaUsuarioExpCerca;

    private Polyline rutaMasCercana;
    private LatLng experienciaMasCercana;

    private FragmentGeolocalizacionBinding binding;

    private GeolocalizacionViewModel viewModel;

    private boolean isZoomedToExperience = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGeolocalizacionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(GeolocalizacionViewModel.class);

        areaMarcadores = new LatLngBounds.Builder();
        areaUsuarioExpCerca = new LatLngBounds.Builder();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Asocio fragment con mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fgMap);
        if (mapFragment != null) {
            // CUando esté listo llama al método onMapReady
            mapFragment.getMapAsync(this);
        }

        binding.btnTodasUbicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    zoomCamaraGeneral();

                }
            }
        });

        binding.btnMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarPermisoUbicacion()) {
                    if (coordenadasAct != null) {
                        zoomCamaraUsuarioExpMasCercana();
                    } else {
                        Toast.makeText(getActivity(), R.string.geolocalizacion_ubicacion_no_disponible, Toast.LENGTH_SHORT).show();
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
            getUbicacionActual();
        }

        cargarExperiencias();

        if (mMap != null && coordenadasAct != null) {
            markerUsuario = mMap.addMarker(new MarkerOptions()
                    .position(coordenadasAct)
                    .title("Aventurero")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizacion_usuario)));
        }

        viewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> updateRouteIfReady());
        viewModel.getDestinoExperiencia().observe(getViewLifecycleOwner(), destino -> updateRouteIfReady());

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

        mMap.setOnMarkerClickListener(marker -> {
            if (coordenadasAct != null) {
                trazarRuta(coordenadasAct, marker.getPosition());
            }
            return false;
        });
    }

    private void updateRouteIfReady() {
        LatLng current = viewModel.getCurrentLocation().getValue();
        LatLng destination = viewModel.getDestinoExperiencia().getValue();

        if (destination != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
            if (current != null) {
                trazarRuta(current, destination);
            } else {
                Toast.makeText(getActivity(), "Obteniendo ubicación actual...", Toast.LENGTH_SHORT).show();
            }
            isZoomedToExperience = true;
        }
    }

    private void getUbicacionActual() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermisoUbicacion();
            return;
        }

        Task<Location> localizacionActual = fusedLocationClient.getLastLocation();
        localizacionActual.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    coordenadasAct = new LatLng(location.getLatitude(), location.getLongitude());
                    viewModel.setCurrentLocation(coordenadasAct);

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
                    Toast.makeText(getActivity(), R.string.geolocalizacion_ubicacion_actual_inobtenible, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean comprobarPermisoUbicacion() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
                Toast.makeText(getActivity(), R.string.geolocalizacion_recomendacion_permisos_ubicacion, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarExperiencias() {


        viewModel.getExperiencias().observe(getViewLifecycleOwner(), experiencias -> {
            if (experiencias != null) {

                mMap.clear();

                String titulo;
                String coordenadas;
                String[] aCoordenadas;
                LatLng posicion;
                double distanciaMinima = Double.MAX_VALUE;
                for (Experiencia exp : experiencias) {
                    titulo = exp.getTitulo();
                    coordenadas = exp.getCoordenadas();

                    if (coordenadas != null) {
                        aCoordenadas = coordenadas.split(",");
                        posicion = new LatLng(Double.parseDouble(aCoordenadas[0]), Double.parseDouble(aCoordenadas[1]));
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

                if (!comprobarPermisoUbicacion() || coordenadasAct == null) {
                    zoomCamaraGeneral();
                } else {
                    zoomCamaraUsuarioExpMasCercana();
                }

                if (coordenadasAct != null && experienciaMasCercana != null) {
                    trazarRuta(coordenadasAct, experienciaMasCercana);
                }
            }
        });
    }

    private void zoomCamaraUsuarioExpMasCercana() {
        if (!isZoomedToExperience) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(areaUsuarioExpCerca.build(), 100));
        }
    }

    private void zoomCamaraGeneral() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(areaMarcadores.build(), 100));
        isZoomedToExperience = false;
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
                    Toast.makeText(getActivity(), R.string.geolocalizacion_ruta_inobtenible, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RutaRespuestaGoogleMaps> call, Throwable t) {
                System.out.println(getString(R.string.geolocalizacion_error_ruta_cercana) + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
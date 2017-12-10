package eric.esteban28.wearfollowtrack;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;

import net.danlew.android.joda.JodaTimeAndroid;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eric.esteban28.wearfollowtrack.helpers.GPXFilesHelper;
import eric.esteban28.wearfollowtrack.helpers.MapBoxDownloadHelper;


public class MapBoxActivity extends FragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, MapboxMap.OnMapLongClickListener,
        GoogleApiClient.OnConnectionFailedListener, MapboxMap.OnScrollListener {

    private MapView mapView;
    private AssetManager assetManager;

    private final Integer PETICION_PERMISO_LOCALIZACION = 00;
    private final Integer PETICION_CONFIG_UBICACION = 01;
    private GoogleApiClient apiClient;
    private LocationRequest locRequest;

//    private Button botonStart;
//    private Button botonStop;
    private Button botonZoomOut;
    private Button botonZoomIn;
    private Button botonCurrentPos;

    private MapboxMap mapboxV;

    private Marker currentPositionMarker = null;

    private MapBoxDownloadHelper mapBoxDownloadHelper;

    private Resources resources;

    private Boolean followLocation = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_osm);

        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);

        assetManager = getAssets();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

//        botonStart = (Button) findViewById(R.id.buttonStr);
//        botonStop = (Button) findViewById(R.id.buttonStp);
        botonZoomOut = (Button) findViewById(R.id.buttonZoomOut);
        botonZoomIn = (Button) findViewById(R.id.buttonZoomIn);
        botonCurrentPos = (Button) findViewById(R.id.buttonCurrentPos);


//        botonStart.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                Log.d("PRUEBA", "Botón pulsado");
//                enableLocationUpdates();
//
//                botonStart.setVisibility(View.INVISIBLE);
//
//            }
//        });
//
//        botonStop.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                Log.d("PRUEBA", "Botón pulsado");
//                disableLocationUpdates();
//
//                botonStart.setVisibility(View.VISIBLE);
//                botonStop.setVisibility(View.INVISIBLE);
//            }
//        });

        botonZoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.d("PRUEBA", "Botón pulsado");
                mapboxV.easeCamera(CameraUpdateFactory.zoomOut());
            }
        });

        botonZoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.d("PRUEBA", "Botón pulsado");
                mapboxV.easeCamera(CameraUpdateFactory.zoomIn());
            }
        });

        botonCurrentPos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.d("PRUEBA", "Botón pulsado");
                followLocation = true;
                Log.d("PRUEBA", "Location null: " + (mapboxV.getMyLocation() != null ? "yes" : "false"));
                if (mapboxV.getMyLocation() != null) onLocationChanged(mapboxV.getMyLocation());
            }
        });

        JodaTimeAndroid.init(this);

        final Bundle bundle = this.getIntent().getExtras();

        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);

            Log.d("PRUEBA", "NO HAY PERMISION");

            return;
        }

        resources = this.getResources();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxV = mapboxMap;

                mapboxMap.easeCamera(CameraUpdateFactory.newLatLng(new LatLng(39.896965, -0.117089)));

                mapBoxDownloadHelper = new MapBoxDownloadHelper(OfflineManager.getInstance(MapBoxActivity.this), 15, 15, MapBoxActivity.this);

                String fileGpx = bundle.getString("FILEGXP");
                InputStream in = null;
                try {
                    in = assetManager.open(fileGpx);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<LatLng> points = GPXFilesHelper.getTrackPoints(in);

                mapboxMap.addPolyline(new PolylineOptions().addAll(points)
                        .color(Color.parseColor("#3bb2d0"))
                        .width(2));

                if (points.size() > 0) {
                    LatLng firstPoint = points.get(0);
                    LatLng lastPoint = points.get(points.size() - 1);

                    IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);

                    mapboxMap.addMarker(new MarkerOptions().position(firstPoint).title("Start").icon(iconFactory.fromResource(R.drawable.ic_map_start)));

                    mapboxMap.addMarker(new MarkerOptions().position(lastPoint).title("End").icon(iconFactory.fromResource(R.drawable.ic_map_end)));

                    mapboxMap.easeCamera(CameraUpdateFactory.newLatLng(firstPoint));

                    //ToDo: Sacar de esta clase
                    LatLng maxMap = GPXFilesHelper.getNorthestPoint(points, 0.001);
                    LatLng minMap = GPXFilesHelper.getSouthwestPoint(points, 0.001);

                    mapBoxDownloadHelper.downloadRegionIfNotExists(fileGpx, maxMap, minMap, mapboxMap.getStyleUrl(), resources.getDisplayMetrics().density);
                }

                enableLocationUpdates();
                setOnMapLongClickListener();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        enableLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void disableLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
    }

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                Log.d("PRUEBA", status.getStatusMessage());

                startLocationUpdates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.d("PRUEBA", "Configuración correcta");
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d("PRUEBA", "Se requiere actuación del usuario");
                            status.startResolutionForResult(MapBoxActivity.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
//                            btnActualizar.setChecked(false);
                            Log.d("PRUEBA", "Error al intentar solucionar configuración de ubicación");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("PRUEBA", "No se puede cumplir la configuración de ubicación necesaria");
//                        btnActualizar.setChecked(false);
                        break;
                    default:
                        Log.d("PRUEBA", "Ni idea");
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MapBoxActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.d("PRUEBA", "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, MapBoxActivity.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("PRUEBA", "Recibida nueva ubicación!");

        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        if (followLocation) mapboxV.easeCamera(CameraUpdateFactory.newLatLng(point));

        if (currentPositionMarker != null)
            currentPositionMarker.setPosition(point);
        else {
            IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);

            Marker newMarker = mapboxV.addMarker(new MarkerOptions().position(point).title("Here")
                    .icon(iconFactory.fromResource(R.drawable.ic_map_position)));

            currentPositionMarker = newMarker;
        }
    }

    private void setOnMapLongClickListener() {
        mapboxV.setOnMapLongClickListener(this);
        mapboxV.setOnScrollListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("PRUEBA", "Se ha interrumpido la conexión con Google Play Services");
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            Log.d("PRUEBA", "Latitud: " + String.valueOf(loc.getLatitude()));
            Log.d("PRUEBA", "Longitud: " + String.valueOf(loc.getLongitude()));
        } else {
            Log.d("PRUEBA", "Latitud: (desconocida)");
            Log.d("PRUEBA", "Longitud: (desconocida)");
        }
    }


    @Override
    public void onMapLongClick(LatLng point) {

        Log.d("PRUEBA", "Pepe!");

//        Boolean updateStartStop = false;
//        Boolean botonesOcultados = false;
//        if (botonZoomIn.getVisibility() == View.VISIBLE) {
//            if (locationEnable) {
//                updateStartStop = true;
//                if (botonStop.getVisibility() == View.VISIBLE) {
//                    botonStop.setVisibility(View.INVISIBLE);
//                    botonesOcultados = true;
//                } else {
//                    botonStop.setVisibility(View.VISIBLE);
//                }
//            } else {
//                if (botonStart.getVisibility() == View.VISIBLE) {
//                    botonStart.setVisibility(View.INVISIBLE);
//                    botonesOcultados = true;
//                } else {
//                    botonStart.setVisibility(View.VISIBLE);
//                }
//            }
//        }

//        if (!updateStartStop || botonesOcultados) {
            if (botonZoomIn.getVisibility() == View.VISIBLE) {
                botonZoomIn.setVisibility(View.INVISIBLE);
                botonZoomOut.setVisibility(View.INVISIBLE);
                botonCurrentPos.setVisibility(View.INVISIBLE);
            } else {
                botonZoomIn.setVisibility(View.VISIBLE);
                botonZoomOut.setVisibility(View.VISIBLE);
                botonCurrentPos.setVisibility(View.VISIBLE);
            }
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("PRUEBA", "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onScroll() {
        followLocation = false;
    }
}
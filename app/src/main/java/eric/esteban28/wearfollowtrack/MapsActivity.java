package eric.esteban28.wearfollowtrack;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import net.danlew.android.joda.JodaTimeAndroid;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eric.esteban28.wearfollowtrack.helpers.GPXFilesHelper;
import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;

public class MapsActivity extends FragmentActivity  {

    private final Integer PETICION_PERMISO_LOCALIZACION = 00;
    private final Integer PETICION_CONFIG_UBICACION = 01;
    private GoogleMap mMap;
    private AssetManager assetManager;
    private GoogleApiClient apiClient;
    private LocationRequest locRequest;
    private  Button botonStart;
    private  Button botonStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        assetManager = getAssets();
//
//        apiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addConnectionCallbacks(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        botonStart = (Button) findViewById(R.id.buttonStr);
//        botonStop = (Button) findViewById(R.id.buttonStp);
//
//        botonStart.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                Log.d("PRUEBA", "Botón pulsado");
//                enableLocationUpdates();
//
//                botonStart.setVisibility(View.INVISIBLE);
//                defineBotonStop();
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
//                borraBotonStop();
//            }
//        });
//
//        JodaTimeAndroid.init(this);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d("PRUEBA", "ON RESUME");
//
//        if (botonStart.getVisibility() == View.INVISIBLE) {
//            enableLocationUpdates();
//        }
//    }
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PETICION_PERMISO_LOCALIZACION);
//
//            Log.d("PRUEBA", "NO HAY PERMISION");
//
//            return;
//        }
//
//        mMap.setMyLocationEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//
//        //Recuperamos la información pasada en el intent
//        Bundle bundle = this.getIntent().getExtras();
//
//        String[] files = new String[0];
//        try {
//            files = assetManager.list("");
//        } catch (Exception ex) { }
//
//        GPXParser mParser = new GPXParser(); // consider injection
//        Gpx parsedGpx = null;
//        InputStream in = null;
//        try {
//            in = getAssets().open(bundle.getString("FILEGXP"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<LatLng> points = GPXFilesHelper.getTrackPoints(in);
//
//        PolylineOptions polylinePoints = new PolylineOptions();
//        polylinePoints.addAll(points);
//        polylinePoints.color(Color.rgb(87,128,191));
//
//        CircleOptions circleOptions = new CircleOptions()
//                .center(points.get(0))
//                .radius(5).fillColor(Color.GREEN).strokeColor(Color.GREEN);
//
//        CircleOptions circleOptions1 = new CircleOptions()
//                .center(points.get(points.size()-1))
//                .radius(5).fillColor(Color.RED).strokeColor(Color.RED);
//        mMap.addCircle(circleOptions1);
//        mMap.addCircle(circleOptions);
//
//        mMap.addPolyline(polylinePoints);
//
//        LatLng firstPont = points.get(0);
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstPont, 18));
//
//    }
//
//    private void updateUI(Location loc) {
//        if (loc != null) {
//            Log.d("PRUEBA", "Latitud: " + String.valueOf(loc.getLatitude()));
//            Log.d("PRUEBA", "Longitud: " + String.valueOf(loc.getLongitude()));
//        } else {
//            Log.d("PRUEBA", "Latitud: (desconocida)");
//            Log.d("PRUEBA", "Longitud: (desconocida)" );
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e("PRUEBA", "Error grave al conectar con Google Play Services");
//
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PETICION_PERMISO_LOCALIZACION);
//        } else {
//
//            Location lastLocation =
//                    LocationServices.FusedLocationApi.getLastLocation(apiClient);
//
//            updateUI(lastLocation);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.e("PRUEBA", "Se ha interrumpido la conexión con Google Play Services");
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
//            if (grantResults.length == 1
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                Log.e("PRUEBA", "Permiso denegado");
//            }
//        }
//    }
//
//    private void disableLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                apiClient, this);
//
//    }
//
//    private void enableLocationUpdates() {
//
//        locRequest = new LocationRequest();
//        locRequest.setInterval(2000);
//        locRequest.setFastestInterval(1000);
//        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        Log.d("PRUEBA", "Pasa");
//
//        LocationSettingsRequest locSettingsRequest =
//                new LocationSettingsRequest.Builder()
//                        .addLocationRequest(locRequest)
//                        .build();
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(
//                        apiClient, locSettingsRequest);
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult locationSettingsResult) {
//                final Status status = locationSettingsResult.getStatus();
//
//                Log.d("PRUEBA", status.getStatusMessage());
//
//                startLocationUpdates();
//
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//
//                        Log.d("PRUEBA", "Configuración correcta");
//                        startLocationUpdates();
//                        break;
//
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            Log.d("PRUEBA", "Se requiere actuación del usuario");
//                            status.startResolutionForResult(MapsActivity.this, PETICION_CONFIG_UBICACION);
//                        } catch (IntentSender.SendIntentException e) {
////                            btnActualizar.setChecked(false);
//                            Log.d("PRUEBA", "Error al intentar solucionar configuración de ubicación");
//                        }
//                        break;
//
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.d("PRUEBA", "No se puede cumplir la configuración de ubicación necesaria");
////                        btnActualizar.setChecked(false);
//                        break;
//                    default:
//                        Log.d("PRUEBA", "Ni idea");
//                }
//            }
//        });
//    }
//
//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
//            //Sería recomendable implementar la posible petición en caso de no tenerlo.
//
//            Log.d("PRUEBA", "Inicio de recepción de ubicaciones");
//
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    apiClient, locRequest, MapsActivity.this);
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        Log.d("PRUEBA", "Recibida nueva ubicación!");
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
//    }
//
//    private void defineBotonStop() {
//        mMap.setOnMapLongClickListener(this);
//    }
//
//    private void borraBotonStop() {
//        botonStop.setVisibility(View.INVISIBLE);
//        mMap.setOnMapLongClickListener(null);
//    }
//
//    @Override
//    public void onMapLongClick(LatLng point) {
//
//        Log.d("PRUEBA", "Pepe!");
//
//        int visible = botonStop.getVisibility();
//        if (visible == View.VISIBLE) {
//            botonStop.setVisibility(View.INVISIBLE);
//        } else {
//            botonStop.setVisibility(View.VISIBLE);
//        }
//
//
//    }
}

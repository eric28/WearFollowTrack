package eric.esteban28.wearfollowtrack;

import android.Manifest;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;

import java.util.List;

import eric.esteban28.wearfollowtrack.helpers.DatabaseHelper;
import eric.esteban28.wearfollowtrack.helpers.GPXFilesHelper;
import eric.esteban28.wearfollowtrack.helpers.MapBoxDownloadHelper;
import eric.esteban28.wearfollowtrack.models.TrackGPX;


public class MapBoxActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        MapboxMap.OnMapLongClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private MapView mapView;

    private GoogleApiClient apiClient;
    private LocationRequest locRequest;

    private Button botonZoomOut;
    private Button botonZoomIn;
    private Button botonCurrentPos;

    private MapboxMap mapboxV;

    private Marker currentPositionMarker = null;

    private MapBoxDownloadHelper mapBoxDownloadHelper;

    private Resources resources;

    private Boolean followLocation = true;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapbox);

        int PETICION_PERMISO_LOCALIZACION = 00;
        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);

            finish();
        }

        Mapbox.getInstance(this, getString(R.string.mapboxsdk_token));

        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        botonZoomOut = findViewById(R.id.buttonZoomOut);
        botonZoomIn = findViewById(R.id.buttonZoomIn);
        botonCurrentPos = findViewById(R.id.buttonCurrentPos);

        botonZoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mapboxV.easeCamera(CameraUpdateFactory.zoomOut());
            }
        });

        botonZoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mapboxV.easeCamera(CameraUpdateFactory.zoomIn());
            }
        });

        botonCurrentPos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                followLocation = true;
                if (currentPositionMarker != null) {
                    LatLng position = currentPositionMarker.getPosition();
                    mapboxV.easeCamera(CameraUpdateFactory.newLatLng(position));
                }
            }
        });

        findViewById(R.id.buttonStop).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finish();
                return true;
            }
        });

        Bundle bundle = this.getIntent().getExtras();

        resources = this.getResources();

        long idTrack = bundle.getLong("id_track");

        final TrackGPX track = databaseHelper.getById(idTrack);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxV = mapboxMap;

                mapboxV.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector detector) {

                    }

                    @Override
                    public void onMove(MoveGestureDetector detector) {
                    }

                    @Override
                    public void onMoveEnd(MoveGestureDetector detector) {
                        followLocation = false;
                    }
                });

                mapboxV.setStyle(Style.SATELLITE, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(Style style) {
                        List<LatLng> points = GPXFilesHelper.getTrackPoints(track.getPoints());

                        mapBoxDownloadHelper = new MapBoxDownloadHelper(
                                OfflineManager.getInstance(MapBoxActivity.this),
                                15,
                                15,
                                MapBoxActivity.this
                        );

                        mapboxV.addPolyline(new PolylineOptions().addAll(points)
                                .color(Color.parseColor("#3bb2d0"))
                                .width(2));

                        if (points.size() > 0) {
                            LatLng firstPoint = points.get(0);
                            LatLng lastPoint = points.get(points.size() - 1);

                            IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);

                            mapboxV.addMarker(new MarkerOptions().position(firstPoint)
                                    .title("Start")
                                    .icon(iconFactory.fromResource(R.drawable.ic_map_start)));

                            mapboxV.addMarker(new MarkerOptions().position(lastPoint)
                                    .title("End")
                                    .icon(iconFactory.fromResource(R.drawable.ic_map_end)));

                            mapboxV.easeCamera(CameraUpdateFactory.newLatLng(firstPoint));

                            LatLng maxMap = GPXFilesHelper.getNorthestPoint(points, 0.001);
                            LatLng minMap = GPXFilesHelper.getSouthwestPoint(points, 0.001);

                            mapBoxDownloadHelper.downloadRegionIfNotExists(
                                    String.valueOf(track.getId()),
                                    maxMap,
                                    minMap,
                                    mapboxV.getStyle().getUri(),
                                    resources.getDisplayMetrics().density
                            );
                        }

                        enableLocationUpdates();
                        setOnMapLongClickListener();
                    }
                });
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
        disableLocationUpdates();
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
        disableLocationUpdates();
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void enableLocationUpdates() {
        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locRequest.setMaxWaitTime(10000);

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

                LocationServices.FusedLocationApi.requestLocationUpdates(
                        apiClient, locRequest, MapBoxActivity.this);
            }
        });
    }

    private void disableLocationUpdates() {
        try {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(apiClient, MapBoxActivity.this);
        } catch (Exception e) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        if (followLocation && mapboxV != null) {
            botonCurrentPos.setBackground(getDrawable(R.drawable.ic_my_location_blue_24dp));
            mapboxV.easeCamera(CameraUpdateFactory.newLatLng(point));
        } else {
            botonCurrentPos.setBackground(getDrawable(R.drawable.ic_my_location_grey_24dp));
        }

        if (currentPositionMarker != null)
            currentPositionMarker.setPosition(point);
        else {
            IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);

            Marker newMarker = mapboxV.addMarker(
                    new MarkerOptions()
                            .position(point)
                            .title("Here")
                            .icon(iconFactory.fromResource(R.drawable.ic_map_position))
            );

            currentPositionMarker = newMarker;
        }
    }

    private void setOnMapLongClickListener() {
        mapboxV.addOnMapLongClickListener(this);
    }

    @Override
    public boolean onMapLongClick(LatLng point) {
        if (botonZoomIn.getVisibility() == View.VISIBLE) {
            botonZoomIn.setVisibility(View.INVISIBLE);
            botonZoomOut.setVisibility(View.INVISIBLE);
            botonCurrentPos.setVisibility(View.INVISIBLE);
        } else {
            botonZoomIn.setVisibility(View.VISIBLE);
            botonZoomOut.setVisibility(View.VISIBLE);
            botonCurrentPos.setVisibility(View.VISIBLE);
        }

        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        enableLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("PRUEBA", "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("PRUEBA", "Error grave al conectar con Google Play Services");
        finish();
    }
}
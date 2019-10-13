package eric.esteban28.wearfollowtrack.local_gpx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eric.esteban28.wearfollowtrack.MapBoxActivity;
import eric.esteban28.wearfollowtrack.R;
import eric.esteban28.wearfollowtrack.helpers.DatabaseHelper;
import eric.esteban28.wearfollowtrack.models.TrackGPX;
import eric.esteban28.wearfollowtrack.remote_gpx.RemoteGpxActivity;

public class LocalGpxActivity extends Activity {

    private LocalGpxAdapter adapter = null;
    private List<GpxItem> menuItems = new ArrayList<>();
    private ArrayList<TrackGPX> tracks = null;
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private OfflineManager offlineManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_gpx);

        WearableRecyclerView wearableRecyclerView = findViewById(R.id.recycler_view_local_gpx);

        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wearableRecyclerView.setCenterEdgeItems(true);

        offlineManager = OfflineManager.getInstance(LocalGpxActivity.this);

        LocalGpxAdapter.AdapterCallback callback = new LocalGpxAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final GpxItem menuPosition) {
                switch (menuPosition.getKey()) {
                    case "descargar":
                        Intent intent = new Intent(LocalGpxActivity.this, RemoteGpxActivity.class);

                        Bundle b = new Bundle();

                        intent.putExtras(b);

                        startActivity(intent);
                        break;
                    default:
                        TrackGPX a = searchTrack(menuPosition.getKey());
                        Intent intentMapbox =
                                new Intent(LocalGpxActivity.this, MapBoxActivity.class);

                        Bundle bMap = new Bundle();

                        bMap.putLong("id_track", a.getId());
                        intentMapbox.putExtras(bMap);

                        startActivity(intentMapbox);
                        break;
                }
            }
        };

        LocalGpxAdapter.AdapterCallbackLong callbackLong = new LocalGpxAdapter.AdapterCallbackLong() {
            @Override
            public void onItemLongClicked(GpxItem item) {
                switch (item.getKey()) {
                    case "descargar":
                        break;
                    default:
                        TrackGPX a = searchTrack(item.getKey());
                        makeDialogDelete(a);
                }
            }
        };

        adapter = new LocalGpxAdapter(this, menuItems, callback, callbackLong);

        this.getLocalTracks();
        wearableRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getLocalTracks();
    }

    private TrackGPX searchTrack(String trackKey) {
        for (TrackGPX a : tracks) {
            if (trackKey.equals(a.getName())) {
                return a;
            }
        }
        return null;
    }

    private void makeDialogDelete(final TrackGPX trackGPX) {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Borrar")
                .setMessage("¿Deseas borrar el track " + trackGPX.getName() + "?")
                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.remove(trackGPX.getId());
                        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                            @Override
                            public void onList(OfflineRegion[] offlineRegions) {
                                OfflineRegion regionTrack = null;
                                String REGION_NAME = "FIELD_REGION_NAME";

                                for (int i = 0; i < offlineRegions.length; i++) {
                                    OfflineRegion reg = offlineRegions[i];

                                    String metadata = new String(reg.getMetadata());
                                    try {
                                        JSONObject jsonObject = new JSONObject(metadata);
                                        String regionName = jsonObject.getString(REGION_NAME);
                                        if (regionName.equals(trackGPX.getId())){
                                            regionTrack = reg;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (regionTrack != null){
                                    regionTrack.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                                        @Override
                                        public void onDelete() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Region borrada", Toast.LENGTH_SHORT)
                                                    .show();
                                            getLocalTracks();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e(String.valueOf(LocalGpxActivity.class), error);
                                            getLocalTracks();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onError(String error) {
                                Log.e(String.valueOf(LocalGpxActivity.class), error);
                            }
                        });

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void getLocalTracks() {
        menuItems.clear();
        menuItems.add(new GpxItem("descargar", "Descargar"));

        tracks = this.databaseHelper.getAllGpx();

        for (TrackGPX a : tracks) {
            menuItems.add(new GpxItem(a.getName(), a.getName()));
        }

        adapter.notifyDataSetChanged();
    }
}

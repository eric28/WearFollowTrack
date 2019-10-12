package eric.esteban28.wearfollowtrack.local_gpx;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;

import java.util.ArrayList;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_gpx);

        WearableRecyclerView wearableRecyclerView = findViewById(R.id.recycler_view_local_gpx);

        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wearableRecyclerView.setCenterEdgeItems(true);


        adapter = new LocalGpxAdapter(this, menuItems, new LocalGpxAdapter.AdapterCallback() {
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
                        for (TrackGPX a : tracks) {
                            if (menuPosition.getKey().equals(a.getName())) {
                                Intent intentMapbox =
                                        new Intent(LocalGpxActivity.this, MapBoxActivity.class);

                                Bundle bMap = new Bundle();

                                bMap.putLong("id_track", a.getId());
                                intentMapbox.putExtras(bMap);

                                startActivity(intentMapbox);
                                break;
                            }
                        }
                }
            }
        });

        this.getLocalTracks();
        wearableRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getLocalTracks();
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

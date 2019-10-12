package eric.esteban28.wearfollowtrack.remote_gpx;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eric.esteban28.wearfollowtrack.R;
import eric.esteban28.wearfollowtrack.helpers.DatabaseHelper;

public class RemoteGpxActivity extends Activity {

    private RemoteGpxAdapter adapter = null;
    private List<GpxItem> menuItems = new ArrayList<>();
    private JSONArray tracks = null;
    private DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_gpx);

        WearableRecyclerView wearableRecyclerView = findViewById(R.id.recycler_view_remote_gpx);

        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wearableRecyclerView.setCenterEdgeItems(true);

        adapter = new RemoteGpxAdapter(this, menuItems, new RemoteGpxAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(GpxItem menuPosition) {
                switch (menuPosition.getKey()) {
                    case "actualizar":
                        getRemoteTracks();
                        break;
                    default:
                        for (int i = 0; i < tracks.length(); i++) {
                            try {
                                JSONObject track = tracks.getJSONObject(i);

                                if (menuPosition.getKey().equals(track.getString("id"))) {
                                    boolean created = helper
                                            .insert(menuPosition.getText(), track.getString("gpx_json"));
                                    if (created) finish();

                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
        });

        this.getRemoteTracks();

        wearableRecyclerView.setAdapter(adapter);
    }

    private void getRemoteTracks() {

        menuItems.clear();
        menuItems.add(new GpxItem("actualizar", "Actualizar"));

        final RequestQueue queue = Volley.newRequestQueue(this);

        String url = getString(R.string.tracks_url) + "gpx-listing";

        final JsonObjectRequest stringRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    tracks = response.getJSONArray("data");

                                    for (int i = 0; i < tracks.length(); i++) {
                                        JSONObject track = tracks.getJSONObject(i);

                                        menuItems.add(new GpxItem(track.getString("id"), track.getString("name")));
                                    }

                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Volley", "Response is: " + error.getMessage());
                            }
                        });

        queue.add(stringRequest);
    }
}

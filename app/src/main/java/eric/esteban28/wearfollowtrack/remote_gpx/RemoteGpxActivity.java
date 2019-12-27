package eric.esteban28.wearfollowtrack.remote_gpx;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.os.AsyncTask;
import android.widget.Toast;

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
import eric.esteban28.wearfollowtrack.exceptions.TrackExistsException;
import eric.esteban28.wearfollowtrack.helpers.DatabaseHelper;

public class RemoteGpxActivity extends Activity {

    private final String ACTUALIZAR_ID = "actualizar";

    private RemoteGpxAdapter adapter = null;
    private List<GpxItem> menuItems = new ArrayList<>();
    private DatabaseHelper helper = new DatabaseHelper(this);
    private Activity context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RemoteGpxAdapter(this, menuItems, new RemoteGpxAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(GpxItem menuPosition) {
                switch (menuPosition.getKey()) {
                    case ACTUALIZAR_ID:
                        getRemoteTracks();
                        break;
                    default:
                        for (int i = 0; i < menuItems.size(); i++) {
                            GpxItem track = menuItems.get(i);

                            if (menuPosition.getKey().equals(track.getKey())) {
                                boolean created = false;
                                try {
                                    created = helper
                                            .insert(menuPosition.getText(), menuPosition.getJsonGpx());
                                } catch (TrackExistsException e) {
                                    String error = getString(R.string.msg_error_track_exists
                                            , menuPosition.getText());

                                    Toast.makeText(getApplicationContext(), error,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                                if (created) finish();

                                break;
                            }
                        }
                }
            }
        });

        loadLayoutList(adapter);

        this.getRemoteTracks();
    }

    private void getRemoteTracks() {
        loadLayoutLoading();
        menuItems.clear();
        menuItems.add(new GpxItem(ACTUALIZAR_ID, getString(R.string.actualizar), null,
                null, null));

        new ObtenerTracksTask().execute(new ServerCallback() {
            @Override
            public void onSuccess(List<GpxItem> result) {
                menuItems.addAll(result);

                loadLayoutList(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadLayoutLoading() {
        this.setContentView(R.layout.activity_progress);
    }

    private void loadLayoutList(RecyclerView.Adapter adapter) {
        context.setContentView(R.layout.activity_remote_gpx);

        WearableRecyclerView wearableRecyclerView =
                context.findViewById(R.id.recycler_view_remote_gpx);

        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        wearableRecyclerView.setCenterEdgeItems(true);
        wearableRecyclerView.setAdapter(adapter);
    }

    private class ObtenerTracksTask extends AsyncTask<ServerCallback, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(ServerCallback... params) {
            final ServerCallback callback = params[0];

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = getString(R.string.tracks_url) + "gpx-listing";

            final JsonObjectRequest stringRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray tracksJson = response.getJSONArray("data");
                                        List<GpxItem> tracks = new ArrayList<>();

                                        for (int i = 0; i < tracksJson.length(); i++) {
                                            JSONObject track = tracksJson.getJSONObject(i);

                                            menuItems.add(new GpxItem(track.getString("id"),
                                                    track.getString("name"),
                                                    track.getDouble("distance"),
                                                    track.getDouble("unevenness_positive"),
                                                    track.getString("gpx_json")));
                                        }

                                        callback.onSuccess(tracks);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Volley", "Response is: " + error.getMessage());
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.msg_error_download_tracks),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    finish();
                                }
                            });

            queue.add(stringRequest);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    public interface ServerCallback {
        void onSuccess(List<GpxItem> result);
    }
}

package eric.esteban28.wearfollowtrack.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.util.ArrayList;

public class MapBoxDownloadHelper {

    private static final String JSON_CHARSET = "UTF-8";
    private static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private static final String TAG = "MapBoxDownloadHelper";

    private OfflineManager offlineManager;
    private Integer maxZoom;
    private Integer minZoom;
    private Context contextActivity;

    public MapBoxDownloadHelper(OfflineManager offlineManager, Integer maxZoom, Integer minZoom, Context contextActivity) {
        this.offlineManager = offlineManager;
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.contextActivity = contextActivity;
    }

    private OfflineRegion offlineRegionDownloaded;

    public void downloadRegionIfNotExists(final String regionNameFind, final LatLng northeast, final LatLng southwest, final String mapBoxStyle, final Float density) {
        final ArrayList<String> offlineRegionsNames = new ArrayList<>();

        // Query the DB asynchronously
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.length == 0) {
                    Toast.makeText(contextActivity, "You have no regions yet.", Toast.LENGTH_SHORT).show();
                }

                for (OfflineRegion offlineRegion : offlineRegions) offlineRegionsNames.add(getRegionName(offlineRegion));

                if (offlineRegionsNames.contains(regionNameFind))
                    Toast.makeText(contextActivity, "Region (" + regionNameFind + ") dowloaded yet.", Toast.LENGTH_LONG).show();
                else
                    downloadOfflineRegion(northeast, southwest, regionNameFind, mapBoxStyle, density);

            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error listing regions: " + error);
            }
        });
    }

    private void downloadOfflineRegion(LatLng northeast, LatLng southwest, final String regionName, String mapBoxStyle, Float density) {

        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(northeast) // Northeast
                .include(southwest) // Southwest
                .build();

        OfflineTilePyramidRegionDefinition definition =
                new OfflineTilePyramidRegionDefinition(mapBoxStyle, latLngBounds, minZoom, maxZoom, density);

        byte[] metadata;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);

        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        // Create the offline region and launch the download
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                Log.d(TAG, "Offline region created: " + regionName);
                offlineRegionDownloaded = offlineRegion;
                launchDownloadRegion();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error downloading region: " + error);
            }
        });
    }

    private void launchDownloadRegion() {

        offlineRegionDownloaded.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                // Compute a percentage
                double percentage = status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0.0;

                if (status.isComplete()) {
                    // Download complete
                    Toast.makeText(contextActivity, "Region downloaded successfully.", Toast.LENGTH_LONG).show();
                    return;
                } else if (status.isRequiredResourceCountPrecise()) {
                    // Switch to determinate state
                    Toast.makeText(contextActivity, "Downloading: " + (int) Math.round(percentage) + " %", Toast.LENGTH_SHORT).show();
                }

                // Log what is being currently downloaded
                Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
                        String.valueOf(status.getCompletedResourceCount()),
                        String.valueOf(status.getRequiredResourceCount()),
                        String.valueOf(status.getCompletedResourceSize())));
            }

            @Override
            public void onError(OfflineRegionError error) {
                Log.e(TAG, "onError reason: " + error.getReason());
                Log.e(TAG, "onError message: " + error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
            }
        });

        offlineRegionDownloaded.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }


    private String getRegionName(OfflineRegion offlineRegion) {
        // Get the retion name from the offline region metadata
        String regionName;

        try {
            byte[] metadata = offlineRegion.getMetadata();
            String json = new String(metadata, JSON_CHARSET);
            JSONObject jsonObject = new JSONObject(json);
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to decode metadata: " + exception.getMessage());
            regionName = "Region " + offlineRegion.getID();
        }
        return regionName;
    }
}
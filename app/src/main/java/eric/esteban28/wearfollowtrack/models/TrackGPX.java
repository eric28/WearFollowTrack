package eric.esteban28.wearfollowtrack.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackGPX {

    private long id;
    private String name;
    private List<PointLatLng> points;

    public TrackGPX(Long id, String name, String jsonPoints) {
        this.id = id;
        this.name = name;

        points = new ArrayList<>();
        try {
            JSONArray arrayPoints = new JSONArray(jsonPoints);

            for (int i = 0; i < arrayPoints.length(); i++) {
                JSONObject point = arrayPoints.getJSONObject(i);

                Double lat = point.getDouble("lat");
                Double lon = point.getDouble("lon");
                Double elevation = point.getDouble("elevation");

                points.add(new PointLatLng(lat, lon, elevation));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PointLatLng> getPoints() {
        return points;
    }
}

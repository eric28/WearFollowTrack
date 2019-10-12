package eric.esteban28.wearfollowtrack.helpers;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eric.esteban28.wearfollowtrack.models.PointLatLng;
import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;

public class GPXFilesHelper {

    private static final String TAG = "GPXFilesHelper";

    public static List<LatLng> getTrackPoints(InputStream inputStream) {

        GPXParser mParser = new GPXParser(); // consider injection
        Gpx parsedGpx = null;
        List<LatLng> points = new ArrayList<>();

        try {
            parsedGpx = mParser.parse(inputStream);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        if (parsedGpx == null) {
            Log.e(TAG, "Error parsing track");
        } else {

            List<Track> trackList1 = parsedGpx.getTracks();
            for (Track track : trackList1) {
                for (TrackSegment segment : track.getTrackSegments()) {
                    PolylineOptions lineas = new PolylineOptions();

                    for (TrackPoint trackPoint : segment.getTrackPoints())
                        points.add(new LatLng(trackPoint.getLatitude(), trackPoint.getLongitude()));
                }

            }
        }

        return points;
    }

    public static List<LatLng> getTrackPoints(List<PointLatLng> points) {

        List<LatLng> pointsMap = new ArrayList<>();

        for (PointLatLng point : points) {
            pointsMap.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return pointsMap;
    }

    public static LatLng getNorthestPoint(List<LatLng> points, double addDistance) {

        Double maxLong = null;
        Double maxLat = null;

        for (LatLng point : points) {
            if (maxLong == null) {
                maxLong = point.getLongitude();
                maxLat = point.getLatitude();
            } else {
                if (point.getLongitude() > maxLong) maxLong = point.getLongitude();
                if (point.getLatitude() > maxLat) maxLat = point.getLatitude();
            }
        }

        return new LatLng(maxLat + addDistance, maxLong + addDistance);
    }

    public static LatLng getSouthwestPoint(List<LatLng> points, double addDistance) {

        Double minLong = null;
        Double minLat = null;

        for (LatLng point : points) {
            if (minLong == null) {
                minLong = point.getLongitude();
                minLat = point.getLatitude();
            } else {
                if (point.getLongitude() < minLong) minLong = point.getLongitude();
                if (point.getLatitude() < minLat) minLat = point.getLatitude();
            }
        }

        return new LatLng(minLat - addDistance, minLong - addDistance);

    }
}

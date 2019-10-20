package eric.esteban28.wearfollowtrack.helpers;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import eric.esteban28.wearfollowtrack.models.PointLatLng;

public class GPXFilesHelper {

    private static final String TAG = "GPXFilesHelper";

    public static List<LatLng> getTrackPoints(List<PointLatLng> points) {

        List<LatLng> pointsMap = new ArrayList<>();

        for (PointLatLng point : points) {
            pointsMap.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return pointsMap;
    }

    public static double getDistance(List<PointLatLng> points) {

        double distance = 0;

        PointLatLng previous = null;
        for (PointLatLng point : points) {
            if (previous == null) {
                previous = point;
                continue;
            }
            distance += CoordinatesHelper
                    .distanceInMeters(previous.getLatitude(), point.getLatitude(),
                            previous.getLongitude(), point.getLongitude());
            previous = point;
        }


        return distance;
    }

    public static double getUnevennessPositive(List<PointLatLng> points) {

        double unevenness = 0;

        PointLatLng previous = null;
        for (PointLatLng point : points) {
            if (previous == null) {
                previous = point;
                continue;
            }
            if (point.getElevation() > previous.getElevation()) {
                unevenness += point.getElevation() - previous.getElevation();
            }

            previous = point;
        }

        return unevenness;
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

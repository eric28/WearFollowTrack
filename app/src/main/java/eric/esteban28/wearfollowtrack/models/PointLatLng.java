package eric.esteban28.wearfollowtrack.models;

public class PointLatLng {
    private Double latitude;
    private Double longitude;
    private Double elevation;

    public PointLatLng(Double latitude, Double longitude, Double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getElevation() {
        return elevation;
    }
}

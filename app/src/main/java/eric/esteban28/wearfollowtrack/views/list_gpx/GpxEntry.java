package eric.esteban28.wearfollowtrack.views.list_gpx;

public class GpxEntry {

    private String title;
private GpxListType gpxListType;

    public GpxEntry(String title, GpxListType gpxListType) {
        this.title = title;
        this.gpxListType = gpxListType;
    }

    public String getTitle() {
        return title;
    }

    public GpxListType getGpxListType() {
        return gpxListType;
    }
}

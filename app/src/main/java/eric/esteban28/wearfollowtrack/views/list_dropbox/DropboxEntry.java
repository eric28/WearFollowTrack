package eric.esteban28.wearfollowtrack.views.list_dropbox;

class DropboxEntry {
    private String title;
    private String subTitle;

    public DropboxEntry(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}

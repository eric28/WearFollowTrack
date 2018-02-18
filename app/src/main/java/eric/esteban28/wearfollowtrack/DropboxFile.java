package eric.esteban28.wearfollowtrack;

public class DropboxFile {
    private String name;
    private String path;

    public DropboxFile(String name, String path) {

        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}

package eric.esteban28.wearfollowtrack;

import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.util.ArrayList;
import java.util.List;

public class DropboxWrapper {
    private static final String ACCESS_TOKEN = "hJF0vioOBVoAAAAAAAAKbQAEEbcmG8HR9IzvIrB9dlDEJxRTLC6EWECKdOiqMD_D";

    private DbxClientV2 client;

    public DropboxWrapper() {
        DbxRequestConfig config = new DbxRequestConfig("WearFollowTrack", "en_US");
        this.client =new DbxClientV2(config, ACCESS_TOKEN);
    }

    public void printAccount() {

        try {
            FullAccount account = this.client.users().getCurrentAccount();
            Log.d("PRUEBA----------------------------------------------", account.getName().getDisplayName());

            System.out.println(account.getName().getDisplayName());
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public void printNameFiles() {
        try {

        ListFolderResult result = this.client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                Log.d("PRUEBA----------------------------------------------", metadata.getPathLower());

                if (metadata.getPathLower().equals("/betxi.gpx")) {
                    DbxDownloader track = this.client.files().download("/betxi.gpx");
                }
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public List<DropboxFile> returnNameFiles() {
        List<DropboxFile> files = new ArrayList<>();
        try {

            ListFolderResult result = this.client.files().listFolder("");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    String name = metadata.getName();
                    String path = metadata.getPathLower();

                    files.add(new DropboxFile(name, path));
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return files;
    }
}

package eric.esteban28.wearfollowtrack;

import android.os.AsyncTask;

import java.net.URL;
import java.util.List;

public class ConnectDropboxTask extends AsyncTask<Void, Void, List<DropboxFile>> {

    private DropboxWrapper dropboxWrapper;

    @Override
    protected List<DropboxFile> doInBackground(Void... params) {
        this.dropboxWrapper = new DropboxWrapper();

        return this.dropboxWrapper.returnNameFiles();
//        this.dropboxWrapper.printAccount();
//
//        this.dropboxWrapper.printNameFiles();
//
//        return null;
    }

//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
//
//    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
//    }
}
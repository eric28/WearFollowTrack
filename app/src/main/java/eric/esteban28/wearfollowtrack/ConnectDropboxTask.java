package eric.esteban28.wearfollowtrack;

import android.os.AsyncTask;

import java.net.URL;

class ConnectDropboxTask extends AsyncTask<Void, Void, Void> {

    private DropboxWrapper dropboxWrapper;

    @Override
    protected Void doInBackground(Void... params) {
        this.dropboxWrapper = new DropboxWrapper();

        this.dropboxWrapper.printAccount();

        this.dropboxWrapper.printNameFiles();

        return null;
    }

//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
//
//    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
//    }
}
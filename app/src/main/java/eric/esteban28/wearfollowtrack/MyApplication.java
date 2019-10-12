package eric.esteban28.wearfollowtrack;

import android.app.Application;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapboxsdk_token));
    }
}
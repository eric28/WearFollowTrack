package eric.esteban28.wearfollowtrack.views.list_gpx;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eric.esteban28.wearfollowtrack.R;
import eric.esteban28.wearfollowtrack.views.main_menu.MenuEntry;
import eric.esteban28.wearfollowtrack.views.main_menu.MenuEntryAdapter;
import eric.esteban28.wearfollowtrack.views.main_menu.MenuOption;

public class GpxActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gpx_list);

        GpxEntry dropbox = new GpxEntry("Buscar en dropbox", GpxListType.DROPBOX);
//        MenuEntry opcionRegions = new MenuEntry("Regions", MenuOption.REGIONS);

        final List<GpxEntry> menuEntries = new ArrayList<>();

        menuEntries.add(dropbox);
//        menuEntries.add(opcionRegions);

        GpxEntryAdapter menuEntryAdapter = new GpxEntryAdapter(this, menuEntries);

        ListView mainMenuList = findViewById(R.id.listGpx);

        mainMenuList.setAdapter(menuEntryAdapter);

        View header = getLayoutInflater().inflate(R.layout.gpx_header, null);

        mainMenuList.addHeaderView(header);

        if (menuEntries.size() > 3) {
            mainMenuList.addFooterView(header);
        }

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                GpxEntry menuEntrySelected = ((GpxEntry)a.getItemAtPosition(position));

                switch (menuEntrySelected.getGpxListType()) {
                    case FILE_GPX:
                        Log.d("prueba", "File GPX!!");
                        break;
                    case DROPBOX:
                        Log.d("prueba","Dropbox!!");
                        break;
                }

            }
        });

    }
}
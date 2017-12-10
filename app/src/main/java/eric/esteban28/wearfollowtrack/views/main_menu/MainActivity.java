package eric.esteban28.wearfollowtrack.views.main_menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eric.esteban28.wearfollowtrack.R;
import eric.esteban28.wearfollowtrack.views.list_gpx.GpxActivity;

public class MainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        MenuEntry opcionLoadTrack = new MenuEntry("Load track", MenuOption.FOLLOW_TRACK);
        MenuEntry opcionRegions = new MenuEntry("Regions", MenuOption.REGIONS);

        final List<MenuEntry> menuEntries = new ArrayList<>();

        menuEntries.add(opcionLoadTrack);
        menuEntries.add(opcionRegions);

        MenuEntryAdapter menuEntryAdapter = new MenuEntryAdapter(this, menuEntries);

        ListView mainMenuList = findViewById(R.id.listMenu);

        mainMenuList.setAdapter(menuEntryAdapter);

        View header = getLayoutInflater().inflate(R.layout.main_menu_header, null);

        mainMenuList.addHeaderView(header);

        if (menuEntries.size() > 3) {
            mainMenuList.addFooterView(header);
        }

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                MenuEntry menuEntrySelected = ((MenuEntry)a.getItemAtPosition(position));

                switch (menuEntrySelected.getMenuOption()) {
                    case FOLLOW_TRACK:
                        Log.d("prueba", "Follow track!!");

                        Intent intent = new Intent(MainActivity.this, GpxActivity.class);

                        startActivity(intent);
                        break;
                    case REGIONS:
                        Log.d("prueba","Regions!!");
                        break;
                }

            }
        });

    }
}
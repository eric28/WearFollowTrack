package eric.esteban28.wearfollowtrack.views.list_gpx;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eric.esteban28.wearfollowtrack.R;
import eric.esteban28.wearfollowtrack.views.main_menu.MenuEntry;

/**
 * Provides a binding from {@link NotificationCompat.Style} data set to views displayed within the
 * {@link WearableRecyclerView}.
 */
public class GpxEntryAdapter extends ArrayAdapter<GpxEntry> {

    private static final String TAG = "MenuEntryAdapter";

    private List<GpxEntry> menuEntries;

    public GpxEntryAdapter(Context context, List<GpxEntry> menuEntries) {
        super(context, R.layout.gpx_item, menuEntries);
        this.menuEntries = menuEntries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GpxEntry itemMenu = menuEntries.get(position);

        View item = convertView;
        ViewHolder holder;

        if(item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.gpx_item, null);

            holder = new ViewHolder();
            holder.title = item.findViewById(R.id.listGpxItemTitle);

            item.setTag(holder);
        } else {
            holder = (ViewHolder)item.getTag();
        }

        holder.title.setText(itemMenu.getTitle());

        return(item);
    }

    static class ViewHolder {
        TextView title;
    }
}
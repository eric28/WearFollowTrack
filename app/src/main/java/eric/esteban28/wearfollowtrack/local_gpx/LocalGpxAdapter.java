package eric.esteban28.wearfollowtrack.local_gpx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import eric.esteban28.wearfollowtrack.R;

public class LocalGpxAdapter extends RecyclerView.Adapter<LocalGpxAdapter.RecyclerViewHolder> {

    private List<GpxItem> dataSource;

    public interface AdapterCallback {
        void onItemClicked(GpxItem item);
    }

    public interface AdapterCallbackLong {
        void onItemLongClicked(GpxItem item);
    }

    private AdapterCallback callback;
    private AdapterCallbackLong callbackLong;

    private Context context;


    public LocalGpxAdapter(Context context, List<GpxItem> dataArgs, AdapterCallback callback,
                           AdapterCallbackLong callbackLong) {
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
        this.callbackLong = callbackLong;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.local_gpx_item, parent, false);

        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout menuContainer;
        TextView menuItem;
        TextView itemKm;
        TextView itemUnevenness;
        TextView itemDescription;
        ImageView mountainImage;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.local_gpx_item);
            menuItem = view.findViewById(R.id.item_name);
            itemKm = view.findViewById(R.id.item_km);
            itemUnevenness = view.findViewById(R.id.item_unevenness);
            itemDescription = view.findViewById(R.id.item_description);
            mountainImage = view.findViewById(R.id.icon_mountain_image);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final GpxItem data_provider = dataSource.get(position);

        holder.menuItem.setText(data_provider.getText());
        if (data_provider.getDistance() != null) {
            double d = data_provider.getDistance() / 1000;
            DecimalFormat f = new DecimalFormat("##.0");
            holder.itemKm.setText(f.format(d) + " km");
            holder.itemUnevenness.setText(data_provider.getUnevenness().intValue() + " m");

            holder.itemKm.setVisibility(View.VISIBLE);
            holder.itemUnevenness.setVisibility(View.VISIBLE);
            holder.itemDescription.setVisibility(View.GONE);
            holder.mountainImage
                    .setImageDrawable(context.getDrawable(R.drawable.ic_terrain_black_18dp));
        } else if (data_provider.getKey().equals(LocalGpxActivity.DESCARGAR_ID)) {
            holder.itemDescription.setText("Obtener mas tracks");

            holder.itemKm.setVisibility(View.GONE);
            holder.itemUnevenness.setVisibility(View.GONE);
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.mountainImage
                    .setImageDrawable(context.getDrawable(R.drawable.ic_file_download_black_16dp));
        } else {
            holder.itemDescription.setText(R.string.borrar_todo_descrip);

            holder.itemKm.setVisibility(View.GONE);
            holder.itemUnevenness.setVisibility(View.GONE);
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.mountainImage
                    .setImageDrawable(context.getDrawable(R.drawable.ic_trash_24dp));
        }

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (callback != null) {
                    callback.onItemClicked(data_provider);
                }
            }
        });

        holder.menuContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callbackLong != null) {
                    callbackLong.onItemLongClicked(data_provider);
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

class GpxItem {
    private String key;
    private String text;
    private Double distance;
    private Double unevenness;

    public GpxItem(String key, String text, Double distance, Double unevenness) {
        this.key = key;
        this.text = text;
        this.distance = distance;
        this.unevenness = unevenness;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getUnevenness() {
        return unevenness;
    }
}

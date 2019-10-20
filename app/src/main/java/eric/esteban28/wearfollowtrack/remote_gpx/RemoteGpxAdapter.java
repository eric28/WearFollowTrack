package eric.esteban28.wearfollowtrack.remote_gpx;

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

public class RemoteGpxAdapter extends RecyclerView.Adapter<RemoteGpxAdapter.RecyclerViewHolder> {

    private List<GpxItem> dataSource;

    public interface AdapterCallback {
        void onItemClicked(GpxItem menuPosition);
    }

    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public RemoteGpxAdapter(Context context, List<GpxItem> dataArgs, AdapterCallback callback) {
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remote_gpx_item, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
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
            menuContainer = view.findViewById(R.id.remote_gpx_relative_layout);
            menuItem = view.findViewById(R.id.item_name);
            itemKm = view.findViewById(R.id.item_km);
            itemUnevenness = view.findViewById(R.id.item_unevenness);
            itemDescription = view.findViewById(R.id.item_description);
            mountainImage = view.findViewById(R.id.icon_download_image);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final GpxItem data_provider = dataSource.get(position);

        if (data_provider == null) return;

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
                    .setImageDrawable(context.getDrawable(R.drawable.ic_file_download_black_16dp));
        } else {
            holder.itemDescription.setText("Actualiza los tracks");

            holder.itemKm.setVisibility(View.GONE);
            holder.itemUnevenness.setVisibility(View.GONE);
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.mountainImage
                    .setImageDrawable(context.getDrawable(R.drawable.ic_refresh_black_24dp));
        }

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (callback != null) {
                    callback.onItemClicked(data_provider);
                }
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

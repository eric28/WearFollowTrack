package eric.esteban28.wearfollowtrack.remote_gpx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.remote_gpx_relative_layout);
            menuItem = view.findViewById(R.id.remote_gpx_item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final GpxItem data_provider = dataSource.get(position);

        if (data_provider == null) return;

        holder.menuItem.setText(data_provider.getText());
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


    public GpxItem(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }
}

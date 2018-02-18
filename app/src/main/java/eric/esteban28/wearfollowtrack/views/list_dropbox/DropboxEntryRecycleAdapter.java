package eric.esteban28.wearfollowtrack.views.list_dropbox;

import android.support.wear.widget.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import eric.esteban28.wearfollowtrack.R;

public class DropboxEntryRecycleAdapter extends WearableRecyclerView.Adapter<DropboxEntryRecycleAdapter.DropboxEntryViewHolder>
        implements View.OnClickListener{

    private ArrayList<DropboxEntry> datos;
    private View.OnClickListener listener;

    //...

    public DropboxEntryRecycleAdapter(ArrayList<DropboxEntry> datos) {
        this.datos = datos;
    }

    @Override
    public DropboxEntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.drobox_item, viewGroup, false);

        itemView.setOnClickListener(this);

        DropboxEntryViewHolder tvh = new DropboxEntryViewHolder(itemView);

        return tvh;
    }

    @Override
    public void onBindViewHolder(DropboxEntryViewHolder viewHolder, int pos) {
        DropboxEntry item = datos.get(pos);

        viewHolder.bindDropboxEntry(item);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class DropboxEntryViewHolder
            extends WearableRecyclerView.ViewHolder {

        private TextView title;
        private TextView txtSubtitulo;

        public DropboxEntryViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.dropbpoxItemTitle);
            txtSubtitulo = itemView.findViewById(R.id.dropbpoxItemSubTitle);
        }

        public void bindDropboxEntry(DropboxEntry t) {
            title.setText(t.getTitle());
            txtSubtitulo.setText(t.getSubTitle());
        }

    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}

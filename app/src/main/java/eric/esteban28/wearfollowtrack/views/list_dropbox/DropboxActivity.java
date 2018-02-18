package eric.esteban28.wearfollowtrack.views.list_dropbox;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eric.esteban28.wearfollowtrack.ConnectDropboxTask;
import eric.esteban28.wearfollowtrack.DropboxFile;
import eric.esteban28.wearfollowtrack.R;

public class DropboxActivity extends WearableActivity {

    private WearableRecyclerView recView;

    private ArrayList<DropboxEntry> datos;

    private ConnectDropboxTask connectDropboxTask = new ConnectDropboxTask();

    List<DropboxFile> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropbox_list);


        try {
            list = connectDropboxTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //inicialización de la lista de datos de ejemplo
        datos = new ArrayList<DropboxEntry>();
        for(DropboxFile dropboxFile : list) {
            datos.add(new DropboxEntry( dropboxFile.getName(), dropboxFile.getPath()));
        }

        //Inicialización RecyclerView
        recView =  findViewById(R.id.DropboxRecycleView);
        recView.setHasFixedSize(true);

        final DropboxEntryRecycleAdapter adaptador = new DropboxEntryRecycleAdapter(datos);

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recView.getChildAdapterPosition(v);
                DropboxFile dropboxFile = list.get(position);
                Log.i("DemoRecView", "Pulsado el elemento " + dropboxFile.getPath());
            }
        });

        recView.setAdapter(adaptador);

        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        recView.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        recView.setItemAnimator(new DefaultItemAnimator());
    }
}
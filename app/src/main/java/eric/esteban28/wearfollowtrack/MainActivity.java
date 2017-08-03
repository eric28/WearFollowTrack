package eric.esteban28.wearfollowtrack;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button botonGPX;
    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        assetManager = getAssets();

        botonGPX = (Button) findViewById(R.id.buttonGPX);

        botonGPX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.d("PRUEBA", "Botón pulsado");

                Intent intent = new Intent(MainActivity.this, MapBoxActivity.class);

                //Creamos la información a pasar entre actividades
                Bundle b = new Bundle();
                b.putString("FILEGXP", "betxi.gpx");

                //Añadimos la información al intent
                intent.putExtras(b);

                //Iniciamos la nueva actividad
                startActivity(intent);
            }
        });
    }
}
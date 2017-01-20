package quercus.takephoto;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 26;

    //Necesitamos un Boton y un imageView
    private Button bt_hacerfoto;
    private Button bt_getFoto;
    private ImageView img;
    private ClienteBD clientDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        clientDB=new ClienteBD();
        //Relacionamos con el XML
        img = (ImageView) this.findViewById(R.id.imageView1);
        bt_hacerfoto = (Button) this.findViewById(R.id.button1);
        bt_getFoto = (Button) findViewById(R.id.button2);
        //Añadimos el Listener Boton
        bt_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent para llamar a la Camara
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //Creamos una carpeta en la memeria del terminal
                File imagesFolder = new File(
                        Environment.getExternalStorageDirectory(), "Tutorialeshtml5");
                imagesFolder.mkdirs();
                //añadimos el nombre de la imagen
                File image = new File(imagesFolder, "foto.jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, 1);
            }
        });

        bt_getFoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Imagen image=clientDB.getImage("imageTest");
                if(image!=null) {
                    Bitmap imageBM = image.getImage();
                    int nh = (int) (imageBM.getHeight() * (512.0 / imageBM.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(imageBM, 512, nh, true);
                    img.setImageBitmap(scaled);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory()+
                            "/Tutorialeshtml5/"+"foto.jpg");
            int nh = (int) ( bMap.getHeight() * (512.0 / bMap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bMap, 512, nh, true);
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            img.setImageBitmap(scaled);
            clientDB.insertImage("imageTest", Environment.getExternalStorageDirectory()+
                    "/Tutorialeshtml5/"+"foto.jpg");
        }
    }

    private void checkPermissions(){
        //The location permission is required on API 23+ to obtain BLE scan results
        int result = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            //Ask for the location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                    REQUEST_PERMISSION);
        }
    }
}

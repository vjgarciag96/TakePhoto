package quercus.takephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by victor on 23/12/16.
 */

public class ClienteBD {
    private Connection connection = null;
    private final String TAG="db";

    public ClienteBD(){
        connect();
    }

    private boolean connect() {
        try {
            /*Necesario para acceder a Interner*/
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            // Creación de la conexión a la BD
            Class.forName("org.mariadb.jdbc.Driver");
            //String serverName = "10.0.2.2"; // Direccion IP for AVD
            String serverNamePublic="192.168.0.113";
            String portNumber = "3306";  // Puerto
            String dbName = "victor"; // Identificador del servicio o instancia
            String url = "jdbc:mariadb://" + serverNamePublic + ":" + portNumber +
                    "/" + dbName;
            String username = "victorAndroid"; // usuario de la BD
            String password = "12345678"; // contraseña
            connection = DriverManager.getConnection(url, username, password);
            Log.d(TAG, "Conexion realizada. " + connection);
            return true;
        } catch(ClassNotFoundException c){
            Log.d(TAG, "ClassNotFound:"+c.getMessage());
            return false;
        }
        catch (SQLException e) {
            // Error. No se ha podido conectar a la BD
            Log.d(TAG, "error en la conexion: " + e.toString());
            return false;
        }
    }

    public boolean closeConnection(){
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            Log.d(TAG, "error al cerrar la conexion "+e.toString());
        }
        return false;
    }

    public boolean insertImage(String id, String imagePath){
        FileInputStream fis=null;
        String query="INSERT INTO pruebaImagen(id, image) VALUES(?, ?);";
        boolean inserted=false;
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            File file=new File(imagePath);
            fis=new FileInputStream(file);
            preparedStatement.setBinaryStream(2, (InputStream) fis, (int)file.length());
            inserted=preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inserted;
    }

    public Imagen getImage(String id){
        String query="SELECT * FROM pruebaImagen WHERE id=?;";
        Imagen image=null;
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
                String identifier=rs.getString("id");
                Blob imageBlob=rs.getBlob("image");
                byte[] data=imageBlob.getBytes(1, (int)imageBlob.length());
                Bitmap imageBitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
                image=new Imagen(identifier, imageBitmap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean executeQuery(String query){
        //Execute a query
        Log.d(TAG, "Creating statement...");
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //Extract data from result set
            /*while(rs.next()){
                //Retrieve by column name
                int id  = rs.getInt("id");
                //Display values
                Log.d(TAG, "ID: " + id);
            }*/
            //Clean-up environment
            stmt.close();
            Log.d(TAG, "la query termina bien");
            return true;
        } catch (SQLException e) {
            Log.d(TAG, "error al hacer la consulta " + e.toString());
        }
        return false;
    }

}

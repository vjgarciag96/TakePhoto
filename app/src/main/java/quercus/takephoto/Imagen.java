package quercus.takephoto;

import android.graphics.Bitmap;

/**
 * Created by victor on 20/01/17.
 */

public class Imagen {

    String id;
    Bitmap image;

    public Imagen(String id, Bitmap image){
        this.id=id;
        this.image=image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

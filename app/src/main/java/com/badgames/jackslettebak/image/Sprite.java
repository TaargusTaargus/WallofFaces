package com.badgames.jackslettebak.image;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Jack Slettebak on 10/22/2017.
 */

public class Sprite {

    private Bitmap image;
    private Integer color;
    private Uri uri;

    public Sprite( Bitmap image, Integer color ) {
        this.color = color;
        this.image = image;
    }

    public Bitmap getImage() { return image; }
    public Integer getColor() { return color; }
    public Uri getUri() { return uri; }

}

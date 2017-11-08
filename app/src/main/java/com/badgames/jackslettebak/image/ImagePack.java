package com.badgames.jackslettebak.image;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Jack Slettebak on 10/28/2017.
 */

public class ImagePack extends HashMap <Integer, Bitmap > {

    private int groupId = 0;

    public void addImage( Bitmap image ) {
        super.put( groupId, image );
        groupId += 1;
    }

    public Bitmap [] getImagesArray() {
        LinkedList<Bitmap> images = new LinkedList< Bitmap >( values() );
        return images.toArray( new Bitmap[ images.size() ] );
    }

}

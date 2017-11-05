package com.badgames.jackslettebak.image;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Jack Slettebak on 10/28/2017.
 */

public class ImagePack extends HashMap <Integer, Sprite > {

    private int groupId = 0;

    public void addImage( Bitmap image ) {
        super.put( groupId, new Sprite( image, groupId ) );
        groupId += 1;
    }

}

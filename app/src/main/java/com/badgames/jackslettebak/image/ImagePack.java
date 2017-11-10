package com.badgames.jackslettebak.image;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Jack Slettebak on 10/28/2017.
 */

public class ImagePack extends LinkedList< Bitmap > {

    private int groupId = 0;

    public Bitmap [] getImagesArray() {
        return toArray( new Bitmap[ size() ] );
    }

}

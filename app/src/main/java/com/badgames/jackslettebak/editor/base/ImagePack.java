package com.badgames.jackslettebak.editor.base;

import android.graphics.Bitmap;

import java.util.LinkedList;

/**
 * Created by Jack Slettebak on 10/28/2017.
 */

public class ImagePack extends LinkedList< Bitmap > {

    private int groupId = 0;

    public Bitmap [] getImagesArray() {
        return toArray( new Bitmap[ size() ] );
    }

}

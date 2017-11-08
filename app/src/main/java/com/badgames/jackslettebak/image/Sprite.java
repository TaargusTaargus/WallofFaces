package com.badgames.jackslettebak.image;

import android.graphics.Bitmap;

/**
 * Created by Jack Slettebak on 10/22/2017.
 */

public class Sprite {

    private Bitmap image;
    private Integer group;

    public Sprite( Bitmap image, Integer group ) {
        this.group = group;
        this.image = image;
    }

    public Bitmap getImage() { return image; }
    public Integer getGroupId() { return group; }

}

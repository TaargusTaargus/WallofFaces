package com.badgames.jackslettebak.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.badgames.jackslettebak.walloffaces.R;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class Globals {
    public static int SCREEN_HEIGHT = 0;
    public static int SCREEN_WIDTH = 0;
    public static float SCREEN_DENSITY = 0;
    public static Bitmap [] IMAGES = null;
    public static int [] BACKGROUNDS = {
            Color.GREEN,
            Color.RED,
            Color.BLUE
    };

    public enum Background {
        BLUE( R.drawable.blue_background ),
        GREEN( R.drawable.green_background ),
        RED( R.drawable.red_background ),
        ORANGE( R.drawable.orange_background ),
        PURPLE( R.drawable.purple_background ),
        YELLOW( R.drawable.yellow_background );

        int resource;

        Background( int resource ) {
            this.resource = resource;
        }

        public Bitmap getBitmap( Context context ) {
            return BitmapFactory.decodeResource( context.getResources(), resource );
        }

        public Drawable getDrawable( Context context ) {
            return new BitmapDrawable( context.getResources(), getBitmap( context ) );
        }

        public int getResource() {
            return resource;
        }
    }

}

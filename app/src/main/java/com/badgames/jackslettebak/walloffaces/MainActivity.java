package com.badgames.jackslettebak.walloffaces;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import com.badgames.jackslettebak.framework.WOFView;
import com.badgames.jackslettebak.utilities.Globals;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    public final static int [] DEFAULT_IMAGES = {
            R.drawable.sylvester_wof,
            R.drawable.seagal_wof
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        startActivity( new Intent( this, FaceCaptureActivity.class ) );

        Display display = getWindowManager().getDefaultDisplay();
        if ( Build.VERSION.SDK_INT >= 17 ){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics( realMetrics );
            Globals.SCREEN_WIDTH = realMetrics.widthPixels;
            Globals.SCREEN_HEIGHT = realMetrics.heightPixels;

        } else if ( Build.VERSION.SDK_INT >= 14 ) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod( "getRawHeight" );
                Method mGetRawW = Display.class.getMethod( "getRawWidth" );
                Globals.SCREEN_WIDTH = ( Integer ) mGetRawW.invoke( display );
                Globals.SCREEN_HEIGHT = ( Integer ) mGetRawH.invoke( display );
            } catch ( Exception e ) {
                //this may not be 100% accurate, but it's all we've got
                Globals.SCREEN_WIDTH = display.getWidth();
                Globals.SCREEN_HEIGHT = display.getHeight();
                Log.e( "Display Info", "Couldn't use reflection to get the real display metrics." );
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            Globals.SCREEN_WIDTH = display.getWidth();
            Globals.SCREEN_HEIGHT = display.getHeight();
        }

        Globals.SCREEN_HEIGHT -= 100;
        Globals.SCREEN_DENSITY = getResources().getDisplayMetrics().density;
        Globals.BLOCK_HEIGHT = Globals.SCREEN_HEIGHT / Globals.N_BLOCKS_Y;
        Globals.BLOCK_WIDTH = Globals.SCREEN_WIDTH / Globals.N_BLOCKS_X;

        Globals.IMAGES = new Bitmap[ DEFAULT_IMAGES.length ];
        for( int i = 0; i < DEFAULT_IMAGES.length; i++ )
            Globals.IMAGES[ i ] = BitmapFactory.decodeResource( getResources(),
                                                                DEFAULT_IMAGES[ i ] );

        setContentView( new WOFView( this ) );

    }

}

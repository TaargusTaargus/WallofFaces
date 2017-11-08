package com.badgames.jackslettebak.walloffaces;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.badgames.jackslettebak.game.WOFView;
import com.badgames.jackslettebak.game.GameContext;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    public final static int [] DEFAULT_IMAGES = {
            R.drawable.sylvester_wof,
            R.drawable.seagal_wof
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        Display display = getWindowManager().getDefaultDisplay();
        if ( Build.VERSION.SDK_INT >= 17 ){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics( realMetrics );
            GameContext.SCREEN_WIDTH = realMetrics.widthPixels;
            GameContext.SCREEN_HEIGHT = realMetrics.heightPixels;

        } else if ( Build.VERSION.SDK_INT >= 14 ) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod( "getRawHeight" );
                Method mGetRawW = Display.class.getMethod( "getRawWidth" );
                GameContext.SCREEN_WIDTH = ( Integer ) mGetRawW.invoke( display );
                GameContext.SCREEN_HEIGHT = ( Integer ) mGetRawH.invoke( display );
            } catch ( Exception e ) {
                //this may not be 100% accurate, but it's all we've got
                GameContext.SCREEN_WIDTH = display.getWidth();
                GameContext.SCREEN_HEIGHT = display.getHeight();
                Log.e( "Display Info", "Couldn't use reflection to get the real display metrics." );
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            GameContext.SCREEN_WIDTH = display.getWidth();
            GameContext.SCREEN_HEIGHT = display.getHeight();
        }

        GameContext.SCREEN_HEIGHT -= 100;
        GameContext.SCREEN_DENSITY = getResources().getDisplayMetrics().density;
        GameContext.BLOCK_HEIGHT = GameContext.SCREEN_HEIGHT / GameContext.N_BLOCKS_Y;
        GameContext.BLOCK_WIDTH = GameContext.SCREEN_WIDTH / GameContext.N_BLOCKS_X;

        GameContext.IMAGES = new Bitmap[ DEFAULT_IMAGES.length ];
        for( int i = 0; i < DEFAULT_IMAGES.length; i++ )
            GameContext.IMAGES[ i ] = BitmapFactory.decodeResource( getResources(),
                                                                DEFAULT_IMAGES[ i ] );

        startActivity( new Intent( this, FaceCaptureActivity.class ) );
    }

}

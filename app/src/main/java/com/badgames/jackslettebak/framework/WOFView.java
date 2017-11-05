package com.badgames.jackslettebak.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.badgames.jackslettebak.utilities.Globals;
import com.badgames.jackslettebak.walloffaces.MainActivity;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class WOFView extends SurfaceView
                            implements View.OnTouchListener {

    private Wall game;

    public WOFView( Context context ) {
        super( context );
        this.game = new Wall( Globals.N_BLOCKS_X, Globals.N_BLOCKS_Y,
                                Globals.BLOCK_WIDTH, Globals.BLOCK_HEIGHT,
                                Globals.IMAGES );
        this.game.init();
        setBackgroundColor( Color.WHITE );
        setOnTouchListener( this );
    }

    public void draw( Canvas canvas ) {
        canvas.drawColor( Color.WHITE );
        super.draw( canvas );
        game.draw( canvas );
    }

    public void requestDraw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            postInvalidate();
        } finally {
            if ( canvas != null ) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }

    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        game.onSelect( motionEvent.getX(), motionEvent.getY() );
        requestDraw();
        return false;
    }
}

package com.badgames.jackslettebak.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;

import com.badgames.jackslettebak.utilities.Globals;

/**
 * Created by Jack Slettebak on 10/30/2017.
 */

public class FaceCropView extends SurfaceView
        implements View.OnTouchListener {

    private Bitmap background, cropper;
    private Float scale = new Float( 1.f );
    private Paint paint;
    private Point cropSize = new Point( 200, 200 );
    private PointF location;
    private ScaleGestureDetector scaleGestureDetector;

    public FaceCropView( Context context, Bitmap image, Bitmap cropImage ) {
        super( context );

        this.background = Bitmap.createScaledBitmap( image, Globals.SCREEN_WIDTH, Globals.SCREEN_HEIGHT, false );
        this.cropper = cropImage;
        this.location = new PointF( ( Globals.SCREEN_WIDTH - cropSize.x ) / 2,
                ( Globals.SCREEN_HEIGHT - cropSize.y ) / 2 );
        this.paint = new Paint();
        this.scaleGestureDetector = new ScaleGestureDetector( context, new ScaleListener() );

        setBackgroundColor( Color.TRANSPARENT );
        setOnTouchListener( this );
    }

    public void draw( Canvas canvas ) {
        super.draw( canvas );
        canvas.save();
        canvas.scale( scale, scale );
        canvas.drawBitmap( background, 0.f, 0.f, paint );
        canvas.drawBitmap( cropper, location.x, location.y, paint );
        canvas.restore();
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

    public Bitmap getCroppedBitmap() {
        return Bitmap.createBitmap( background, ( int )location.x, ( int ) location.y,
                                    cropper.getWidth(), cropper.getHeight() );
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        location.x = motionEvent.getX();
        location.y = motionEvent.getY();
        requestDraw();
        return true;
    }

    @Override
    public boolean onTouchEvent( MotionEvent ev ) {
        scaleGestureDetector.onTouchEvent( ev );
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale( ScaleGestureDetector detector ) {
            scale *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scale = Math.max( 0.1f, Math.min( scale, 5.0f ) );

            requestDraw();
            return true;
        }
    }

}

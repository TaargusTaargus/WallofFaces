package com.badgames.jackslettebak.image.image.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.badgames.jackslettebak.game.GameContext;

/**
 * Created by Jack Slettebak on 10/30/2017.
 */

public class FaceCropView extends SurfaceView
        implements View.OnTouchListener {

    private final static Point CROP_SIZE_INTERVAL = new Point( 50, 50 );
    private final static Point DEFAULT_CROP_SIZE = new Point( 300, 300 );
    private final static Point MAX_CROP_SIZE = new Point( 500, 500 );
    private final static Point MIN_CROP_SIZE = new Point( 200, 200 );

    private Bitmap background, cropper;
    private Paint paint;
    private Point cropSize = new Point( DEFAULT_CROP_SIZE );
    private PointF location, offset;

    public FaceCropView( Context context, Bitmap image, Bitmap cropImage ) {
        super( context );

        this.background = Bitmap.createScaledBitmap( image, GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT, false );
        this.cropper = Bitmap.createScaledBitmap( cropImage, cropSize.x, cropSize.y, false );
        this.location = new PointF( ( GameContext.SCREEN_WIDTH - cropSize.x ) / 2,
                ( GameContext.SCREEN_HEIGHT - cropSize.y ) / 2 );
        this.offset = new PointF( - cropSize.x / 2, - cropSize.y / 2 );
        this.paint = new Paint();

        setBackgroundColor( Color.TRANSPARENT );
        setOnTouchListener( this );
    }


    public void draw( Canvas canvas ) {
        super.draw( canvas );
        canvas.drawBitmap( background, 0.f, 0.f, paint );
        canvas.drawBitmap( cropper, location.x, location.y, paint );
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

    public void adjustCropSize( Integer delta ) {
        cropSize = new Point(
                Math.max(
                        Math.min( cropSize.x + delta * CROP_SIZE_INTERVAL.x, GameContext.SCREEN_WIDTH ),
                        MIN_CROP_SIZE.x
                ),
                Math.max(
                        Math.min( cropSize.y + delta * CROP_SIZE_INTERVAL.y, GameContext.SCREEN_HEIGHT ),
                        MIN_CROP_SIZE.y
                )
        );
        cropper = Bitmap.createScaledBitmap( cropper, cropSize.x, cropSize.y, false );
        this.offset = new PointF( - cropSize.x / 2, - cropSize.y / 2 );
        requestDraw();
    }

    public Bitmap getCroppedImage() {
        return Bitmap.createBitmap(
                background, ( int ) location.x, ( int ) location.y,
                ( int ) ( location.x + cropper.getWidth() <= GameContext.SCREEN_WIDTH ?
                            cropper.getWidth() : GameContext.SCREEN_WIDTH - location.x ),
                ( int ) ( location.y + cropper.getHeight() <= GameContext.SCREEN_HEIGHT ?
                            cropper.getHeight() : GameContext.SCREEN_HEIGHT - location.y )
        );
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        location.x = motionEvent.getX() + offset.x;
        location.y = motionEvent.getY() + offset.y;
        requestDraw();
        return true;
    }

}

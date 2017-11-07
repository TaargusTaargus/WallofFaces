package com.badgames.jackslettebak.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.Toast;

import com.badgames.jackslettebak.utilities.Globals;

/**
 * Created by Jack Slettebak on 10/31/2017.
 */

public class FaceEditView extends SurfaceView
        implements View.OnTouchListener {

    public final int DEFAULT_ERASER_RADIUS = ( int ) Globals.SCREEN_DENSITY * 8;
    public final int MAX_ERASER_RADIUS = ( int ) Globals.SCREEN_DENSITY * 16;
    public final int MIN_ERASER_RADIUS = ( int ) Globals.SCREEN_DENSITY * 2;

    private Bitmap background, image;
    private Integer eraserSize;
    private Paint eraserPaint, backgroundPaint, imagePaint;
    private PointF location, bounds, eraser;

    public FaceEditView(Context context, Bitmap imageToEdit, Bitmap border ) {
        super( context );
        this.background = border;
        this.backgroundPaint = new Paint();
        this.eraser = new PointF( 0.f, 0.f );
        this.eraserPaint = new Paint();
        this.eraserSize = DEFAULT_ERASER_RADIUS;
        this.image = imageToEdit;
        this.imagePaint = new Paint();
        this.location = new PointF( 0.f, 0.f );


        eraserPaint.setColor( Color.WHITE );
        image.setHasAlpha( true );

        getViewTreeObserver().addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener( this );
                background = Bitmap.createScaledBitmap( background, getWidth(), getHeight(), false );
                image = Bitmap.createScaledBitmap( image, getWidth(), getHeight(), false );
                bounds = new PointF( image.getWidth(), image.getHeight() );
                return true;
            }
        } );
        setBackgroundColor( Color.GRAY );
        setOnTouchListener( this );

        requestDraw();
    }


    public void attachSeekBar( SeekBar seekbar ) {
        seekbar.setMax( MAX_ERASER_RADIUS - MIN_ERASER_RADIUS );
        seekbar.setProgress( DEFAULT_ERASER_RADIUS );
        seekbar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged( SeekBar seekBar, int i, boolean b ) {
                eraserSize = i + MIN_ERASER_RADIUS;
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                Toast.makeText( getContext(), "Eraser size set to: " + eraserSize, Toast.LENGTH_SHORT ).show();
            }

        } );
        Toast.makeText( getContext(), "Eraser size currently set to: " + eraserSize, Toast.LENGTH_SHORT ).show();
    }

    public void draw( Canvas canvas ) {
        super.draw( canvas );
        canvas.drawBitmap( image, location.x, location.y, imagePaint );
        canvas.drawCircle( eraser.x, eraser.y, eraserSize, eraserPaint );
        canvas.drawBitmap( background, location.x, location.y, backgroundPaint );
    }

    public void requestDraw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            postInvalidate();
        } finally {
            if ( canvas != null ) {
                getHolder().unlockCanvasAndPost( canvas );
            }
        }
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        eraser.set( motionEvent.getX(), motionEvent.getY() );

        for( int dx = -eraserSize,
            x = ( int ) ( motionEvent.getX() - location.x - eraserSize );
            dx < eraserSize; dx++, x++ ) {
            for ( int dy = -eraserSize,
                 y = ( int ) ( motionEvent.getY() - location.y - eraserSize );
                 dy < eraserSize; dy++, y++ ) {
                if ( x >= 0 && x < bounds.x && y >= 0 && y < bounds.y
                        && Math.sqrt( dx * dx + dy * dy ) <= eraserSize ) {
                    image.setPixel( x, y, Color.TRANSPARENT );
                }
            }
        }

        requestDraw();
        return true;
    }

    public Bitmap getFinalImage() {
        Bitmap finalImage = Bitmap.createBitmap( ( int ) bounds.x, ( int ) bounds.y, Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( finalImage );
        super.draw( canvas );
        canvas.drawBitmap( image, location.x, location.y, imagePaint );
        canvas.drawBitmap( background, location.x, location.y, backgroundPaint );
        return finalImage;
    }

    public void setBackgroundImage( Bitmap image ) { background = Bitmap.createScaledBitmap( image, getWidth(), getHeight(), false ); }

}


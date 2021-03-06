package com.badgames.jackslettebak.editor.views;

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

import com.badgames.jackslettebak.game.game.utilities.GameContext;
import com.badgames.jackslettebak.utilities.DrawTask;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jack Slettebak on 10/31/2017.
 */

public class FaceEditView extends SurfaceView
        implements View.OnTouchListener {

    public final int DEFAULT_ERASER_RADIUS = ( int ) GameContext.SCREEN_DENSITY * 8;
    public final int MAX_ERASER_RADIUS = ( int ) GameContext.SCREEN_DENSITY * 16;
    public final int MIN_ERASER_RADIUS = ( int ) GameContext.SCREEN_DENSITY * 2;

    private Bitmap background, border, image;
    private Integer eraserSize;
    private Paint eraserPaint, imagePaint;
    private PointF location, bounds, eraser;

    public FaceEditView( Context context, Bitmap imageToEdit ) {
        super( context );
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
                image = Bitmap.createScaledBitmap( image, getWidth(), getHeight(), false );
                bounds = new PointF( image.getWidth(), image.getHeight() );
                return true;
            }
        } );
        setBackgroundColor( Color.WHITE );
        setOnTouchListener( this );

        ( new ScheduledThreadPoolExecutor( 1 ) )
                .scheduleAtFixedRate(
                        new DrawTask( this ),
                        0l,
                        GameContext.FRAMES_PER_SECOND,
                        TimeUnit.MILLISECONDS
                );
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
        if( background != null )
            canvas.drawBitmap( background, 0.f, 0.f, imagePaint );

        canvas.drawBitmap( image, location.x, location.y, imagePaint );
        canvas.drawCircle( eraser.x, eraser.y, eraserSize, eraserPaint );

        if( border != null )
            canvas.drawBitmap( border, location.x, location.y, imagePaint );
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
        return true;
    }

    public Bitmap getEdittedImage() {
        Bitmap finalImage = Bitmap.createBitmap( ( int ) bounds.x, ( int ) bounds.y, Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( finalImage );
        super.draw( canvas );
        if( background != null )
            canvas.drawBitmap( background, 0.f, 0.f, imagePaint );

        canvas.drawBitmap( image, location.x, location.y, imagePaint );

        if( border != null )
            canvas.drawBitmap( border, location.x, location.y, imagePaint );

        return finalImage;
    }

    public void setBackgroundImage( Bitmap image ) {
        background = Bitmap.createScaledBitmap( image, ( int ) bounds.x, ( int ) bounds.y, false );
    }

    public void setBorderImage( Bitmap image ) {
        border = Bitmap.createScaledBitmap( image, ( int ) bounds.x, ( int ) bounds.y, false );
    }

}


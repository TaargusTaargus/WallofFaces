package com.badgames.jackslettebak.framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badgames.jackslettebak.image.Sprite;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class Face {

    public final int SELECTED = 127;
    public final int DESELECTED = 255;

    private Bitmap image;
    private Integer type;
    private Paint paint, background;
    private PointF location;

    public Face(PointF location, Sprite sprite, Integer type ) {
        this.image = sprite.getImage();
        this.location = location;
        this.paint = new Paint();
        this.type = type;
        this.background = new Paint();
        this.background.setColor( sprite.getColor() );
        setSelected( false );
    }

    public void draw( Canvas canvas ) {
        canvas.drawRect( location.x, location.y,
                            location.x + image.getWidth(), location.y + image.getHeight(),
                            background );
        canvas.drawBitmap( image, location.x, location.y, paint );
    }

    public void move( PointF dl ) {
        location.offset( dl.x, dl.y );
    }

    public void setLocation( PointF loc ) {
        this.location = new PointF( loc.x, loc.y );
    }

    public void setSelected( boolean selected ) {
        paint.setAlpha( selected ? SELECTED : DESELECTED );
        background.setAlpha( selected ? SELECTED : DESELECTED );
    }

    public Bitmap getImage() { return image; }
    public Integer getType() { return type; }
    public PointF getLocation() { return location; }

}
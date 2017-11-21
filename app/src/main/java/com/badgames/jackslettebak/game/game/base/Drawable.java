package com.badgames.jackslettebak.game.game.base;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Jack Slettebak on 11/13/2017.
 */

public interface Drawable {

    public void draw( Canvas canvas );
    public PointF getLocation();
    public void move( PointF distance );
    public void setLocation( PointF location );

}

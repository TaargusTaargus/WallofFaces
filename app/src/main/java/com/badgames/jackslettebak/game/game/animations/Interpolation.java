package com.badgames.jackslettebak.game.game.animations;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.badgames.jackslettebak.game.game.base.GameSprite;

/**
 * Created by Jack Slettebak on 11/13/2017.
 */

public class Interpolation implements Animation {

    private GameSprite image;
    private Float current = 0.f, time;
    private PointF begin, end;

    public Interpolation(GameSprite image, PointF destination, Float time ) {
        this.begin = image.getLocation();
        this.end = destination;
        this.image = image;
        this.time = time;
    }

    public void tick( float dt ) {
        current += dt;
        image.setLocation(
                new PointF(
                        current / time * begin.x + ( 1 - current / time ) * end.x,
                        current / time * begin.y + ( 1 - current / time ) * end.y
                )
        );
    }

    public void draw( Canvas canvas ) {
        image.draw( canvas );
    }

    public void finalize() {
        image.setLocation( end );
    }

    public boolean isAlive() { return current < time; }
    public GameSprite getAnimatedImage() { return image; }

}

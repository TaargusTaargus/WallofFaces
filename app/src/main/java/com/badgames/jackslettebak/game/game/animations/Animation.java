package com.badgames.jackslettebak.game.game.animations;

import android.graphics.Canvas;

import com.badgames.jackslettebak.game.game.base.GameSprite;

/**
 * Created by Jack Slettebak on 11/14/2017.
 */

public interface Animation {

    public boolean isAlive();
    public void draw( Canvas canvas );
    public void finalize();
    public void tick( float dl );
    public GameSprite getAnimatedImage();

}

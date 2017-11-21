package com.badgames.jackslettebak.game.game.animations;

import android.graphics.Canvas;

import com.badgames.jackslettebak.game.game.base.GameSprite;

import java.util.LinkedList;

/**
 * Created by Jack Slettebak on 11/15/2017.
 */

public class Animator extends LinkedList< Animation> {

    public interface AnimationCallback {

        public void onAnimationComplete( GameSprite sprite );

    }

    private AnimationCallback callback;

    public Animator( AnimationCallback callback ) {
        this.callback = callback;
    }

    public void draw( Canvas canvas ) {
        for( Animation e : this )
            e.draw( canvas );
    }

    public void tick( Float fps ) {
        for( Animation e : this ) {
            e.tick( fps );
            if( ! e.isAlive() ) {
                e.finalize();
                callback.onAnimationComplete( e.getAnimatedImage() );
                remove( e );
            }
        }
    }

}

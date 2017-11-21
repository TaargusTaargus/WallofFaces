package com.badgames.jackslettebak.game.game.utilities;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.badgames.jackslettebak.game.game.animations.Animator;
import com.badgames.jackslettebak.game.game.views.WOFView;

/**
 * Created by Jack Slettebak on 11/12/2017.
 */

public class GameTask implements Runnable {

    private Animator animator;
    private Float rate;
    private SurfaceHolder holder;
    private WOFView parent;

    public GameTask( WOFView parent, Float rate ) {
        this.animator = parent.getAnimator();
        this.holder = parent.getHolder();
        this.rate = rate;
        this.parent = parent;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            animator.tick( rate );
            parent.postInvalidate();
        } finally {
            if ( canvas != null ) {
                holder.unlockCanvasAndPost( canvas );
            }
        }
    }

}

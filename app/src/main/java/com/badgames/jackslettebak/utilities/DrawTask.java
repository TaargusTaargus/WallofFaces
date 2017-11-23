package com.badgames.jackslettebak.utilities;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jack Slettebak on 11/12/2017.
 */

public class DrawTask implements Runnable {

    private SurfaceHolder holder;
    private SurfaceView parent;

    public DrawTask( SurfaceView parent ) {
        this.holder = parent.getHolder();
        this.parent = parent;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            parent.postInvalidate();
        } finally {
            if ( canvas != null ) {
                holder.unlockCanvasAndPost( canvas );
            }
        }
    }

}

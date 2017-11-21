package com.badgames.jackslettebak.game.game.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.camera2.params.Face;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.badgames.jackslettebak.game.game.utilities.GameContext;
import com.badgames.jackslettebak.game.game.animations.Animator;
import com.badgames.jackslettebak.game.game.animations.Interpolation;
import com.badgames.jackslettebak.game.game.base.GameSprite;
import com.badgames.jackslettebak.game.game.base.GameBoard;
import com.badgames.jackslettebak.game.game.utilities.GameTask;
import com.badgames.jackslettebak.utilities.DrawTask;
import com.badgames.jackslettebak.walloffaces.GameActivity;

import java.lang.reflect.GenericArrayType;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class WOFView extends SurfaceView
                            implements View.OnTouchListener, Animator.AnimationCallback {

    private Animator animator;
    private Boolean lock = new Boolean( false );
    private PointF faceStartPosition, touchStartPosition;
    private ScheduledThreadPoolExecutor executor;
    private GameBoard game;

    public WOFView( Context context ) {
        super( context );
        this.animator = new Animator( this );
        this.game = new GameBoard(
                GameContext.SCREEN_WIDTH, GameContext.SCREEN_HEIGHT,
                GameContext.N_BLOCKS_X, GameContext.N_BLOCKS_Y,
                GameContext.IMAGES
        );

        setBackgroundColor( Color.BLACK );
        setOnTouchListener( this );
        ( executor = new ScheduledThreadPoolExecutor( 1 ) )
                .scheduleAtFixedRate(
                        new GameTask( this, new Float( GameContext.FRAMES_PER_SECOND  ) ),
                        0l,
                        GameContext.FRAMES_PER_SECOND,
                        TimeUnit.MILLISECONDS
                );
    }

    public void draw( Canvas canvas ) {
        super.draw( canvas );
        game.draw( canvas );
        animator.draw( canvas );
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        switch( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                faceStartPosition = game.getFaceFromCoordinates( motionEvent.getX(), motionEvent.getY() ).getLocation();
                touchStartPosition = new PointF( motionEvent.getX(), motionEvent.getY() );
                break;
            case MotionEvent.ACTION_MOVE:
                if( Math.abs( motionEvent.getX() - touchStartPosition.x ) > GameContext.BLOCK_SWIPE_THRESHOLD_X
                        && ! lock ) {
                    PointF faceEndPosition = new PointF(
                            faceStartPosition.x + ( motionEvent.getX() > touchStartPosition.x ? 1 : -1 ) * GameContext.BLOCK_WIDTH,
                            faceStartPosition.y
                    );

                    GameSprite start = game.getFaceFromCoordinates( faceStartPosition.x, faceStartPosition.y );
                    start.setLocation( faceEndPosition );

                    GameSprite end = game.getFaceFromCoordinates( faceEndPosition.x, faceEndPosition.y );
                    end.setLocation( faceStartPosition );

                    game.setFaceAtCoordinate( start );
                    game.setFaceAtCoordinate( end );

                    lock = true;
                    /*
                    animator.add(
                            new Interpolation(
                                    game.getFaceFromCoordinates( faceEndPosition.x, faceEndPosition.y ),
                                    faceStartPosition,
                                    5000.f
                            )
                    );
                    animator.add(
                            new Interpolation(
                                    game.getFaceFromCoordinates( touchStartPosition.x, touchStartPosition.y ),
                                    faceEndPosition,
                                    5000.f
                            )
                    );
                    */
                }
                else if( Math.abs( motionEvent.getY() - touchStartPosition.y ) > GameContext.BLOCK_SWIPE_THRESHOLD_Y
                        && ! lock ) {
                    PointF faceEndPosition = new PointF(
                            faceStartPosition.x,
                            faceStartPosition.y + ( motionEvent.getY() > touchStartPosition.y ? 1 : -1 ) * GameContext.BLOCK_HEIGHT
                    );

                    GameSprite start = game.getFaceFromCoordinates( faceStartPosition.x, faceStartPosition.y );
                    start.setLocation( faceEndPosition );

                    GameSprite end = game.getFaceFromCoordinates( faceEndPosition.x, faceEndPosition.y );
                    end.setLocation( faceStartPosition );

                    game.setFaceAtCoordinate( start );
                    game.setFaceAtCoordinate( end );

                    lock = true;
                    /*
                    animator.add(
                            new Interpolation(
                                    game.getFaceFromCoordinates( faceEndPosition.x, faceEndPosition.y ),
                                    faceStartPosition,
                                    5000.f
                            )
                    );
                    animator.add(
                            new Interpolation(
                                    game.getFaceFromCoordinates( touchStartPosition.x, touchStartPosition.y ),
                                    faceEndPosition,
                                    5000.f
                            )
                    );
                    */
                }
                break;
            case MotionEvent.ACTION_UP:
                lock = false;
                break;
        }
        return true;
    }

    @Override
    public void onAnimationComplete( GameSprite sprite ) {
        game.setFaceAtCoordinate( sprite );
    }

    public Animator getAnimator() { return animator; }

}

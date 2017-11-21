package com.badgames.jackslettebak.walloffaces;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.badgames.jackslettebak.game.game.utilities.GameContext;
import com.badgames.jackslettebak.game.game.views.WOFView;
import com.badgames.jackslettebak.utilities.ImageBuilder;

/**
 * Created by Jack Slettebak on 11/7/2017.
 */

public class GameActivity extends Activity {

    private LinearLayout layout;
    private WOFView gameView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.game_activity_layout );
        loadGame();
    }

    @Override
    public void onWindowFocusChanged( boolean hasFocus ) {
        super.onWindowFocusChanged( hasFocus );
        if ( hasFocus )
            gameView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
    }

    private void loadGame() {
        layout = ( LinearLayout ) findViewById( R.id.game_screen );
        layout.addView( gameView = new WOFView( this ) );
    }

}

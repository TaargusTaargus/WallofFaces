package com.badgames.jackslettebak.walloffaces;

import android.app.Activity;
import android.os.Bundle;

import com.badgames.jackslettebak.game.WOFView;

/**
 * Created by Jack Slettebak on 11/7/2017.
 */

public class GameActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( new WOFView( this ) );
    }

}

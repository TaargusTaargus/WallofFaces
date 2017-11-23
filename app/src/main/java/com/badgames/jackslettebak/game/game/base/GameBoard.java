package com.badgames.jackslettebak.game.game.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badgames.jackslettebak.game.game.utilities.GameContext;
import com.badgames.jackslettebak.editor.base.Sprite;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class GameBoard {

    private Bitmap [] images;
    private GameSprite [] wall;
    private Integer blocksOnX, blocksOnY, border = GameContext.IMAGE_BORDER, screenWidth, screenHeight, spriteHeight, spriteWidth;
    private SelectList selected;

    public GameBoard(
            Integer screenWidth, Integer screenHeight,
            Integer blocksOnX, Integer blocksOnY,
            Bitmap [] images
    ) {
        this.blocksOnX = blocksOnX;
        this.blocksOnY = blocksOnY;
        this.images = images;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.spriteHeight = screenHeight / blocksOnY;
        this.spriteWidth = screenWidth / blocksOnX;
        this.wall = new GameSprite[ blocksOnX * blocksOnY ];

        Random generator = new Random( ( new Date() ).getTime() );
        for(
                int i = 0, x = 0, y = 0;
                i < wall.length;
                i++, x = i % blocksOnX * spriteWidth, y = ( ( int ) ( i / blocksOnX ) ) * spriteHeight
        ) {
            Integer type = generator.nextInt( images.length );
            wall[ i ] = new GameSprite(
                    new PointF( x, y ),
                    new Sprite(
                            Bitmap.createScaledBitmap(
                                    images[ type ],
                                    spriteWidth - border,
                                    spriteHeight - border,
                                    false
                            ),
                            GameContext.BACKGROUNDS[ type ]
                    ),
                    type
            );
        }

    }

    public void draw( Canvas canvas ) {
        for( GameSprite face : wall ) {
            if( face != null )
                face.draw( canvas );
        }
        if( selected != null ) {
            for( GameSprite el : selected.values() )
                el.draw( canvas );
        }
    }

    public void delete( GameSprite sprite ) {
        delete( sprite.getLocation().x, sprite.getLocation().y );
    }

    public void delete( Float x, Float y ) {
        Integer index = get1DFromCoordinate( x, y );
        if( wall[ index ] != null ) {
            selected = new SelectList();
            selectRecursive( x, y, wall[ index ].getType(), selected );
            if( selected.size() >= 3 )
                selected = null;
            else
                deselect();
        }
    }

    public void restructure() {
        int missingX = 0;
        for(int x = 0, missingY = 0; x < blocksOnX; x++, missingY = 0 ) {
            for(int y = ( blocksOnY - 1 ) * blocksOnX + x; y >= 0; y -= blocksOnX ) {
                if( wall[ y ] == null )
                    missingY += 1;
                else if( missingY > 0 ) {
                    wall[ y + missingY * blocksOnX ] = wall[ y ];
                    wall[ y ].move( new PointF( 0, spriteHeight * missingY ) );
                    wall[ y ] = null;
                }
            }
            if( missingY == blocksOnY )
                missingX -= 1;
            if( missingX != 0 ) {
                for(int y = ( blocksOnY - 1 ) * blocksOnX + x; y >= 0; y -= blocksOnX ) {
                    if( wall[ y ] != null ) {
                        wall[ y - 1 ] = wall[ y ];
                        wall[ y - 1 ].move( new PointF( spriteWidth * missingX, 0 ) );
                        wall[ y ] = null;
                    }
                }
            }
        }
    }

    public GameSprite getFaceFromCoordinates( Float x, Float y ) {
        try {
            return wall[ get1DFromCoordinate( x, y ) ];
        } catch( ArrayIndexOutOfBoundsException e ) {
            return null;
        }
    }

    public GameSprite removeFaceFromCoordinate( Float x, Float y ) {
        try {
            Integer idx = get1DFromCoordinate( x, y );
            GameSprite face = wall[ idx ];
            wall[ idx ] = null;
            return face;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return null;
        }
    }

    public Integer get1DFromCoordinate( Float x, Float y ) {
        return ( ( int ) ( x / spriteWidth ) ) + blocksOnX * ( ( int ) ( y / spriteHeight ) );
    }

    public void setFaceAtCoordinate( GameSprite face ) {
        PointF loc = face.getLocation();
        wall[ get1DFromCoordinate( loc.x, loc.y ) ] = face;
    }

    // private METHODS
    private void deselect() {
        for( Integer key : selected.keySet() ) {
            GameSprite val = selected.get( key );
            val.setSelected( false );
            wall[ key ] = val;
        }
    }

    private void selectRecursive( Float x, Float y, final Integer type, SelectList list ) {
        if( x > screenWidth || x < 0.f
                || y > screenHeight || y < 0.f )
            return;

        GameSprite select = getFaceFromCoordinates( x, y );
        if( select == null  || select.getType() != type )
            return;

        else {
            Integer index = get1DFromCoordinate( x, y );
            list.put( select.getType(), select );
            wall[ index ] = null;
            selectRecursive( x + spriteWidth, y, type, list );
            selectRecursive( x, y + spriteHeight, type, list );
            selectRecursive( x - spriteWidth, y, type, list );
            selectRecursive( x, y - spriteHeight, type, list );
        }
    }

    private class SelectList extends HashMap< Integer, GameSprite> {
        public void select() {
            for( GameSprite val : values() )
                val.setSelected( true );
        }
    }

}

package com.badgames.jackslettebak.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badgames.jackslettebak.image.Sprite;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Jack Slettebak on 10/16/2017.
 */

public class Wall {

    private Bitmap [] images;
    private Face [] wall;
    private Integer spriteHeight, spriteWidth;
    private Integer height, width;
    private SelectList selected;

    public Wall( Integer width, Integer height,
                 Integer spriteWidth, Integer spriteHeight,
                 Bitmap [] images ) {
        this.height = height;
        this.images = images;
        this.spriteHeight = spriteHeight;
        this.spriteWidth = spriteWidth;
        this.wall = new Face[ height * width ];
        this.width = width;
    }

    public void clear() {
        this.wall = new Face[ height * width ];
    }

    public void draw( Canvas canvas ) {
        for( Face face : wall ) {
            if( face != null )
                face.draw( canvas );
        }
        if( selected != null ) {
            for( Face el : selected.values() )
                el.draw( canvas );
        }
    }

    public void init() {
        Random generator = new Random( ( new Date() ).getTime() );
        for ( int i = 0; i < wall.length; i++ ) {
            Integer type = generator.nextInt( images.length );
            wall[ i ] = new Face( new PointF( i % width * spriteWidth,
                                              ( ( int ) ( i / width ) ) * spriteHeight ),
                                  new Sprite(
                                          Bitmap.createScaledBitmap(
                                                  images[ type ],
                                                  spriteWidth,
                                                  spriteHeight,
                                                  false
                                          ),
                                          GameContext.BACKGROUNDS[ type ]
                                  ),
                    type
            );

        }
    }

    public void onSelect( Float x, Float y ) {
        Integer index = get1DFromCoordinate( x, y );
        if( selected != null && selected.containsKey( index ) ) {
            selected = null;
            restructure();
        } else {
            if( selected != null )
                deselect();
            if( wall[ index ] != null ) {
                selected = new SelectList();
                selectRecursive( x, y, wall[ index ].getType(), selected );
                if( selected.size() > 1 )
                    selected.select();
                else
                    deselect();
            }
        }
    }

    public void reinit() {
        clear();
        init();
    }



    // private METHODS

    public Face getFaceFromCoordinates( Float x, Float y ) {
        try {
            return wall[ get1DFromCoordinate( x, y ) ];
        } catch( ArrayIndexOutOfBoundsException e ) {
            return null;
        }
    }

    public Integer get1DFromCoordinate( Float x, Float y ) {
        return ( (int) ( x / spriteWidth ) ) + width * ( (int) ( y / spriteHeight ) );
    }

    private void deselect() {
        for( Integer key : selected.keySet() ) {
            Face val = selected.get( key );
            val.setSelected( false );
            wall[ key ] = val;
        }
    }

    private void restructure() {
        int missingX = 0;
        for( int x = 0, missingY = 0; x < width; x++, missingY = 0 ) {
            for( int y = ( height - 1 ) * width + x; y >= 0; y -= width  ) {
                if( wall[ y ] == null )
                    missingY += 1;
                else if( missingY > 0 ) {
                    wall[ y + missingY * width ] = wall[ y ];
                    wall[ y ].move( new PointF( 0, spriteHeight * missingY ) );
                    wall[ y ] = null;
                }
            }
            if( missingY == height )
                missingX -= 1;
            if( missingX != 0 ) {
                for( int y = ( height - 1 ) * width + x; y >= 0; y -= width  ) {
                    if( wall[ y ] != null ) {
                        wall[ y - 1 ] = wall[ y ];
                        wall[ y - 1 ].move( new PointF( spriteWidth * missingX, 0 ) );
                        wall[ y ] = null;
                    }
                }
            }
        }
    }

    private void selectRecursive( Float x, Float y, final Integer type, SelectList list ) {
        if( x > GameContext.SCREEN_WIDTH || x < 0.f
                || y > GameContext.SCREEN_HEIGHT || y < 0.f )
            return;

        Face select = getFaceFromCoordinates( x, y );
        if( select == null  || select.getType() != type )
            return;

        else {
            Integer index = get1DFromCoordinate( x, y );
            list.put( index, select );
            wall[ index ] = null;
            selectRecursive( x + spriteWidth, y, type, list );
            selectRecursive( x, y + spriteHeight, type, list );
            selectRecursive( x - spriteWidth, y, type, list );
            selectRecursive( x, y - spriteHeight, type, list );
        }
    }

    private class SelectList extends HashMap< Integer, Face > {
        public void select() {
            for( Face val : values() )
                val.setSelected( true );
        }
    }

}

package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

/**
 * PowerUp class used to define common traits of PowerUp objects
 * Allows game to efficiently process different types of power ups
 */
public abstract class PowerUp extends Sprite {

    // enum for types of power ups
    public enum PowerUpType {BLAST, BOMB};

    // private instance members
    private PowerUpType mPowerUpType;
    private boolean mHasBeenProcessed;

    // set the power up type
    public void setPowerUpType(PowerUpType powerUpType) {
        mPowerUpType = powerUpType;
    }

    public void setSquare(Square square) {
        setCenter(square.getCenter());
    }

    // process the power up -> called during collision
    public void process(){
        // collision occurs multiple times before OpsList removes PowerUp
        // check if PowerUp has been processed so that it is only applied 1x before it is removed
        if (!mHasBeenProcessed) {
            // apply power up based on type
            if (mPowerUpType == PowerUpType.BLAST) {
                CommandCenter.getInstance().getBomberman().increaseBlastPower();
            } else if (mPowerUpType == PowerUpType.BOMB) {
                CommandCenter.getInstance().getBomberman().addBombToUse();
            }

            Sound.playSound("powerup.wav");

            // update boolean so that only processed 1x
            mHasBeenProcessed = !mHasBeenProcessed;
        }
    }

}

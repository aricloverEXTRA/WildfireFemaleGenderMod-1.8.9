package com.wildfire.main.entitydata;

import net.minecraft.entity.player.EntityPlayer;
import com.wildfire.main.config.Configuration;

public class Breasts {
    private final EntityPlayer player;

    public Breasts() {
        this.player = null;
    }

    public Breasts(EntityPlayer player) {
        this.player = player;
    }

    private EntityPlayer requirePlayer() {
        if (player == null) throw new IllegalStateException("Breasts instance has no associated player");
        return player;
    }

    public float getXOffset() {
        EntityPlayer p = requirePlayer();
        return Configuration.getPlayerSettings(p).breastsOffsetX;
    }

    public boolean updateXOffset(float value) {
        EntityPlayer p = requirePlayer();
        if (validateOffset(value)) {
            Configuration.getPlayerSettings(p).breastsOffsetX = value;
            Configuration.saveConfig();
            return true;
        }
        return false;
    }

    public float getYOffset() {
        EntityPlayer p = requirePlayer();
        return Configuration.getPlayerSettings(p).breastsOffsetY;
    }

    public boolean updateYOffset(float value) {
        EntityPlayer p = requirePlayer();
        if (validateOffset(value)) {
            Configuration.getPlayerSettings(p).breastsOffsetY = value;
            Configuration.saveConfig();
            return true;
        }
        return false;
    }

    public float getZOffset() {
        EntityPlayer p = requirePlayer();
        return Configuration.getPlayerSettings(p).breastsOffsetZ;
    }

    public boolean updateZOffset(float value) {
        EntityPlayer p = requirePlayer();
        if (validateOffset(value)) {
            Configuration.getPlayerSettings(p).breastsOffsetZ = value;
            Configuration.saveConfig();
            return true;
        }
        return false;
    }

    public float getCleavage() {
        EntityPlayer p = requirePlayer();
        return Configuration.getPlayerSettings(p).breastsCleavage;
    }

    public boolean updateCleavage(float value) {
        EntityPlayer p = requirePlayer();
        if (validateCleavage(value)) {
            Configuration.getPlayerSettings(p).breastsCleavage = value;
            Configuration.saveConfig();
            return true;
        }
        return false;
    }

    public boolean isUniboob() {
        EntityPlayer p = requirePlayer();
        return Configuration.getPlayerSettings(p).breastsUniboob;
    }

    public boolean updateUniboob(boolean value) {
        EntityPlayer p = requirePlayer();
        Configuration.getPlayerSettings(p).breastsUniboob = value;
        Configuration.saveConfig();
        return true;
    }

    private boolean validateOffset(float value) {
        return value >= -10.0F && value <= 10.0F;
    }

    private boolean validateCleavage(float value) {
        return value >= 0.0F && value <= 10.0F;
    }
}

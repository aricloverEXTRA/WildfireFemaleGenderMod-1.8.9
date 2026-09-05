package com.wildfire.main;

import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.config.Configuration;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.GenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WildfireEventHandler {

    public WildfireEventHandler() {}

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        try {
            EntityPlayer player = evt.player;
            if (player == null) return;
            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft == null || minecraft.theWorld == null) return;

            if (!player.worldObj.isRemote) return;

            Configuration.PlayerGenderSettings settings = null;
            boolean isLocal = player == minecraft.thePlayer;
            if (isLocal) {
                settings = Configuration.getPlayerSettings(player);
                if (settings == null) return;
                if (!(player instanceof AbstractClientPlayer)) return;
            } else {

                if (!(player instanceof AbstractClientPlayer)) return;

                settings = Configuration.getPlayerSettings(minecraft.thePlayer);
                if (settings == null) return;
            }

            AbstractClientPlayer acp = (AbstractClientPlayer) player;
            GenderLayer.ensureRegisteredForPlayer(acp);
            BreastPhysics[] phys = GenderLayer.getPhysicsForPlayer(acp);
            if (phys == null) return;

            ItemStack chest = null;
            try { chest = player.inventory.armorInventory[2]; } catch (Throwable ignored) {}

            com.wildfire.api.IGenderArmor armor;
            if (chest == null || !(chest.getItem() instanceof ItemArmor)) {
                armor = new com.wildfire.api.IGenderArmor() {};
            } else if (chest.getItem() == net.minecraft.init.Items.leather_chestplate) {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 0.3f; }
                    @Override public float tightness() { return 0.5f; }
                };
            } else if (chest.getItem() == net.minecraft.init.Items.chainmail_chestplate) {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 0.5f; }
                    @Override public float tightness() { return 0.2f; }
                };
            } else if (chest.getItem() == net.minecraft.init.Items.golden_chestplate) {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 0.85f; }
                };
            } else if (chest.getItem() == net.minecraft.init.Items.iron_chestplate) {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 1.0f; }
                };
            } else if (chest.getItem() == net.minecraft.init.Items.diamond_chestplate) {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 1.0f; }
                };
            } else {
                armor = new com.wildfire.api.IGenderArmor() {
                    @Override public float physicsResistance() { return 0.5f; }
                };
            }

            boolean physicsEnabled = isLocal ? settings.physicsEnabled : true;
            boolean uniboob = isLocal ? settings.breastsUniboob : false;

            if (!physicsEnabled) {
                phys[0].resetPhysics();
                if (!uniboob) phys[1].resetPhysics();
                return;
            }

            boolean dualPhysics = !uniboob;
            if (dualPhysics) {
                phys[0].update((EntityLivingBase) player, armor);
                phys[1].update((EntityLivingBase) player, armor);
            } else {
                phys[0].update((EntityLivingBase) player, armor);
                phys[1].syncFrom(phys[0]);
            }
        } catch (Throwable t) {

            System.err.println("[WFG] onPlayerTick error: " + t.getMessage());
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {}

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {}

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        try {
            if (event.player != null) {
                GenderLayer.unregister(event.player.getUniqueID());
            }
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        try {

            WildfireSounds.cleanupOldEntries();
        } catch (Throwable ignored) {}
    }
}

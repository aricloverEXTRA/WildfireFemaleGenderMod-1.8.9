package com.wildfire.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class GuiUtils {

    public static void drawEntityOnScreenNoScissor(int x, int y, int scale, int mouseX, int mouseY, EntityLivingBase entity) {
        drawEntityOnScreenNoScissor(null, x, y, scale, mouseX, mouseY, entity);
    }

    public static void drawEntityOnScreenNoScissor(Object gui, int x, int y, int scale, int mouseX, int mouseY, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = entity.renderYawOffset;
        float f1 = entity.rotationYaw;
        float f2 = entity.rotationPitch;
        float f3 = entity.prevRotationYawHead;
        float f4 = entity.rotationYawHead;
        entity.renderYawOffset = 0.0F;
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.prevRotationYawHead = entity.rotationYawHead;
        entity.rotationYawHead = entity.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        entity.renderYawOffset = f;
        entity.rotationYaw = f1;
        entity.rotationPitch = f2;
        entity.prevRotationYawHead = f3;
        entity.rotationYawHead = f4;
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableColorMaterial();
    }
}
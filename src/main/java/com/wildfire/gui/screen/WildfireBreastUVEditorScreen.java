package com.wildfire.gui.screen;

import com.wildfire.gui.GuiUtils;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.ArmorTextureHelper;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.uvs.BreastTypes;
import com.wildfire.main.uvs.UVDirection;
import com.wildfire.main.uvs.UVLayout;
import com.wildfire.main.uvs.UVQuad;
import com.wildfire.main.uvs.UVStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class WildfireBreastUVEditorScreen extends GuiScreen {
    private static final int SIDEBAR_WIDTH = 180;
    private static final int TEXTURE_DRAW_SIZE = 196;
    private static final int TEXTURE_SOURCE_SIZE = 64;
    private static final float UV_WINDOW_SCALE = (float) TEXTURE_DRAW_SIZE / TEXTURE_SOURCE_SIZE;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFFDD55;
    private static final int COLOR_CYAN = 0xFF00FFFF;
    private static final int COLOR_SIDEBAR_BG = 0xCC000000;
    private static final int COLOR_SIDEBAR_DARK = 0x66000000;

    private final GuiScreen parent;
    private final UUID playerUuid;

    private UVDirection selectedDirection = null;
    private BreastTypes selectedBreastIndex = BreastTypes.LEFT;
    private UVLayout selectedUVs;

    private int uvWindowX;
    private int uvWindowY;
    private int winElementX;
    private int winElementY;

    private static final ResourceLocation ADD_ICON = new ResourceLocation("wildfire_gender:textures/gui/widgets/add.png");
    private static final ResourceLocation SUB_ICON = new ResourceLocation("wildfire_gender:textures/gui/widgets/subtract.png");

    public WildfireBreastUVEditorScreen(GuiScreen parent, UUID playerUuid) {
        this.parent = parent;
        this.playerUuid = playerUuid;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        try {
            this.selectedUVs = UVStorage.getLayout(this.playerUuid, this.selectedBreastIndex);
        } catch (Throwable t) {
            this.selectedUVs = new UVLayout(this.selectedBreastIndex);
        }
        if (this.selectedUVs == null) this.selectedUVs = new UVLayout(this.selectedBreastIndex);

        this.uvWindowX = 5;
        this.uvWindowY = this.height / 2 - TEXTURE_DRAW_SIZE / 2;
        this.winElementX = this.width - SIDEBAR_WIDTH + 7;
        this.winElementY = 32;

        int sidebarX = this.width - SIDEBAR_WIDTH;
        int sidebarY = 0;

        this.buttonList.add(new WildfireButton(0, sidebarX + 5, sidebarY + 5, SIDEBAR_WIDTH - 10, 20,
                StatCollector.translateToLocal("wildfire_gender.uv_editor.reset_defaults_all")));
        this.buttonList.add(new WildfireButton(5, sidebarX + 5, sidebarY + 28, SIDEBAR_WIDTH - 10, 15,
                StatCollector.translateToLocal("wildfire_gender.gui.back")));

        int btnW = SIDEBAR_WIDTH / 4 - 5;
        this.buttonList.add(new WildfireButton(1, winElementX, winElementY + 13, btnW, 15,
                StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.left_breast")));
        this.buttonList.add(new WildfireButton(2, winElementX + SIDEBAR_WIDTH / 4 - 3, winElementY + 13, btnW, 15,
                StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.right_breast")));
        this.buttonList.add(new WildfireButton(3, winElementX, winElementY + 44, btnW, 15,
                StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.left_breast_overlay")));
        this.buttonList.add(new WildfireButton(4, winElementX + SIDEBAR_WIDTH / 4 - 3, winElementY + 44, btnW, 15,
                StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.right_breast_overlay")));

        for (GuiButton b : this.buttonList) {
            if (b.id >= 1 && b.id <= 4) {
                BreastTypes t = BreastTypes.values()[b.id - 1];
                if (t == this.selectedBreastIndex) {
                    b.enabled = false;
                }
            }
        }

        if (this.selectedDirection != null) {
            int uvPositionWindowX = this.width - 130 + 5;
            int buttonArrayY = 52;

            for (int i = 0; i < 8; i++) {
                boolean isAdd = i % 2 == 1;
                int uvIndex = i / 2;
                int delta = isAdd ? 1 : -1;

                int xOffset = isAdd ? 106 : 92;
                int yOffset = (i / 2) * 14;

                this.buttonList.add(new WildfireButton(100 + i, uvPositionWindowX + xOffset, winElementY + buttonArrayY + yOffset, 12, 12, ""));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int sidebarX = this.width - SIDEBAR_WIDTH;
        drawRect(sidebarX, 0, this.width, this.height, COLOR_SIDEBAR_BG);
        drawRect(sidebarX + 5, 30, sidebarX + SIDEBAR_WIDTH / 2 - 5, 93, COLOR_SIDEBAR_DARK);
        drawRect(sidebarX + SIDEBAR_WIDTH / 2, 30, this.width - 5, 128, COLOR_SIDEBAR_DARK);

        drawRect(this.uvWindowX - 2, this.uvWindowY - 2, this.uvWindowX + TEXTURE_DRAW_SIZE + 2, this.uvWindowY + TEXTURE_DRAW_SIZE + 2, COLOR_SIDEBAR_BG);
        drawRect(this.uvWindowX, this.uvWindowY, this.uvWindowX + TEXTURE_DRAW_SIZE, this.uvWindowY + TEXTURE_DRAW_SIZE, COLOR_WHITE);

        ResourceLocation texture = null;
        try {
            if (this.mc.thePlayer != null) texture = this.mc.thePlayer.getLocationSkin();
            if (this.selectedBreastIndex.name().contains("OVERLAY")) {
                ResourceLocation armor = ArmorTextureHelper.getArmorTextureForPlayerUUID(this.playerUuid, true);
                if (armor == null) armor = ArmorTextureHelper.getArmorTextureForPlayerUUID(this.playerUuid, false);
                if (armor != null) texture = armor;
            }
        } catch (Throwable ignored) {}
        if (texture != null) {
            try {
                this.mc.getTextureManager().bindTexture(texture);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                drawScaledCustomSizeModalRect(this.uvWindowX, this.uvWindowY, 0, 0, TEXTURE_SOURCE_SIZE, TEXTURE_SOURCE_SIZE, TEXTURE_DRAW_SIZE, TEXTURE_DRAW_SIZE, TEXTURE_SOURCE_SIZE, TEXTURE_SOURCE_SIZE);
            } catch (Throwable ignored) {}
        } else {
            drawRect(this.uvWindowX, this.uvWindowY, this.uvWindowX + TEXTURE_DRAW_SIZE, this.uvWindowY + TEXTURE_DRAW_SIZE, 0xFF333333);
        }

        if (this.selectedUVs != null) {
            for (Map.Entry<UVDirection, UVQuad> entry : this.selectedUVs.getAllSides().entrySet()) {
                UVQuad q = entry.getValue();
                if (q == null) continue;

                drawFaceBorderWithTooltip(entry.getKey(), q, mouseX, mouseY, this.selectedDirection != entry.getKey());
            }
        }

        try {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            if (this.mc.thePlayer != null) {
                int modelScale = 120;
                if (this.width < 1920) modelScale = 60;
                else if (this.width >= 2560) modelScale = 200;
                int left = this.width / 2 - modelScale;
                int top = this.height / 2 - modelScale;
                int right = this.width / 2 + modelScale;
                int bottom = this.height / 2 + modelScale;
                GuiInventory.drawEntityOnScreen(left, top, right, bottom, modelScale,
                        (float) (this.width / 2 - mouseX), (float) (this.height / 2 - mouseY), this.mc.thePlayer);
            }
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
        } catch (Throwable t) {
            try { GlStateManager.popMatrix(); } catch (Throwable ignored) {}
        }

        drawRightEditorPanel(this.width - SIDEBAR_WIDTH + 5);
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderButtonIcons();

        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("wildfire_gender.uv_editor"), this.uvWindowX + TEXTURE_DRAW_SIZE / 2, this.uvWindowY - 15, COLOR_WHITE);

        if (this.selectedDirection != null && this.selectedUVs != null) {
            UVQuad q = this.selectedUVs.get(this.selectedDirection);
            if (q != null) {
                String info = String.format("%s: [%d,%d -> %d,%d] %s", this.selectedDirection.name(), q.x1(), q.y1(), q.x2(), q.y2(), this.selectedDirection.getDirectionText(this.selectedBreastIndex));
            }
        }
    }

    private void drawFaceBorderWithTooltip(UVDirection direction, UVQuad quad, int mouseX, int mouseY, boolean faded) {
        if (quad == null) return;

        int qx1 = Math.max(0, Math.min(63, quad.x1()));
        int qy1 = Math.max(0, Math.min(63, quad.y1()));
        int qx2 = Math.max(0, Math.min(63, quad.x2()));
        int qy2 = Math.max(0, Math.min(63, quad.y2()));
        if (qx2 < qx1 || qy2 < qy1) return;

        int rectX1 = this.uvWindowX + (int) (qx1 * UV_WINDOW_SCALE);
        int rectY1 = this.uvWindowY + (int) ((qy1 - 1) * UV_WINDOW_SCALE);
        int rectX2 = this.uvWindowX + (int) (qx2 * UV_WINDOW_SCALE);
        int rectY2 = this.uvWindowY + (int) ((qy2 - 1) * UV_WINDOW_SCALE);

        int borderColor = (this.selectedDirection == direction && !faded) ? COLOR_WHITE : direction.getFaceColor(faded);
        int borderThickness = 1;

        drawRect(rectX1, rectY1, rectX2, rectY1 + borderThickness, borderColor);
        drawRect(rectX1, rectY2 - borderThickness, rectX2, rectY2, borderColor);
        drawRect(rectX1, rectY1, rectX1 + borderThickness, rectY2, borderColor);
        drawRect(rectX2 - borderThickness, rectY1, rectX2, rectY2, borderColor);

        int fill = (borderColor & 0x00FFFFFF) | 0x22000000;
        drawRect(rectX1 + 1, rectY1 + 1, rectX2 - 1, rectY2 - 1, fill);

        String faceName = direction.getShortName();
        this.fontRendererObj.drawString(faceName, rectX1 + borderThickness, rectY1 + borderThickness, COLOR_WHITE);

        if (mouseX >= rectX1 && mouseX <= rectX2 && mouseY >= rectY1 && mouseY <= rectY2) {
            this.drawHoveringText(Arrays.asList(
                "\u00A7e" + direction.name() + " \u00A77(" + direction.getDirectionText(this.selectedBreastIndex) + ")",
                "\u00A7bX:" + quad.x1() + " Y:" + quad.y1() + " -> " + quad.x2() + "," + quad.y2(),
                "\u00A77Click to select"), mouseX, mouseY);
        }
    }

    private void drawRightEditorPanel(int x) {
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.layer_body"), x + SIDEBAR_WIDTH / 4, winElementY + 2, COLOR_WHITE);
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("wildfire_gender.uv_editor.selection.layer_jacket"), x + SIDEBAR_WIDTH / 4, winElementY + 32, COLOR_WHITE);

        int positionBoxX = this.width - SIDEBAR_WIDTH / 4;

        if (this.selectedDirection == null) {
            this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("wildfire_gender.uv_editor.no_face_selected"), positionBoxX, 60, 0xFF888888);
        } else {
            this.drawCenteredString(this.fontRendererObj, this.selectedDirection.getDirectionText(this.selectedBreastIndex), positionBoxX - 40, 37, COLOR_YELLOW);

            String[] labels = { "Move X", "Move Y", "Width", "Height" };
            for (int i = 0; i < labels.length; i++) {
                this.fontRendererObj.drawString(labels[i], positionBoxX - 35, 55 + (i * 14), COLOR_WHITE);
            }
            UVQuad q = this.selectedUVs.get(this.selectedDirection);
            if (q != null) {
                this.fontRendererObj.drawString(String.format("\u00A77[%d,%d %dx%d]", q.x1(), q.y1(), q.x2() - q.x1() + 1, q.y2() - q.y1() + 1), positionBoxX - 35, 55 + 4 * 14, COLOR_CYAN);
            }
            int increment = getIncrement();
            int incrementColor = increment == 1 ? COLOR_WHITE : (increment == 10 ? 0xFF00FFFF : 0xFF5555FF);
            this.fontRendererObj.drawString("\u00A77Shift: x10  Ctrl+Shift: x20", positionBoxX - 40, 109, incrementColor);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == null) return;
        if (button.id == 0) {
            UVStorage.unregister(this.playerUuid);
            this.selectedDirection = null;
            this.selectedUVs = new UVLayout(this.selectedBreastIndex);
            initGui();
        } else if (button.id == 5) {
            this.mc.displayGuiScreen(this.parent);
        } else if (button.id >= 1 && button.id <= 4) {

            if (this.selectedUVs != null && this.playerUuid != null) {
                try { UVStorage.saveLayout(this.playerUuid, this.selectedBreastIndex, this.selectedUVs); } catch (Throwable ignored) {}
            }
            this.selectedBreastIndex = BreastTypes.values()[button.id - 1];
            this.selectedDirection = null;
            try {
                this.selectedUVs = UVStorage.getLayout(this.playerUuid, this.selectedBreastIndex);
            } catch (Throwable t) {
                this.selectedUVs = new UVLayout(this.selectedBreastIndex);
            }
            initGui();
        } else if (button.id >= 100 && this.selectedDirection != null) {
            handleAdjustment(button.id, this.playerUuid);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.selectedUVs == null) return;

        for (Map.Entry<UVDirection, UVQuad> entry : this.selectedUVs.getAllSides().entrySet()) {
            UVQuad quad = entry.getValue();
            if (quad == null) continue;
            if (!(quad.x1() == 0 && quad.y1() == 0 && quad.x2() == 0 && quad.y2() == 0)) {
                int rectX1 = this.uvWindowX + (int) (quad.x1() * UV_WINDOW_SCALE);
                int rectY1 = this.uvWindowY + (int) ((quad.y1() - 1) * UV_WINDOW_SCALE);
                int rectX2 = this.uvWindowX + (int) (quad.x2() * UV_WINDOW_SCALE);
                int rectY2 = this.uvWindowY + (int) ((quad.y2() - 1) * UV_WINDOW_SCALE);

                if (mouseX >= rectX1 && mouseX <= rectX2 && mouseY >= rectY1 && mouseY <= rectY2) {
                    if (mouseButton == 0) {
                        if (this.selectedDirection != entry.getKey()) {
                            this.selectedDirection = entry.getKey();
                            initGui();
                        }
                    } else if (mouseButton == 1 && this.selectedDirection != null) {
                        this.selectedDirection = null;
                        initGui();
                    }
                    return;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (this.selectedUVs != null && this.playerUuid != null) {
                try { UVStorage.saveLayout(this.playerUuid, this.selectedBreastIndex, this.selectedUVs); } catch (Throwable ignored) {}
            }
            this.mc.displayGuiScreen(this.parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void handleAdjustment(int id, UUID uuid) {
        if (this.selectedUVs == null || this.selectedDirection == null) return;
        UVQuad quad = this.selectedUVs.get(this.selectedDirection);
        if (quad == null) return;

        int i = id - 100;
        boolean isAdd = i % 2 == 1;
        int uvIndex = i / 2;
        int delta = isAdd ? 1 : -1;
        int increment = getIncrement();
        int toAdd = delta * increment;

        try {
            switch (uvIndex) {
                case 0:
                    quad = quad.addX1(toAdd).addX2(toAdd);
                    break;
                case 1:
                    quad = quad.addY1(toAdd).addY2(toAdd);
                    break;
                case 2:
                    quad = quad.addX2(toAdd);
                    break;
                case 3:
                    quad = quad.addY2(toAdd);
                    break;
                default:
                    break;
            }
            this.selectedUVs.put(this.selectedDirection, quad);
            UVStorage.saveLayout(uuid, this.selectedBreastIndex, this.selectedUVs);

            try { UVStorage.generateBreastTextures(uuid); } catch (Throwable ignored) {}
        } catch (Throwable t) {
            System.err.println("[WFG] UV adjustment failed: " + t.getMessage());
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void renderButtonIcons() {
        for (GuiButton button : this.buttonList) {
            if (button.id >= 100) {
                try {
                    this.mc.getTextureManager().bindTexture((button.id % 2 != 0) ? ADD_ICON : SUB_ICON);
                    GlStateManager.enableBlend();
                    drawModalRectWithCustomSizedTexture(button.xPosition + 3, button.yPosition + 3, 0, 0, 6, 6, 6, 6);
                    GlStateManager.disableBlend();
                } catch (Throwable ignored) {}
            }
        }
    }

    private int getIncrement() {
        return isShiftKeyDown() ? (isCtrlKeyDown() ? 20 : 10) : 1;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        if (this.selectedUVs != null && this.playerUuid != null) {
            try { UVStorage.saveLayout(this.playerUuid, this.selectedBreastIndex, this.selectedUVs); } catch (Throwable ignored) {}
        }
        super.onGuiClosed();
    }
}
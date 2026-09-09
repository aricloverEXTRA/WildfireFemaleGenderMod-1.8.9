package com.wildfire.gui.screen;

import com.wildfire.gui.FakeGUIPlayer;
import com.wildfire.gui.GuiUtils;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.contributors.Contributor;
import com.wildfire.main.contributors.Contributors;
import com.wildfire.main.config.Configuration;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.UUID;

public class WildfireCreditsScreen extends GuiScreen {

    private static final int BOXES_PER_PAGE = 12;
    private static final int COLUMNS = 6;
    private static final int BOX_WIDTH = 52;
    private static final int BOX_HEIGHT = 68;
    private static final int H_SPACING = 8;
    private static final int V_SPACING = 8;
    private static final int PORTRAIT_PLAYER_DOWN_PX = 20;

    private static final ResourceLocation CREDIT_CONTAINER = new ResourceLocation("wildfire_gender:textures/gui/credits/credit_container.png");
    private static final ResourceLocation CREDIT_OUTLINE = new ResourceLocation("wildfire_gender:textures/gui/credits/credit_outline.png");
    private static final ResourceLocation BUTTON_CONTAINER = new ResourceLocation("wildfire_gender:textures/gui/credits/button_container.png");
    private static final ResourceLocation TAB_CONTAINER = new ResourceLocation("wildfire_gender:textures/gui/credits/tab_container.png");

    private static final ResourceLocation DARK_CREDIT_CONTAINER = new ResourceLocation("wildfire_gender:textures/darkmode/gui/credits/credit_container.png");
    private static final ResourceLocation DARK_CREDIT_OUTLINE = new ResourceLocation("wildfire_gender:textures/darkmode/gui/credits/credit_outline.png");
    private static final ResourceLocation DARK_BUTTON_CONTAINER = new ResourceLocation("wildfire_gender:textures/darkmode/gui/credits/button_container.png");
    private static final ResourceLocation DARK_TAB_CONTAINER = new ResourceLocation("wildfire_gender:textures/darkmode/gui/credits/tab_container.png");

    private enum Category { GENERAL, TRANSLATORS }
    private Category categoryTab = Category.GENERAL;
    private int creditsPage = 0;

    private WildfireButton btnBack, btnPrev, btnNext, btnGeneral, btnTranslators;
    private int navigationY;

    private FakeGUIPlayer[] generalPlayers = new FakeGUIPlayer[0];
    private FakeGUIPlayer[] translatorPlayers = new FakeGUIPlayer[0];

    public WildfireCreditsScreen() {
        Map<UUID, Contributor> map = Contributors.getContributors();

        List<FakeGUIPlayer> generals = new ArrayList<>();
        List<FakeGUIPlayer> translators = new ArrayList<>();

        for (Map.Entry<UUID, Contributor> entry : map.entrySet()) {
            Contributor c = entry.getValue();
            if (c == null || !Boolean.TRUE.equals(c.showInCredits())) continue;
            if (c.getRole() == Contributor.Role.TRANSLATOR) {
                translators.add(new FakeGUIPlayer(c.name(), entry.getKey()));
            } else {
                generals.add(new FakeGUIPlayer(c.name(), entry.getKey()));
            }
        }

        // Sort by role priority then by name
        Collections.sort(generals, (a, b) -> {
            Contributor ca = Contributors.getContributors().get(a.getUUID());
            Contributor cb = Contributors.getContributors().get(b.getUUID());
            if (ca == null || cb == null) return 0;
            int roleCmp = Integer.compare(ca.getRole().ordinal(), cb.getRole().ordinal());
            if (roleCmp != 0) return roleCmp;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        Collections.sort(translators, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        this.generalPlayers = generals.toArray(new FakeGUIPlayer[0]);
        this.translatorPlayers = translators.toArray(new FakeGUIPlayer[0]);
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        navigationY = this.height / 2 + 82;

        btnBack = new WildfireButton(0, this.width / 2 - 25, navigationY + 6, 50, 13, StatCollector.translateToLocal("wildfire_gender.details.go_back"));
        btnPrev = new WildfireButton(1, this.width / 2 - 89, navigationY + 6, 60, 13, StatCollector.translateToLocal("wildfire_gender.details.prev_page"));
        btnNext = new WildfireButton(2, this.width / 2 + 29, navigationY + 6, 60, 13, StatCollector.translateToLocal("wildfire_gender.details.next_page"));
        btnGeneral = new WildfireButton(3, this.width / 2 - 89, navigationY + 34, 87, 13, StatCollector.translateToLocal("wildfire_gender.credits.general"));
        btnTranslators = new WildfireButton(4, this.width / 2 + 2, navigationY + 34, 87, 13, StatCollector.translateToLocal("wildfire_gender.credits.translators"));

        updateButtonState();

        this.buttonList.add(btnBack);
        this.buttonList.add(btnPrev);
        this.buttonList.add(btnNext);
        this.buttonList.add(btnGeneral);
        this.buttonList.add(btnTranslators);
    }

    private void updateButtonState() {
        btnPrev.enabled = creditsPage > 0;
        btnNext.enabled = creditsPage < getTotalPages() - 1;
        btnGeneral.enabled = categoryTab != Category.GENERAL;
        btnTranslators.enabled = categoryTab != Category.TRANSLATORS;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!(button instanceof WildfireButton)) return;

        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new WardrobeBrowserScreen());
                return;
            case 1:
                if (creditsPage > 0) creditsPage--;
                break;
            case 2:
                if (creditsPage < getTotalPages() - 1) creditsPage++;
                break;
            case 3:
                categoryTab = Category.GENERAL;
                creditsPage = 0;
                break;
            case 4:
                categoryTab = Category.TRANSLATORS;
                creditsPage = 0;
                break;
        }

        updateButtonState();
    }

    private FakeGUIPlayer[] getActivePlayers() {
        return categoryTab == Category.TRANSLATORS ? translatorPlayers : generalPlayers;
    }

    private int getTotalPages() {
        FakeGUIPlayer[] arr = getActivePlayers();
        if (arr.length == 0) return 1;
        return (int)Math.ceil((double)arr.length / BOXES_PER_PAGE);
    }

    @Override
    public void updateScreen() {
        for (FakeGUIPlayer p : getActivePlayers()) {
            p.tick();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        boolean isDarkMode = mc.thePlayer != null && Configuration.getDarkMode(mc.thePlayer);

        ResourceLocation btnContainer = isDarkMode ? DARK_BUTTON_CONTAINER : BUTTON_CONTAINER;
        ResourceLocation tabContainer = isDarkMode ? DARK_TAB_CONTAINER : TAB_CONTAINER;
        ResourceLocation creditContainer = isDarkMode ? DARK_CREDIT_CONTAINER : CREDIT_CONTAINER;
        ResourceLocation creditOutline = isDarkMode ? DARK_CREDIT_OUTLINE : CREDIT_OUTLINE;

        GL11.glColor4f(1f, 1f, 1f, 1f);

        drawCenteredString(fontRendererObj,
                StatCollector.translateToLocal("wildfire_gender.credits.title"),
                width / 2, height / 2 - 100, 0xFFFFFF);

        drawCenteredString(fontRendererObj,
                StatCollector.translateToLocal("wildfire_gender.credits.description"),
                width / 2, height / 2 - 85, 0x888888);

        mc.getTextureManager().bindTexture(btnContainer);
        drawModalRectWithCustomSizedTexture(width / 2 - 95, navigationY, 0, 0, 190, 25, 190, 25);

        mc.getTextureManager().bindTexture(tabContainer);
        drawModalRectWithCustomSizedTexture(width / 2 - 95, navigationY + 28, 0, 0, 190, 25, 190, 25);

        FakeGUIPlayer[] active = getActivePlayers();
        int startIndex = creditsPage * BOXES_PER_PAGE;
        int endIndex = Math.min(startIndex + BOXES_PER_PAGE, active.length);

        int rows = (int) Math.ceil((double) Math.max(1, endIndex - startIndex) / COLUMNS);
        int totalHeight = rows * BOX_HEIGHT + (rows - 1) * V_SPACING;
        int startY = this.height / 2 - (totalHeight / 2) + 4;

        Map<UUID, Contributor> contribMap = Contributors.getContributors();

        List<String> hoverTooltip = null;

        for (int i = startIndex; i < endIndex; i++) {

            FakeGUIPlayer fp = active[i];
            int local = i - startIndex;
            int col = local % COLUMNS;
            int row = local / COLUMNS;

            int remainingInRow = Math.min(endIndex - startIndex - row * COLUMNS, COLUMNS);
            int rowWidth = remainingInRow * BOX_WIDTH + (remainingInRow - 1) * H_SPACING;
            int startX = (this.width / 2) - (rowWidth / 2);

            int cx = startX + col * (BOX_WIDTH + H_SPACING);
            int cy = startY + row * (BOX_HEIGHT + V_SPACING);

            mc.getTextureManager().bindTexture(creditContainer);
            drawModalRectWithCustomSizedTexture(cx, cy, 0, 0, BOX_WIDTH, BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);

            Contributor found = contribMap.get(fp.getUUID());
            int outlineColor = found != null ? found.getColor() : 0xFFFFFFFF;

            float r = ((outlineColor >> 16) & 0xFF) / 255.0F;
            float g = ((outlineColor >> 8) & 0xFF) / 255.0F;
            float b = (outlineColor & 0xFF) / 255.0F;

            GL11.glColor4f(r, g, b, 1.0F);
            mc.getTextureManager().bindTexture(creditOutline);
            drawModalRectWithCustomSizedTexture(cx + 3, cy + 3, 0, 0, 46, 53, 46, 53);
            GL11.glColor4f(1, 1, 1, 1);

            final int portraitX = cx + 3;
            final int portraitY = cy + 3;
            final int portraitW = 46;
            final int portraitH = 53;

            int sx = portraitX * this.mc.displayWidth / this.width;
            int sy = this.mc.displayHeight - (portraitY + portraitH) * this.mc.displayHeight / this.height;
            int sw = portraitW * this.mc.displayWidth / this.width;
            int sh = portraitH * this.mc.displayHeight / this.height;

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(sx, sy, sw, sh);

            int drawCenterX = portraitX + portraitW / 2;
            int drawCenterY = portraitY + (int)(portraitH * 0.80f) + PORTRAIT_PLAYER_DOWN_PX + 12;
            int modelScale = 32;

            int mouseOffsetX = mouseX - drawCenterX;
            int mouseOffsetY = mouseY - drawCenterY;

            AbstractClientPlayer entity = fp.getEntity();

            GuiUtils.drawEntityOnScreenNoScissor(
                    this,
                    drawCenterX,
                    drawCenterY,
                    modelScale,
                    mouseOffsetX,
                    mouseOffsetY,
                    entity
            );

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            int nameDrawX = cx + (BOX_WIDTH / 2);
            int nameDrawY = cy + 55;

            GL11.glPushMatrix();
            GL11.glTranslatef(nameDrawX, nameDrawY, 0f);
            GL11.glScalef(0.55f, 0.55f, 1.0f);
            GL11.glTranslatef(-nameDrawX, -nameDrawY, 0f);
            drawCenteredString(this.fontRendererObj, fp.getName(), nameDrawX, nameDrawY + 7, 0xFFFFFF);
            GL11.glPopMatrix();

            String name = fp.getName();
            int textWidthUnscaled = this.fontRendererObj.getStringWidth(name);
            int scaledTextWidth = (int)(textWidthUnscaled * 0.55f);
            int textLeft = nameDrawX - (scaledTextWidth / 2);
            int textRight = nameDrawX + (scaledTextWidth / 2);
            int textTop = nameDrawY + 7;
            int textHeight = (int)(9 * 0.55f);
            int textBottom = textTop + textHeight;

            if (mouseX >= textLeft && mouseX <= textRight && mouseY >= textTop && mouseY <= textBottom) {

                List<String> tooltip = new ArrayList<>();
                String roleText = StatCollector.translateToLocal("wildfire_gender.contributor.role.generic.short");
                int roleColor = 0xFFFFFF;

                if (found != null) {
                    roleColor = found.getColor();
                    try {
                        roleText = StatCollector.translateToLocal(found.getRole().shortNameKey());
                    } catch (Throwable ignored) {}

                    if (found.getDescription() != null && !found.getDescription().isEmpty()) {
                        tooltip.add(Contributor.getLegacyColorCode(roleColor) + found.getDescription());
                    }
                }

                tooltip.add(Contributor.getLegacyColorCode(roleColor) + roleText + " - " + fp.getName());
                hoverTooltip = tooltip;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hoverTooltip != null) {
            GL11.glPushMatrix();
            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            drawHoveringText(hoverTooltip, mouseX, mouseY);
            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

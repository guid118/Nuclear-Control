package shedar.mods.ic2.nuclearcontrol.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiToggleButton extends GuiButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nuclearcontrol:textures/gui/GUIAdvancedInfoPanelLines.png");

    private boolean isChecked;
    int dragOffsetY = 0; // Stores how much offset from mouse click

    public GuiToggleButton(int id, int x, int y, String buttonText, boolean initialState) {
        super(id, x, y, 140, 20, buttonText);
        this.isChecked = initialState;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void toggle() {
        isChecked = !isChecked;
    }


    public void setPosition(int y) {
        this.yPosition = y;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        mc.getTextureManager().bindTexture(TEXTURE);
        GL11.glColor4f(1, 1, 1, 1);

        boolean hovered = mouseX >= xPosition && mouseX < xPosition + width &&
                mouseY >= yPosition && mouseY < yPosition + height;

        int textureY = hovered ? 185 : 165;
        drawTexturedModalRect(xPosition-1, yPosition + 1, 0, textureY, 140, 20);

        int iconX = 188;
        int iconY = isChecked ? 0 : 16;
        drawTexturedModalRect(xPosition + 2, yPosition + 3, iconX, iconY, 16, 14);

        mc.fontRenderer.drawString(displayString, xPosition + 24, yPosition + 6, 0xFFFFFF);
    }
}

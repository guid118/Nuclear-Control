package shedar.mods.ic2.nuclearcontrol.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import ic2.core.IC2;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanel;

public class GuiPanelSlope extends GuiScreen {

    private static final String TEXTURE_FILE = "nuclearcontrol:textures/gui/GUISlope.png";
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(TEXTURE_FILE);

    protected int xSize = 171;
    protected int ySize = 94;
    protected int guiLeft;
    protected int guiTop;

    private GuiInfoPanel parentGui;
    private TileEntityAdvancedInfoPanel panel;

    public GuiPanelSlope(GuiInfoPanel parentGui, TileEntityAdvancedInfoPanel panel) {
        this.parentGui = parentGui;
        this.panel = panel;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);
        updateValue(x, y);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        updateValue(mouseX, mouseY);
    }

    private void updateValue(int mouseX, int mouseY) {
        mouseX -= guiLeft;
        mouseY -= guiTop;
        if (mouseY >= 23 && mouseY <= 89) {
            int amount = (87 - mouseY + 2) / 4;
            int offset = 0;
            if (mouseX >= 21 && mouseX <= 34) {
                offset = TileEntityAdvancedInfoPanel.OFFSET_THICKNESS;
                if (amount < 1) amount = 1;
            } else if (mouseX >= 79 && mouseX <= 92) {
                offset = TileEntityAdvancedInfoPanel.OFFSET_ROTATE_HOR;
                if (amount < 0) amount = 0;
            } else if (mouseX >= 137 && mouseX <= 150) {
                offset = TileEntityAdvancedInfoPanel.OFFSET_ROTATE_VERT;
                if (amount < 0) amount = 0;
            }
            (IC2.network.get()).initiateClientTileEntityEvent(panel, offset + amount);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(TEXTURE_LOCATION);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
        int textureHeight = 4 * (16 - panel.thickness);

        drawTexturedModalRect(left + 21, top + 25, 172, 0, 14, textureHeight);
        drawTexturedModalRect(
                left + 79,
                top + 25 + (panel.rotateHor < 0 ? 32 + panel.rotateHor * 4 / 7 : 32),
                186,
                0,
                14,
                Math.abs(panel.rotateHor * 4 / 7));
        drawTexturedModalRect(
                left + 137,
                top + 25 + (panel.rotateVert < 0 ? 32 + panel.rotateVert * 4 / 7 : 32),
                186,
                0,
                14,
                Math.abs(panel.rotateVert * 4 / 7));

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == 1) FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
        else super.keyTyped(par1, par2);
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        buttonList.clear();
    }
}

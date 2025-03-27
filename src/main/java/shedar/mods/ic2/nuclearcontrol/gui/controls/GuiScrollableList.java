package shedar.mods.ic2.nuclearcontrol.gui.controls;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiScrollableList extends GuiScreen {
    private static final int GUI_WIDTH = 173;
    private static final int GUI_HEIGHT = 153;
    private static final int PADDING_LEFT = 6;
    private static final int PADDING_TOP = 6;
    private static final int PADDING_BOTTOM = 6;
    private static final int LIST_WIDTH = 163;
    private static final int LIST_HEIGHT = 152;
    private static final int SCROLLBAR_PADDING_MIDDLE = 7;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_PADDING_SIDE = 9;
    private static final int BUTTON_WIDTH = 140;
    private static final int TOGGLE_BUTTON_WIDTH = 20;

    private int guiLeft = 0;
    private int guiTop = 0;
    private int guiRight = 0;
    private int guiBottom = 0;
    private int internalLeft = 0;
    private int internalTop = 0;
    private int listRight = 0;
    private int scrollbarLeft = 0;
    private int scrollbarRight = 0;
    private int internalBottom = 0;


    private static final ResourceLocation TEXTURE = new ResourceLocation("nuclearcontrol:textures/gui/GUIAdvancedInfoPanelLines.png");


    private List<GuiToggleButton> buttonListFull = new ArrayList<>();
    private int scrollOffset = 0;
    private int visibleButtons = 7;
    private int buttonHeight = 20;
    private int scrollSpeed = 1;
    private int scrollbarLocation = internalTop + 1;
    private int scrollbarOffset = 0;

    private GuiToggleButton draggedButton = null;
    private int originalIndex = -1;
    List<String> buttons;


    public GuiScrollableList(List<String> buttons) {
        this.buttons = buttons;
        //TODO REMOVE THIS DEBUG CODE
        for (int i = 3; i < 20; i++) {
            buttons.add( i + "");
        }
    }


    @Override
    public void initGui() {

        guiLeft = (width - GUI_WIDTH) / 2;
        guiTop = (height - GUI_HEIGHT) / 2;
        guiRight = (width + GUI_HEIGHT) / 2;
        guiBottom = (height + GUI_HEIGHT) / 2;
        internalLeft = guiLeft + PADDING_LEFT + 1;
        internalTop = guiTop + PADDING_TOP + 1;
        internalBottom = guiBottom - PADDING_BOTTOM;
        listRight = internalLeft + BUTTON_WIDTH;
        scrollbarLeft = guiRight - SCROLLBAR_PADDING_SIDE;
        scrollbarRight = scrollbarLeft + SCROLLBAR_WIDTH;


        this.buttonListFull.clear();
        this.buttonList.clear();
        for (int i = 0; i < buttons.size(); i++) {
            buttonListFull.add(new GuiToggleButton(i, internalLeft + 1, 0, buttons.get(i), false));
        }
        updateVisibleButtons();
    }

    private void updateVisibleButtons() {
        buttonList.clear();

        for (int i = scrollOffset; i < scrollOffset + visibleButtons && i < buttonListFull.size(); i++) {
            GuiToggleButton btn = buttonListFull.get(i);
            btn.yPosition = PADDING_TOP + (i - scrollOffset) * buttonHeight + guiTop;
            buttonList.add(btn);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        drawTexturedModalRect(scrollbarLeft + 1, scrollbarLocation, 176,0,12,32);


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        setScissor(internalLeft, PADDING_TOP + guiTop, LIST_WIDTH, LIST_HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (draggedButton != null) {
            draggedButton.drawButton(mc, mouseX, mouseY);
        }

    }

    private void setScissor(int x, int y, int width, int height) {
        int scaleFactor = 2;
        GL11.glScissor(x * scaleFactor, (mc.displayHeight - (y + height) * scaleFactor), width * scaleFactor, height * scaleFactor);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            // Determine bounds

            // check if click was within general GUI bounds
            if (mouseX > guiLeft && mouseX < guiRight) {
                //check if click was within CheckBox bounds
                if (mouseX < guiLeft + TOGGLE_BUTTON_WIDTH) {
                    for (Object o : buttonList) {
                        GuiToggleButton btn = (GuiToggleButton) o;
                        if (mouseY >= btn.yPosition && mouseY < btn.yPosition + buttonHeight) {
                            btn.toggle();
                        }
                    }
                    //check if click was within draggable bounds
                } else if (mouseX < listRight) {
                    for (Object o : buttonList) {
                        GuiToggleButton btn = (GuiToggleButton) o;
                        if (mouseY >= btn.yPosition && mouseY < btn.yPosition + buttonHeight) {
                            draggedButton = btn;
                            originalIndex = buttonListFull.indexOf(draggedButton);
                            btn.dragOffsetY = mouseY - btn.yPosition;
                            break;
                        }
                    }
                    // check if click was within scrollbar bounds.
                } else if (mouseX > scrollbarLeft && mouseX < scrollbarRight) {
                    //TODO do scrolling if dragged
                    scrollbarOffset = mouseY - internalTop;
                    System.out.println(scrollbarOffset);
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (draggedButton != null) {
            int adjustedY = mouseY - draggedButton.dragOffsetY;
            if (adjustedY < internalTop) {
                adjustedY = internalTop;
                scrollUp();
            } else if (adjustedY > internalBottom) {
                adjustedY = internalBottom;
                scrollDown();
            }
            draggedButton.setPosition(adjustedY);
        } else if (mouseX > scrollbarLeft && mouseX < scrollbarRight) {
            //TODO fix the scrollbar
            int adjustedY = mouseY - scrollbarOffset;
            scrollbarLocation = adjustedY;
            if (adjustedY < internalTop) {
                scrollbarLocation = internalTop;
            } else if (adjustedY > internalBottom) {
                scrollbarLocation = internalBottom - 32; // 32 is scrollbar height
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        super.mouseMovedOrUp(mouseX, mouseY, state);

        if (draggedButton != null) {
            int newIndex = (mouseY - guiTop + PADDING_TOP) / buttonHeight + scrollOffset - 1;
            newIndex = Math.max(0, Math.min(buttonListFull.size() - 1, newIndex));

            if (newIndex != originalIndex) {
                buttonListFull.remove(originalIndex);
                buttonListFull.add(newIndex, draggedButton);
            }

            draggedButton = null;
            updateVisibleButtons(); // Refresh button list after drag
            //TODO update the settingsList to reflect changes
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int scroll = Mouse.getDWheel();

        if (scroll > 0) scrollUp();
        else if (scroll < 0) scrollDown();
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset -= scrollSpeed;
            updateVisibleButtons();
        }
    }

    private void scrollDown() {
        if (scrollOffset + visibleButtons < buttonListFull.size()) {
            scrollOffset += scrollSpeed;
            updateVisibleButtons();
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

package shedar.mods.ic2.nuclearcontrol.api;

import java.util.UUID;

/**
 * Object of PanelSetting class defines one checkbox in the card's settings.
 * 
 * @author Shedar
 * @see IPanelDataSource#getSettingsList()
 */
public class PanelSetting {

    /**
     * Name of the option
     */
    public String title;

    /**
     * A bit mask of the display setting
     */
    public int displayBit;

    /**
     * Identifier of the card. Should be same as {@link IPanelDataSource#getCardType()}.
     */
    public UUID cardType;

    /**
     * @param title      Name of the option
     * @param displayBit A bit mask of the setting.
     * @param cardType   Identifier of the card. Should be same as {@link IPanelDataSource#getCardType()}.
     */
    public PanelSetting(String title, int displayBit, UUID cardType) {
        this.title = title;
        this.displayBit = displayBit;
        this.cardType = cardType;
    }
}

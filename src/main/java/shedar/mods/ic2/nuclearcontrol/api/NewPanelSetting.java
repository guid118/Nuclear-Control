package shedar.mods.ic2.nuclearcontrol.api;

import java.util.UUID;

public class NewPanelSetting extends PanelSetting {


    public int settingIndex;
    /**
     * @param title      Name of the option
     * @param displayBit The index of the setting it will be checked against
     * @param cardType   Identifier of the card. Should be same as {@link IPanelDataSource#getCardType()}.
     */
    public NewPanelSetting(String title, int settingIndex, UUID cardType) {
        super(title, (int) Math.pow(2, settingIndex), cardType);
        this.settingIndex = settingIndex;
    }

    /**
     * @param setting The original setting. use this if you need the index of a setting instead of the bitmask
     */
    public NewPanelSetting(PanelSetting setting) {
        super(setting.title, setting.displayBit, setting.cardType);
        this.settingIndex = (int)(Math.log(setting.displayBit) / Math.log(2));
    }
}

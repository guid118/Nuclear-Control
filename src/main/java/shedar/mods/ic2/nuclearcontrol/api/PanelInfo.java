package shedar.mods.ic2.nuclearcontrol.api;

public class PanelInfo {
    private PanelSetting panelSetting;
    private PanelString panelString;

    // Constructor
    public PanelInfo(PanelSetting setting, PanelString string) {
        this.panelSetting = setting;
        this.panelString = string;
    }

    // Getters
    public PanelSetting getPanelSetting() {
        return panelSetting;
    }

    public PanelString getPanelString() {
        return panelString;
    }
}

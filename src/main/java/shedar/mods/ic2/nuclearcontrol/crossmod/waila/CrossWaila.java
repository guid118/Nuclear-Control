package shedar.mods.ic2.nuclearcontrol.crossmod.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityHowlerAlarm;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityThermo;

public class CrossWaila {

    public static void callbackRegister(IWailaRegistrar register) {
        register.registerBodyProvider(new ThermoProvider(), TileEntityThermo.class);
        register.registerBodyProvider(new InfoPanelProvider(), TileEntityInfoPanel.class);
        register.registerBodyProvider(new HowlerAlarmProvider(), TileEntityHowlerAlarm.class);
        register.registerBodyProvider(new EnergyCounterProvider(), TileEntityEnergyCounter.class);
        register.registerBodyProvider(new AverageCounterProvider(), TileEntityAverageCounter.class);
    }

}

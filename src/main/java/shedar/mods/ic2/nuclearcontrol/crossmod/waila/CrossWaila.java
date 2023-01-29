package shedar.mods.ic2.nuclearcontrol.crossmod.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import shedar.mods.ic2.nuclearcontrol.tileentities.*;

public class CrossWaila {

    public static void callbackRegister(IWailaRegistrar register) {
        register.registerBodyProvider(new ThermoProvider(), TileEntityThermo.class);
        register.registerBodyProvider(new InfoPanelProvider(), TileEntityInfoPanel.class);
        register.registerBodyProvider(new HowlerAlarmProvider(), TileEntityHowlerAlarm.class);
        register.registerBodyProvider(new EnergyCounterProvider(), TileEntityEnergyCounter.class);
        register.registerBodyProvider(new AverageCounterProvider(), TileEntityAverageCounter.class);
    }

}

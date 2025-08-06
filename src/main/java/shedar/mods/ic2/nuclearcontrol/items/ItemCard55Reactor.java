package shedar.mods.ic2.nuclearcontrol.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import ic2.api.item.IC2Items;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import shedar.mods.ic2.nuclearcontrol.IC2NuclearControl;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.api.NewPanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.crossmod.ic2.IC2Cross.ReactorInfo;
import shedar.mods.ic2.nuclearcontrol.utils.LangHelper;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

public class ItemCard55Reactor extends ItemCardEnergySensorLocation implements IRemoteSensor {

    public ItemCard55Reactor() {
        this.setTextureName("nuclearcontrol:cardReactor");
    }

    public static final int DISPLAY_ON = 1;
    public static final int DISPLAY_OUTPUTTank = 2;
    public static final int DISPLAY_INPUTTank = 3;
    public static final int DISPLAY_HeatUnits = 4;
    public static final int DISPLAY_CoreTemp = 5;
    public static final UUID CARD_TYPE1 = new UUID(0, 2);

    @Override
    public UUID getCardType() {
        return CARD_TYPE1;
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range) {
        ChunkCoordinates target = card.getTarget();
        if (target == null) return CardState.NO_TARGET;
        // int targetType = card.getInt("targetType");
        TileEntity check = panel.getWorldObj().getTileEntity(target.posX, target.posY, target.posZ);
        if (isReactorPart(check) || panel.getWorldObj().getBlock(target.posX, target.posY, target.posZ)
                == Block.getBlockFromItem(IC2Items.getItem("reactorvessel").getItem())) {
            IReactor reactor = this.getReactor(panel.getWorldObj(), target.posX, target.posY, target.posZ);
            if (reactor != null) {
                ReactorInfo info = IC2NuclearControl.instance.crossIc2.getReactorInfo((TileEntity) reactor);
                if (info == null) {
                    return CardState.INVALID_CARD;
                }
                card.setBoolean("Online", info.isOnline);
                card.setInt("outputTank", info.outTank);
                card.setInt("inputTank", info.inTank);
                card.setInt("HeatUnits", info.emitHeat);
                card.setInt("CoreTempurature", info.coreTemp);
                return CardState.OK;
            } else {
                return CardState.INVALID_CARD;
            }
        } else {
            return CardState.NO_TARGET;
        }
    }

    @Override
    public CardState update(World world, ICardWrapper card, int range) {
        ChunkCoordinates target = card.getTarget();
        if (target == null) return CardState.NO_TARGET;
        // int targetType = card.getInt("targetType");
        TileEntity check = world.getTileEntity(target.posX, target.posY, target.posZ);
        if (isReactorPart(check) || world.getBlock(target.posX, target.posY, target.posZ)
                == Block.getBlockFromItem(IC2Items.getItem("reactorvessel").getItem())) {
            IReactor reactor = this.getReactor(world, target.posX, target.posY, target.posZ);
            if (reactor != null) {
                ReactorInfo info = IC2NuclearControl.instance.crossIc2.getReactorInfo((TileEntity) reactor);
                if (info == null) {
                    return CardState.INVALID_CARD;
                }
                card.setBoolean("Online", info.isOnline);
                card.setInt("outputTank", info.outTank);
                card.setInt("inputTank", info.inTank);
                card.setInt("HeatUnits", info.emitHeat);
                card.setInt("CoreTempurature", info.coreTemp);
                return CardState.OK;
            } else {
                return CardState.INVALID_CARD;
            }
        } else {
            return CardState.NO_TARGET;
        }
    }

    /*
     * This is taken directly from IC2's code, because I couldn't find a better way to do it || All credits to IC2 for
     * this function
     */
    public static IReactor getReactor(World world, int xCoord, int yCoord, int zCoord) {
        for (int xoffset = -1; xoffset < 2; xoffset++) {
            for (int yoffset = -1; yoffset < 2; yoffset++) {
                for (int zoffset = -1; zoffset < 2; zoffset++) {
                    TileEntity te = world.getTileEntity(xCoord + xoffset, yCoord + yoffset, zCoord + zoffset);
                    if (te instanceof IReactor) {
                        return (IReactor) te;
                    } else if (te instanceof IReactorChamber) {
                        return ((IReactorChamber) te).getReactor();
                    }
                }
            }
        }
        return null;
    }

    private boolean isReactorPart(TileEntity par1) {
        return IC2NuclearControl.instance.crossIc2.isMultiReactorPart(par1);
    }

    @Override
    public List<PanelString> getStringData(DisplaySettingHelper displaySettings, ICardWrapper card,
            boolean showLabels) {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;

        double tOut = card.getInt("outputTank");
        double tIn = card.getInt("inputTank");
        double heatUnits = card.getInt("HeatUnits");
        double coreTemp = card.getInt("CoreTempurature");

        // Temperature
        if (displaySettings.getSetting(DISPLAY_HeatUnits)) {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanel55.Output", heatUnits, showLabels);
            result.add(line);
        }

        // Stored Energy
        if (displaySettings.getSetting(DISPLAY_CoreTemp)) {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanel55.Temp", coreTemp, showLabels);
            result.add(line);
        }

        // Energy Created Frequency
        if (displaySettings.getSetting(DISPLAY_INPUTTank)) {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanel55.tarkin", tIn, showLabels);
            result.add(line);
        }

        // Output Percentage
        if (displaySettings.getSetting(DISPLAY_OUTPUTTank)) {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanel55.tankout", tOut, showLabels);
            result.add(line);
        }

        // On or Off
        int txtColor = 0;
        String text;
        if (displaySettings.getSetting(DISPLAY_ON)) {
            boolean reactorPowered = card.getBoolean("Online");
            if (reactorPowered) {
                txtColor = 0x00ff00;
                text = LangHelper.translate("msg.nc.InfoPanelOn");
            } else {
                txtColor = 0xff0000;
                text = LangHelper.translate("msg.nc.InfoPanelOff");
            }
            if (!result.isEmpty()) {
                PanelString firstLine = result.get(0);
                firstLine.textRight = text;
                firstLine.colorRight = txtColor;
            } else {
                line = new PanelString();
                line.textLeft = text;
                line.colorLeft = txtColor;
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList() {
        List<PanelSetting> result = new ArrayList<PanelSetting>(5);
        result.add(new NewPanelSetting(LangHelper.translate("msg.nc.cbInfoPanelOnOff"), DISPLAY_ON, CARD_TYPE));
        result.add(
                new NewPanelSetting(
                        LangHelper.translate("msg.nc.InfoPanel55.BufferOut"),
                        DISPLAY_OUTPUTTank,
                        CARD_TYPE));
        result.add(
                new NewPanelSetting(LangHelper.translate("msg.nc.InfoPanel55.BufferIn"), DISPLAY_INPUTTank, CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("msg.nc.InfoPanel55.Out"), DISPLAY_HeatUnits, CARD_TYPE));
        result.add(
                new NewPanelSetting(
                        LangHelper.translate("msg.nc.InfoPanelRF.TempPROPER"),
                        DISPLAY_CoreTemp,
                        CARD_TYPE));
        return result;
    }
}

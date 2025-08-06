package shedar.mods.ic2.nuclearcontrol.crossmod.appeng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import appeng.tile.crafting.TileCraftingMonitorTile;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.NewPanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.items.ItemCardEnergySensorLocation;
import shedar.mods.ic2.nuclearcontrol.utils.LangHelper;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

public class ItemCardAppeng extends ItemCardEnergySensorLocation {

    public ItemCardAppeng() {
        this.setTextureName("nuclearcontrol:cardAEMonitor");
        this.setUnlocalizedName("AppengCard");
    }

    public static final int DISPLAY_BYTES = 1;
    public static final int DISPLAY_ITEMS = 2;
    public static final int DISPLAY_CRAFTER = 3;
    public static final int DISPLAY_CRAFTSTACK = 4;
    // public static final int DISPLAY_TEMP = 16;
    public static final UUID CARD_TYPE1 = new UUID(0, 2);

    @Override
    public UUID getCardType() {
        return CARD_TYPE1;
    }

    @Override
    public CardState update(World world, ICardWrapper card, int range) {
        ChunkCoordinates target = card.getTarget();
        if (target == null) return CardState.NO_TARGET;
        int targetType = card.getInt("targetType");
        if (targetType == 1) {
            TileEntity check = world.getTileEntity(target.posX, target.posY, target.posZ);
            if (check instanceof TileEntityNetworkLink) {
                TileEntityNetworkLink tileNetworkLink = (TileEntityNetworkLink) check;
                card.setInt("ByteTotal", tileNetworkLink.getTOTALBYTES());
                card.setInt("UsedBytes", tileNetworkLink.getUSEDBYTES());
                card.setInt("ItemsTotal", tileNetworkLink.getITEMTYPETOTAL());
                card.setInt("UsedItems", tileNetworkLink.getUSEDITEMTYPE());
                return CardState.OK;
            } else {
                return CardState.NO_TARGET;
            }
        } else if (targetType == 2) {
            TileEntity check = world.getTileEntity(target.posX, target.posY, target.posZ);
            if (check instanceof TileCraftingMonitorTile) {
                TileCraftingMonitorTile monitorTile = (TileCraftingMonitorTile) check;
                Item crafter;
                int size;
                if (monitorTile.getJobProgress() != null) {
                    crafter = monitorTile.getJobProgress().getItemStack().getItem();
                    size = (int) monitorTile.getJobProgress().getStackSize();
                } else {
                    crafter = CrossAppeng.cardAppeng;
                    size = 0;
                }
                card.setInt("ITEMSTACK", Item.getIdFromItem(crafter));
                card.setInt("STACKSIZE", size);
                return CardState.OK;
            }
        } else {
            return CardState.NO_TARGET;
        }
        return CardState.NO_TARGET;
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range) {
        ChunkCoordinates target = card.getTarget();
        if (target == null) return CardState.NO_TARGET;
        int targetType = card.getInt("targetType");
        if (targetType == 1) {
            TileEntity check = panel.getWorldObj().getTileEntity(target.posX, target.posY, target.posZ);
            if (check instanceof TileEntityNetworkLink) {
                TileEntityNetworkLink tileNetworkLink = (TileEntityNetworkLink) check;
                card.setInt("ByteTotal", tileNetworkLink.getTOTALBYTES());
                card.setInt("UsedBytes", tileNetworkLink.getUSEDBYTES());
                card.setInt("ItemsTotal", tileNetworkLink.getITEMTYPETOTAL());
                card.setInt("UsedItems", tileNetworkLink.getUSEDITEMTYPE());
                return CardState.OK;
            } else {
                return CardState.NO_TARGET;
            }
        } else if (targetType == 2) {
            TileEntity check = panel.getWorldObj().getTileEntity(target.posX, target.posY, target.posZ);
            if (check instanceof TileCraftingMonitorTile) {
                TileCraftingMonitorTile monitorTile = (TileCraftingMonitorTile) check;
                Item crafter;
                int size;
                if (monitorTile.getJobProgress() != null) {
                    crafter = monitorTile.getJobProgress().getItemStack().getItem();
                    size = (int) monitorTile.getJobProgress().getStackSize();
                } else {
                    crafter = CrossAppeng.cardAppeng;
                    size = 0;
                }
                card.setInt("ITEMSTACK", Item.getIdFromItem(crafter));
                card.setInt("STACKSIZE", size);
                return CardState.OK;
            }
        } else {
            return CardState.NO_TARGET;
        }
        return CardState.NO_TARGET;
    }

    @Override
    public List<PanelString> getStringData(DisplaySettingHelper displaySettings, ICardWrapper card,
            boolean showLabels) {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        int TYPE = card.getInt("targetType");

        if (TYPE == 1) {
            int byteTotal = card.getInt("ByteTotal");
            int usedBytes = card.getInt("UsedBytes");
            int items = card.getInt("ItemsTotal");
            int itemsUsed = card.getInt("UsedItems");

            // Total Bytes
            if (displaySettings.getSetting(DISPLAY_BYTES)) {
                line = new PanelString();
                line.textRight = String.format(
                        StatCollector.translateToLocal("msg.nc.InfoPanelAE.DisplayBytes"),
                        usedBytes,
                        byteTotal);
                result.add(line);
            }

            // Used Items
            if (displaySettings.getSetting(DISPLAY_ITEMS)) {
                line = new PanelString();
                line.textRight = String
                        .format(StatCollector.translateToLocal("msg.nc.InfoPanelAE.DisplayItem"), itemsUsed, items);
                result.add(line);
            }
        } else if (TYPE == 2) {
            int stackSize = card.getInt("STACKSIZE");
            Item item = Item.getItemById(card.getInt("ITEMSTACK"));
            String localName = "item.null.name";
            try {
                localName = StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
            } catch (NullPointerException e) {}
            if (localName == "item.null.name" || localName.equals("Applied Energistics Card")) {
                localName = StatCollector.translateToLocal("msg.null.craft");
            }

            // Crafting item
            if (displaySettings.getSetting(DISPLAY_CRAFTER)) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelAE.CraftItemMake", localName, showLabels);
                result.add(line);
            }

            // Crafting Stacks
            if (displaySettings.getSetting(DISPLAY_CRAFTSTACK)) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelAE.CraftAMT", stackSize, showLabels);
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList() {
        List<PanelSetting> result = new ArrayList<PanelSetting>(4);
        result.add(new NewPanelSetting(LangHelper.translate("1"), DISPLAY_BYTES, CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("2"), DISPLAY_ITEMS, CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("3"), DISPLAY_CRAFTER, CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("4"), DISPLAY_CRAFTSTACK, CARD_TYPE));
        return result;
    }
}

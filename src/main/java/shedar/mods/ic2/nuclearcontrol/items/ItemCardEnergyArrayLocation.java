package shedar.mods.ic2.nuclearcontrol.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.NewPanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.crossmod.EnergyStorageData;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.EnergyStorageHelper;
import shedar.mods.ic2.nuclearcontrol.utils.LangHelper;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

public class ItemCardEnergyArrayLocation extends ItemCardBase {

    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 3;
    public static final int DISPLAY_EACH = 4;
    public static final int DISPLAY_TOTAL = 5;
    public static final int DISPLAY_PERCENTAGE = 6;

    private static final int STATUS_NOT_FOUND = Integer.MIN_VALUE;
    private static final int STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;

    public static final UUID CARD_TYPE = new UUID(0, 3);

    public ItemCardEnergyArrayLocation() {
        super("cardEnergyArray");
    }

    private int[] getCoordinates(ICardWrapper card, int cardNumber) {
        int cardCount = card.getInt("cardCount");
        if (cardNumber >= cardCount) return null;
        int[] coordinates = new int[] { card.getInt(String.format("_%dx", cardNumber)),
                card.getInt(String.format("_%dy", cardNumber)), card.getInt(String.format("_%dz", cardNumber)) };
        return coordinates;
    }

    public static int getCardCount(ICardWrapper card) {
        return card.getInt("cardCount");
    }

    public static void initArray(CardWrapperImpl card, Vector<ItemStack> cards) {
        int cardCount = getCardCount(card);
        for (ItemStack subCard : cards) {
            CardWrapperImpl wrapper = new CardWrapperImpl(subCard, -1);
            ChunkCoordinates target = wrapper.getTarget();
            if (target == null) continue;
            card.setInt(String.format("_%dx", cardCount), target.posX);
            card.setInt(String.format("_%dy", cardCount), target.posY);
            card.setInt(String.format("_%dz", cardCount), target.posZ);
            card.setInt(String.format("_%dtargetType", cardCount), wrapper.getInt("targetType"));
            cardCount++;
        }
        card.setInt("cardCount", cardCount);
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range) {
        int cardCount = getCardCount(card);
        double totalEnergy = 0.0;
        if (cardCount == 0) {
            return CardState.INVALID_CARD;
        } else {
            boolean foundAny = false;
            boolean outOfRange = false;
            for (int i = 0; i < cardCount; i++) {
                int[] coordinates = getCoordinates(card, i);
                int dx = coordinates[0] - panel.xCoord;
                int dy = coordinates[1] - panel.yCoord;
                int dz = coordinates[2] - panel.zCoord;
                if (Math.abs(dx) <= range && Math.abs(dy) <= range && Math.abs(dz) <= range) {
                    EnergyStorageData storage = EnergyStorageHelper.getStorageAt(
                            panel.getWorldObj(),
                            coordinates[0],
                            coordinates[1],
                            coordinates[2],
                            card.getInt(String.format("_%dtargetType", i)));
                    if (storage != null) {
                        totalEnergy += storage.stored;
                        card.setInt(String.format("_%denergy", i), (int) storage.stored);
                        card.setInt(String.format("_%dmaxStorage", i), (int) storage.capacity);
                        foundAny = true;
                    } else {
                        card.setInt(String.format("_%denergy", i), STATUS_NOT_FOUND);
                    }
                } else {
                    card.setInt(String.format("_%denergy", i), STATUS_OUT_OF_RANGE);
                    outOfRange = true;
                }
            }
            card.setDouble("energyL", totalEnergy);
            if (!foundAny) {
                if (outOfRange) return CardState.OUT_OF_RANGE;
                else return CardState.NO_TARGET;
            }
            return CardState.OK;
        }
    }

    @Override
    public CardState update(World world, ICardWrapper card, int range) {
        return CardState.CUSTOM_ERROR;
    }

    @Override
    public UUID getCardType() {
        return CARD_TYPE;
    }

    @Override
    public List<PanelString> getStringData(DisplaySettingHelper displaySettings, ICardWrapper card,
            boolean showLabels) {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        double totalEnergy = 0;
        double totalStorage = 0;
        boolean showEach = displaySettings.getSetting(DISPLAY_EACH);
        boolean showSummary = displaySettings.getSetting(DISPLAY_TOTAL);
        boolean showEnergy = displaySettings.getSetting(DISPLAY_ENERGY);
        boolean showFree = displaySettings.getSetting(DISPLAY_FREE);
        boolean showStorage = displaySettings.getSetting(DISPLAY_STORAGE);
        boolean showPercentage = displaySettings.getSetting(DISPLAY_PERCENTAGE);
        int cardCount = getCardCount(card);
        for (int i = 0; i < cardCount; i++) {
            int energy = card.getInt(String.format("_%denergy", i));
            int storage = card.getInt(String.format("_%dmaxStorage", i));
            boolean isOutOfRange = energy == STATUS_OUT_OF_RANGE;
            boolean isNotFound = energy == STATUS_NOT_FOUND;
            if (showSummary && !isOutOfRange && !isNotFound) {
                totalEnergy += energy;
                totalStorage += storage;
            }

            if (showEach) {
                if (isOutOfRange) {
                    line = new PanelString();
                    line.textLeft = StringUtils.getFormattedKey("msg.nc.InfoPanelOutOfRangeN", i + 1);
                    result.add(line);
                } else if (isNotFound) {
                    line = new PanelString();
                    line.textLeft = StringUtils.getFormattedKey("msg.nc.InfoPanelNotFoundN", i + 1);
                    result.add(line);
                } else {
                    if (showEnergy) {
                        line = new PanelString();
                        if (showLabels) line.textLeft = StringUtils.getFormattedKey(
                                "msg.nc.InfoPanelEnergyN",
                                i + 1,
                                StringUtils.getFormatted("", energy, false));
                        else line.textLeft = StringUtils.getFormatted("", energy, false);
                        result.add(line);
                    }
                    if (showFree) {
                        line = new PanelString();
                        if (showLabels) line.textLeft = StringUtils.getFormattedKey(
                                "msg.nc.InfoPanelEnergyFreeN",
                                i + 1,
                                StringUtils.getFormatted("", storage - energy, false));
                        else line.textLeft = StringUtils.getFormatted("", storage - energy, false);

                        result.add(line);
                    }
                    if (showStorage) {
                        line = new PanelString();
                        if (showLabels) line.textLeft = StringUtils.getFormattedKey(
                                "msg.nc.InfoPanelEnergyStorageN",
                                i + 1,
                                StringUtils.getFormatted("", storage, false));
                        else line.textLeft = StringUtils.getFormatted("", storage, false);
                        result.add(line);
                    }
                    if (showPercentage) {
                        line = new PanelString();
                        if (showLabels) line.textLeft = StringUtils.getFormattedKey(
                                "msg.nc.InfoPanelEnergyPercentageN",
                                i + 1,
                                StringUtils.getFormatted(
                                        "",
                                        storage == 0 ? 100 : (int) (((double) energy / storage) * 100D),
                                        false));
                        else line.textLeft = StringUtils.getFormatted(
                                "",
                                storage == 0 ? 100 : (int) (((double) energy / storage) * 100D),
                                false);
                        result.add(line);
                    }
                }
            }
        }
        if (showSummary) {
            if (showEnergy) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergy", totalEnergy, showLabels);
                result.add(line);
            }
            if (showFree) {
                line = new PanelString();
                line.textLeft = StringUtils
                        .getFormatted("msg.nc.InfoPanelEnergyFree", totalStorage - totalEnergy, showLabels);
                result.add(line);
            }
            if (showStorage) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyStorage", totalStorage, showLabels);
                result.add(line);
            }
            if (showPercentage) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted(
                        "msg.nc.InfoPanelEnergyPercentage",
                        totalStorage == 0 ? 100 : ((totalEnergy / totalStorage) * 100),
                        showLabels);
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList() {
        List<PanelSetting> result = new ArrayList<>(6);
        result.add(
                new NewPanelSetting(
                        LangHelper.translate("msg.nc.cbInfoPanelEnergyCurrent"),
                        DISPLAY_ENERGY,
                        CARD_TYPE));
        result.add(
                new NewPanelSetting(
                        LangHelper.translate("msg.nc.cbInfoPanelEnergyStorage"),
                        DISPLAY_STORAGE,
                        CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("msg.nc.cbInfoPanelEnergyFree"), DISPLAY_FREE, CARD_TYPE));
        result.add(
                new NewPanelSetting(
                        LangHelper.translate("msg.nc.cbInfoPanelEnergyPercentage"),
                        DISPLAY_PERCENTAGE,
                        CARD_TYPE));
        result.add(new NewPanelSetting(LangHelper.translate("msg.nc.cbInfoPanelEnergyEach"), DISPLAY_EACH, CARD_TYPE));
        result.add(
                new NewPanelSetting(LangHelper.translate("msg.nc.cbInfoPanelEnergyTotal"), DISPLAY_TOTAL, CARD_TYPE));
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean advanced) {
        CardWrapperImpl card = new CardWrapperImpl(itemStack, -1);
        int cardCount = getCardCount(card);
        if (cardCount > 0) {
            String title = card.getTitle();
            if (title != null && !title.isEmpty()) {
                info.add(title);
            }
            String hint = String.format(LangHelper.translate("msg.nc.EnergyCardQuantity"), cardCount);
            info.add(hint);
        }
    }
}

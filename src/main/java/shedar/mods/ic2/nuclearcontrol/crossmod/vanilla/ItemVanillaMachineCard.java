package shedar.mods.ic2.nuclearcontrol.crossmod.vanilla;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.NewPanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.items.ItemCardBase;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

public class ItemVanillaMachineCard extends ItemCardBase {

    public static final int DISPLAY_BREWING = 1;
    public static final int DISPLAY_TIME = 2;
    public static final int DISPLAY_SLOT_1 = 3;
    public static final int DISPLAY_SLOT_2 = 4;
    public static final int DISPLAY_SLOT_3 = 5;

    private static final String BREW_STAND = "brewStand";
    private static final String FURNACE = "furnace";

    public ItemVanillaMachineCard() {
        super("cardVanilla");
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range) {
        return update(panel.getWorldObj(), card, range);
    }

    @Override
    public CardState update(World world, ICardWrapper card, int range) {
        ChunkCoordinates target = card.getTarget();
        if (target == null) return CardState.NO_TARGET;
        TileEntity tile = world.getTileEntity(target.posX, target.posY, target.posZ);

        if (tile instanceof TileEntityBrewingStand) {
            TileEntityBrewingStand brewingStand = (TileEntityBrewingStand) tile;
            card.setString("entity", BREW_STAND);
            card.setBoolean("brewing", brewingStand.getBrewTime() > 0);
            card.setInt("brewTime", brewingStand.getBrewTime());
            NBTTagCompound tag = new NBTTagCompound();
            if (brewingStand.getBrewTime() > 0) {
                if (brewingStand.getStackInSlot(3) != null && brewingStand.getStackInSlot(0) != null)
                    tag.setString("Slot1", potionFN(brewingStand.getStackInSlot(3), brewingStand.getStackInSlot(0)));
                if (brewingStand.getStackInSlot(3) != null && brewingStand.getStackInSlot(1) != null)
                    tag.setString("Slot2", potionFN(brewingStand.getStackInSlot(3), brewingStand.getStackInSlot(1)));
                if (brewingStand.getStackInSlot(3) != null && brewingStand.getStackInSlot(2) != null)
                    tag.setString("Slot3", potionFN(brewingStand.getStackInSlot(3), brewingStand.getStackInSlot(2)));
            }
            card.setTag("BrewInfo", tag);
            return CardState.OK;
            // brewingStand.getStackInSlot(3).getItem().isPotionIngredient(brewingStand.getStackInSlot(3));
        } else if (tile instanceof TileEntityFurnace) {
            TileEntityFurnace furnace = (TileEntityFurnace) tile;
            card.setString("entity", "furnace");
            card.setBoolean("burning", furnace.isBurning());
            card.setInt("burnTime", furnace.furnaceBurnTime);
            NBTTagCompound tag = new NBTTagCompound();
            if (furnace.getStackInSlot(0) != null) {
                tag.setString("Cooking", furnace.getStackInSlot(0).getDisplayName());
                tag.setInteger("Csize", furnace.getStackInSlot(0).stackSize);
            }
            if (furnace.getStackInSlot(1) != null) {
                tag.setString("Fuel", furnace.getStackInSlot(1).getDisplayName());
                tag.setInteger("Fsize", furnace.getStackInSlot(1).stackSize);
            }
            if (furnace.getStackInSlot(2) != null) {
                tag.setString("Output", furnace.getStackInSlot(2).getDisplayName());
                tag.setInteger("Osize", furnace.getStackInSlot(2).stackSize);
            }
            card.setTag("Info", tag);
            return CardState.OK;
        }
        return CardState.INVALID_CARD;
    }

    private String potionFN(ItemStack stack, ItemStack stack1) {
        int k = this.getSecondPotionList(stack1.getItemDamage(), stack);
        List list1 = Items.potionitem.getEffects(k);
        if (list1 != null) {
            return StatCollector.translateToLocal(((PotionEffect) list1.get(0)).getEffectName());
        } else {
            if (stack.getItem().equals(Items.redstone)) {
                return StatCollector.translateToLocal("potion.prefix.mundane");
            } else if (stack.getItem().equals(Items.glowstone_dust)) {
                return StatCollector.translateToLocal("potion.prefix.thick");
            } else {
                return StatCollector.translateToLocal("potion.prefix.awkward");
            }
        }

    }

    private int getSecondPotionList(int itemDamage, ItemStack stack) {
        return stack == null ? itemDamage
                : (stack.getItem().isPotionIngredient(stack)
                        ? PotionHelper.applyIngredient(itemDamage, stack.getItem().getPotionEffect(stack))
                        : itemDamage);
    }

    @Override
    public UUID getCardType() {
        return new UUID(0, 2);
    }

    @Override
    public List<PanelString> getStringData(DisplaySettingHelper displaySettings, ICardWrapper card,
            boolean showLabels) {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;

        String machineType = card.getString("entity");

        if (BREW_STAND.equals(machineType)) {
            // ... (Brewing Stand logic as previously discussed and refined) ...
            Boolean isBrewing = card.getBoolean("brewing");
            int brewTime = card.getInt("brewTime");
            NBTTagCompound tag = card.getTag("BrewInfo");

            if (displaySettings.getSetting(DISPLAY_TIME)) {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.Vanilla.brewstand", brewTime, showLabels);
                result.add(line);
            }
            if (displaySettings.getSetting(DISPLAY_SLOT_1)) {
                String slot1pre = StatCollector.translateToLocal("msg.nc.None");
                if (tag != null && tag.hasKey("Slot1")) {
                    slot1pre = tag.getString("Slot1");
                }
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.Vanilla.potionType1", slot1pre, showLabels);
                result.add(line);
            }
            if (displaySettings.getSetting(DISPLAY_SLOT_2)) {
                String slot2pre = StatCollector.translateToLocal("msg.nc.None");
                if (tag != null && tag.hasKey("Slot2")) {
                    slot2pre = tag.getString("Slot2");
                }
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.Vanilla.potionType2", slot2pre, showLabels);
                result.add(line);
            }
            if (displaySettings.getSetting(DISPLAY_SLOT_3)) {
                String slot3pre = StatCollector.translateToLocal("msg.nc.None");
                if (tag != null && tag.hasKey("Slot3")) {
                    slot3pre = tag.getString("Slot3");
                }
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.Vanilla.potionType3", slot3pre, showLabels);
                result.add(line);
            }
            // Brewing stand status logic (applied after other lines are potentially added)
            if (displaySettings.getSetting(DISPLAY_BREWING)) {
                int txtColour;
                String text;
                if (isBrewing) {
                    txtColour = 0x00ff00; // Green
                    text = StatCollector.translateToLocal("msg.nc.Vanilla.brewing");
                } else {
                    txtColour = 0xff0000; // Red
                    text = StatCollector.translateToLocal("msg.nc.Vanilla.notBrewing");
                }

                if (result.size() > 0 && result.get(0).textRight == null) {
                    // Attach to the right of the first line if available
                    PanelString firstLine = result.get(0);
                    firstLine.textRight = text;
                    firstLine.colorRight = txtColour;
                } else {
                    // Otherwise, add as a new line
                    line = new PanelString();
                    line.textLeft = text;
                    line.colorLeft = txtColour;
                    result.add(line);
                }
            }

        } else if (FURNACE.equals(machineType)) {
            boolean isBurning = card.getBoolean("burning");
            int burnTime = card.getInt("burnTime");
            NBTTagCompound tagCompound = card.getTag("Info");

            boolean statusHandled = false;
            String statusText = null;
            int statusColour = 0;

            if (displaySettings.getSetting(DISPLAY_BREWING)) { // DISPLAY_BREWING is reused for furnace activity
                if (isBurning) {
                    statusColour = 0x00ff00; // Green
                    statusText = StatCollector.translateToLocal("msg.nc.InfoPanelOn");
                } else {
                    statusColour = 0xff0000; // Red
                    statusText = StatCollector.translateToLocal("msg.nc.InfoPanelOff");
                }
            }

            if (displaySettings.getSetting(DISPLAY_TIME)) {
                line = new PanelString();
                line.textLeft = String.format( // Using String.format as in your original for furnace time
                        StatCollector.translateToLocal("msg.nc.Vanilla.burnTime"),
                        burnTime);
                if (displaySettings.getSetting(DISPLAY_BREWING)) { // If status is also active
                    line.textRight = statusText;
                    line.colorRight = statusColour;
                    statusHandled = true;
                }
                result.add(line);
            }

            if (tagCompound != null) {
                if (displaySettings.getSetting(DISPLAY_SLOT_1)) {
                    String slot1pre = StatCollector.translateToLocal("msg.nc.None");
                    if (tagCompound.hasKey("Cooking")) {
                        slot1pre = tagCompound.getString("Cooking");
                    }
                    line = new PanelString();
                    line.textLeft = String.format(
                            StatCollector.translateToLocal("msg.nc.Vanilla.cooking"),
                            tagCompound.getInteger("Csize"),
                            slot1pre);
                    result.add(line);
                }
                if (displaySettings.getSetting(DISPLAY_SLOT_2)) {
                    String slot2pre = StatCollector.translateToLocal("msg.nc.None");
                    if (tagCompound.hasKey("Fuel")) {
                        slot2pre = tagCompound.getString("Fuel");
                    }
                    line = new PanelString();
                    line.textLeft = String.format(
                            StatCollector.translateToLocal("msg.nc.Vanilla.fuel"),
                            tagCompound.getInteger("Fsize"),
                            slot2pre);
                    result.add(line);
                }
                if (displaySettings.getSetting(DISPLAY_SLOT_3)) {
                    String slot3pre = StatCollector.translateToLocal("msg.nc.None");
                    if (tagCompound.hasKey("Output")) {
                        slot3pre = tagCompound.getString("Output");
                    }
                    line = new PanelString();
                    line.textLeft = String.format(
                            StatCollector.translateToLocal("msg.nc.Vanilla.output"),
                            tagCompound.getInteger("Osize"),
                            slot3pre);
                    result.add(line);
                }
            }

            // If status is active but wasn't attached to the time line
            if (displaySettings.getSetting(DISPLAY_BREWING) && !statusHandled) {
                if (!result.isEmpty() && result.get(0).textRight == null) {
                    // Attach to the right of the first existing line (could be a slot)
                    PanelString firstLine = result.get(0);
                    firstLine.textRight = statusText;
                    firstLine.colorRight = statusColour;
                } else {
                    // If no other lines or first line's right is occupied, add status as a new line
                    line = new PanelString();
                    line.textLeft = statusText;
                    line.colorLeft = statusColour;
                    result.add(line);
                }
            }
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList() {
        List<PanelSetting> result = new ArrayList<PanelSetting>();
        result.add(
                new NewPanelSetting(
                        StatCollector.translateToLocal("msg.nc.Vanilla.setting.activeTime"),
                        DISPLAY_TIME,
                        getCardType()));
        result.add(
                new NewPanelSetting(
                        StatCollector.translateToLocal("msg.nc.Vanilla.setting.slot1Contents"),
                        DISPLAY_SLOT_1,
                        getCardType()));
        result.add(
                new NewPanelSetting(
                        StatCollector.translateToLocal("msg.nc.Vanilla.setting.slot2Contents"),
                        DISPLAY_SLOT_2,
                        getCardType()));
        result.add(
                new NewPanelSetting(
                        StatCollector.translateToLocal("msg.nc.Vanilla.setting.slot3Contents"),
                        DISPLAY_SLOT_3,
                        getCardType()));
        result.add(
                new NewPanelSetting(
                        StatCollector.translateToLocal("msg.nc.Vanilla.setting.activityStatus"),
                        DISPLAY_BREWING,
                        getCardType()));
        return result;
    }
}

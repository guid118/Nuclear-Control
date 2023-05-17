package shedar.mods.ic2.nuclearcontrol.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shedar.mods.ic2.nuclearcontrol.IC2NuclearControl;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.utils.TextureResolver;

public class ItemPanelMemoryCard extends Item {

    public ItemPanelMemoryCard() {
        super();
        setMaxStackSize(1);
        setCreativeTab(IC2NuclearControl.tabIC2NC);
        setTextureName(TextureResolver.getItemTexture("panelMemoryCard"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean advanced) {
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt != null) {
            // Count saved settings for existing cards
            int cardsSaved = 0;
            for (int i = 0; i < 4; i++) {
                String key = "dSettings";
                if (i > 0) key += i;
                if (nbt.hasKey(key)) {
                    NBTTagList settings = nbt.getTagList(key, Constants.NBT.TAG_COMPOUND);
                    if (settings.tagCount() > 0) cardsSaved++;
                }
            }

            if (nbt.hasKey("rotateHor")) info.add("Contains panel metadata");
            if (cardsSaved > 0) info.add("Contains settings for " + cardsSaved + " card(s)");
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        boolean isServer = !world.isRemote;
        if (!isServer) return false;

        TileEntity panel = world.getTileEntity(x, y, z);
        if (panel instanceof TileEntityInfoPanel) {
            if (player.isSneaking()) {
                ((TileEntityInfoPanel) panel).saveDisplaySettingsToCard(itemStack);
                messagePlayer(player, "Panel settings saved");
            } else {
                NBTTagCompound panelNBT = itemStack.getTagCompound();
                if (panelNBT == null) return false;
                ((TileEntityInfoPanel) panel).readDisplaySettingsFromCard(itemStack);
                messagePlayer(player, "Panel settings applied");
            }
        } else {
            if (player.isSneaking()) {
                itemStack.setTagCompound(null);
                messagePlayer(player, "Settings cleared.");
            }
            return false;
        }

        return true;
    }

    protected void messagePlayer(EntityPlayer entityplayer, String text) {
        entityplayer.addChatComponentMessage(new ChatComponentText(text));
    }
}

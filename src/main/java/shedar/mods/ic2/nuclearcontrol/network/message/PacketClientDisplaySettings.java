package shedar.mods.ic2.nuclearcontrol.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerInfoPanel;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.utils.DisplaySettingHelper;

public class PacketClientDisplaySettings implements IMessage, IMessageHandler<PacketClientDisplaySettings, IMessage> {

    private int x;
    private int y;
    private int z;
    private byte slot;
    private DisplaySettingHelper settings = new DisplaySettingHelper();

    public PacketClientDisplaySettings() {}

    /**
     * @deprecated use {@link #PacketClientDisplaySettings(int, int, int, byte, DisplaySettingHelper)}
     */
    public PacketClientDisplaySettings(int x, int y, int z, byte slot, int settings) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slot = slot;
        this.settings = new DisplaySettingHelper(settings);
    }

    public PacketClientDisplaySettings(int x, int y, int z, byte slot, DisplaySettingHelper settings) {
        PacketClientDisplaySettings packet = new PacketClientDisplaySettings();
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.slot = slot;
        packet.settings = settings;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        slot = buf.readByte();
        settings = new DisplaySettingHelper(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(slot);
        settings.writeToByteBuffer(buf);
    }

    @Override
    public IMessage onMessage(PacketClientDisplaySettings message, MessageContext ctx) {
        /*
         * TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y,
         * message.z); if (tile instanceof TileEntityInfoPanel) ((TileEntityInfoPanel)
         * tile).setDisplaySettings(message.slot, message.settings);
         */
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        Container openContainer = player.openContainer;
        if (openContainer instanceof ContainerInfoPanel) {
            int x = message.x;
            int y = message.y;
            int z = message.z;
            TileEntityInfoPanel panel = ((ContainerInfoPanel) openContainer).panel;
            if (panel != null && panel.xCoord == x
                    && panel.yCoord == y
                    && panel.zCoord == z
                    && panel == player.worldObj.getTileEntity(x, y, z))
                panel.setDisplaySettings(message.slot, message.settings);
        }
        return null;
    }
}

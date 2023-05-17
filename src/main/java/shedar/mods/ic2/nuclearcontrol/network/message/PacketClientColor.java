package shedar.mods.ic2.nuclearcontrol.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerInfoPanel;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;

public class PacketClientColor implements IMessage, IMessageHandler<PacketClientColor, IMessage> {

    private int x;
    private int y;
    private int z;
    private int colors;

    public PacketClientColor() {}

    public PacketClientColor(int x, int y, int z, int colors) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colors = colors;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        colors = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(colors);
    }

    @Override
    public IMessage onMessage(PacketClientColor message, MessageContext ctx) {
        /*
         * TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y,
         * message.z); if (tile instanceof TileEntityInfoPanel) { int back = message.colors >> 4; int text =
         * message.colors & 0xf; ((TileEntityInfoPanel) tile).setColorBackground(back); ((TileEntityInfoPanel)
         * tile).setColorText(text); }
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
                    && panel == player.worldObj.getTileEntity(x, y, z)) {
                int back = message.colors >> 4;
                int text = message.colors & 0xf;
                panel.setColorBackground(back);
                panel.setColorText(text);
            }
        }
        return null;
    }
}

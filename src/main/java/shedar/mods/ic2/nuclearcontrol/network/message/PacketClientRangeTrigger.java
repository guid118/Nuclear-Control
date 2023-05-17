package shedar.mods.ic2.nuclearcontrol.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerRangeTrigger;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRangeTrigger;

public class PacketClientRangeTrigger implements IMessage, IMessageHandler<PacketClientRangeTrigger, IMessage> {

    private int x;
    private int y;
    private int z;
    private double value;
    private boolean isEnd;

    public PacketClientRangeTrigger() {}

    public PacketClientRangeTrigger(int x, int y, int z, double value, boolean isEnd) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
        this.isEnd = isEnd;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        value = buf.readDouble();
        isEnd = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeDouble(value);
        buf.writeBoolean(isEnd);
    }

    @Override
    public IMessage onMessage(PacketClientRangeTrigger message, MessageContext ctx) {
        /*
         * TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y,
         * message.z); if (tile instanceof TileEntityRangeTrigger) if (message.isEnd) ((TileEntityRangeTrigger)
         * tile).setLevelEnd(message.value); else ((TileEntityRangeTrigger) tile).setLevelStart(message.value);
         */
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        Container openContainer = player.openContainer;
        if (openContainer instanceof ContainerRangeTrigger) {
            int x = message.x;
            int y = message.y;
            int z = message.z;
            TileEntityRangeTrigger rangeTrigger = ((ContainerRangeTrigger) openContainer).trigger;
            if (rangeTrigger != null && rangeTrigger.xCoord == x
                    && rangeTrigger.yCoord == y
                    && rangeTrigger.zCoord == z
                    && rangeTrigger == player.worldObj.getTileEntity(x, y, z)) {
                if (message.isEnd) rangeTrigger.setLevelEnd(message.value);
                else rangeTrigger.setLevelStart(message.value);
            }
        }
        return null;
    }
}

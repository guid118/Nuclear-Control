package shedar.mods.ic2.nuclearcontrol.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanel;
import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;

public class PacketClientRequest implements IMessage, IMessageHandler<PacketClientRequest, IMessage> {

    private int x;
    private int y;
    private int z;

    public PacketClientRequest() {}

    public PacketClientRequest(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public IMessage onMessage(PacketClientRequest message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(message.x, message.y, message.z);

        NuclearNetworkHelper.sendDisplaySettingsToPlayer(message.x, message.y, message.z, player);

        if (te instanceof TileEntityAdvancedInfoPanel) {
            NuclearNetworkHelper.sendDataSorterSync((TileEntityAdvancedInfoPanel) te);
        }
        return null;
    }
}

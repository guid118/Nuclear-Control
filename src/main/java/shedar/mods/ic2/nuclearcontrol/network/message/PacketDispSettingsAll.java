package shedar.mods.ic2.nuclearcontrol.network.message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;

public class PacketDispSettingsAll implements IMessage, IMessageHandler<PacketDispSettingsAll, IMessage> {

    private int x;
    private int y;
    private int z;
    private Map<Byte, Map<UUID, DisplaySettingHelper>> settings;

    public PacketDispSettingsAll() {}

    public PacketDispSettingsAll(int x, int y, int z, Map<Byte, Map<UUID, DisplaySettingHelper>> settings) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.settings = settings;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        settings = new HashMap<>();
        byte count = buf.readByte();
        for (int i = 0; i < count; i++) {
            byte slot = buf.readByte();
            short dCount = buf.readShort();
            Map<UUID, DisplaySettingHelper> setting = new HashMap<>();
            for (int j = 0; j < dCount; j++) {
                long most = buf.readLong();
                long least = buf.readLong();
                setting.put(new UUID(most, least), new DisplaySettingHelper(buf));
            }
            settings.put(slot, setting);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(settings.size());
        for (Map.Entry<Byte, Map<UUID, DisplaySettingHelper>> slotData : settings.entrySet()) {
            buf.writeByte(slotData.getKey());
            buf.writeShort(slotData.getValue().size());
            for (Map.Entry<UUID, DisplaySettingHelper> item : slotData.getValue().entrySet()) {
                UUID key = item.getKey();
                if (key == null) {
                    continue;
                }
                buf.writeLong(key.getMostSignificantBits());
                buf.writeLong(key.getLeastSignificantBits());
                item.getValue().writeToByteBuffer(buf);
            }
        }
    }

    @Override
    public IMessage onMessage(PacketDispSettingsAll message, MessageContext ctx) {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
                .getTileEntity(message.x, message.y, message.z);
        if (tileEntity == null || !(tileEntity instanceof TileEntityInfoPanel)) {
            return null;
        }
        TileEntityInfoPanel panel = (TileEntityInfoPanel) tileEntity;
        for (Map.Entry<Byte, Map<UUID, DisplaySettingHelper>> slotData : message.settings.entrySet()) {
            Map<UUID, DisplaySettingHelper> setting = panel.getDisplaySettingsForSlot(slotData.getKey());
            for (Map.Entry<UUID, DisplaySettingHelper> item : slotData.getValue().entrySet()) {
                UUID key = item.getKey();
                if (key == null) {
                    continue;
                }
                setting.put(new UUID(key.getMostSignificantBits(), key.getLeastSignificantBits()), item.getValue());
            }
        }
        panel.resetCardData();
        return null;
    }
}

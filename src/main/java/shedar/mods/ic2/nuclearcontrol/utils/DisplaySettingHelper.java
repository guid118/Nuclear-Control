package shedar.mods.ic2.nuclearcontrol.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.List;

public class DisplaySettingHelper {


    private String settings = "";

    public DisplaySettingHelper() {}

    public DisplaySettingHelper(String settings) {
        this.settings = settings;
    }

    /**
     * @deprecated use any other constructor, and don't use an int to store display settings
     * @param legacySettings Settings
     */
    public DisplaySettingHelper(int legacySettings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            sb.append(((legacySettings >>> i) & 1) == 1 ? "1" : "0");
        }
        this.settings = sb.toString();
    }

    public DisplaySettingHelper(ByteBuf buf) {
        short length = buf.readShort();
        int bytesToRead = (length + 7) / 8;

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < bytesToRead; i++) {
            byte value = buf.readByte();
            sb.append(String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'));
        }
        this.settings = sb.substring(0, bytesToRead);
    }

    /**
     * get the current state of the setting at the given index
     * @param index index of the setting
     * @return value of the index
     */
    public boolean getSetting(int index) {
        return settings.charAt(index) == '1';
    }

    /**
     * add a setting to this DisplaySettingHelper
     * @param value current status
     * @return index
     */
    public int addSetting(boolean value) {
        settings += value ? '1' : '0';
        return settings.length() - 1;
    }

    /**
     * @deprecated do not use.
     * @return the settings as an integer. does not support more than 32 options
     */
    public int getAsInteger() {
        String s = settings.substring(0, Math.min(32,settings.length()));
        return Integer.parseInt(s, 2);
    }


    /**
     * Write the current settings to the given ByteBuf
     * @param buf ByteBuf to write to
     */
    public void writeToByteBuffer(ByteBuf buf) {
        buf.writeShort(settings.length());
        for (int i = 0; i < settings.length(); i += 8) {
            String chunk = settings.substring(i, Math.min(i + 8, settings.length()));
            // Pad if less than 8 bits
            if (chunk.length() < 8) {
                chunk = String.format("%-8s", chunk).replace(' ', '0');
            }
            byte value = (byte) Integer.parseInt(chunk, 2);
            buf.writeByte(value);
        }
    }


    @Override
    public String toString() {
        return settings;
    }
}

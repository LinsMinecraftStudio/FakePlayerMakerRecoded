package org.lins.mmmjjkx.fakeplayermaker.objects.providers;

import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec;

public class GeyserObjectProvider implements IObjectProvider {
    @Override
    public Object minecraftCodec() {
        return MinecraftCodec.CODEC;
    }

    @Override
    public Object codecHelper() {
        return MinecraftCodec.CODEC.getHelperFactory().get();
    }
}

package org.lins.mmmjjkx.fakeplayermaker.objects.providers;

import com.github.steveice10.mc.protocol.codec.MinecraftCodec;

public class DefObjectProvider implements IObjectProvider {
    @Override
    public Object minecraftCodec() {
        return MinecraftCodec.CODEC;
    }

    @Override
    public Object codecHelper() {
        return MinecraftCodec.CODEC.getHelperFactory().get();
    }
}

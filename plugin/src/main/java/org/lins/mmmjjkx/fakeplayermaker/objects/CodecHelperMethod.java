package org.lins.mmmjjkx.fakeplayermaker.objects;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.cloudburstmc.math.vector.Vector3i;

import java.util.BitSet;

@Getter
public enum CodecHelperMethod {
    WRITE_STRING("writeString", ByteBuf.class, String.class),
    WRITE_VAR_INT("writeVarInt", ByteBuf.class, int.class),
    WRITE_FIXED_BITSET("writeFixedBitSet", ByteBuf.class, BitSet.class, int.class),
    WRITE_POSITION("writePosition", ByteBuf.class, Vector3i.class);

    private final String methodName;
    private final Class<?>[] parameterTypes;

    CodecHelperMethod(String methodName, Class<?>... parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }
}

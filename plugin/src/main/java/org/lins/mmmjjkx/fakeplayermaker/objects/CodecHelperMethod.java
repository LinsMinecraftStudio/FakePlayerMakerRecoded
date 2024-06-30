package org.lins.mmmjjkx.fakeplayermaker.objects;

import lombok.Getter;

@Getter
public enum CodecHelperMethod {
    WRITE_STRING("writeString"),
    WRITE_VAR_INT("writeVarInt"),
    WRITE_FIXED_BITSET("writeFixedBitSet");

    private final String methodName;
    private final Class<?>[] parameterTypes;

    CodecHelperMethod(String methodName, Class<?>... parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }
}

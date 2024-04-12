package org.lins.mmmjjkx.fakeplayermaker.impls.v1_19_R3;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.AxisAlignedBB;
import org.lins.mmmjjkx.fakeplayermaker.commons.PlayerActionImplements;

public final class ActionImpl extends PlayerActionImplements {
    @Override
    public void mountNearest(Object player, int radius) {
        EntityPlayer entityPlayer = (EntityPlayer) player;
        //AxisAlignedBB axisAlignedBB = entityPlayer.
    }

    @Override
    public void dismount(Object player) {

    }
}

package org.lins.mmmjjkx.fakeplayermaker.listeners;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.player.lookup.UniqueIdLookupEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lins.mmmjjkx.fakeplayermaker.FPMRecoded;
import org.lins.mmmjjkx.fakeplayermaker.commons.FPMImplements;
import org.lins.mmmjjkx.fakeplayermaker.commons.IFPMPlayer;

public class LPLookupUUIDListener {
    public LPLookupUUIDListener() {
        EventBus bus = LuckPermsProvider.get().getEventBus();
        bus.subscribe(FPMRecoded.INSTANCE, UniqueIdLookupEvent.class, this::onUUIDLookup);
    }

    private void onUUIDLookup(UniqueIdLookupEvent e) {
        Pair<Boolean, IFPMPlayer> pair = FPMRecoded.fakePlayerManager.getFakePlayer(e.getUsername());
        IFPMPlayer player = pair.getValue();
        if (player != null) {
            e.setUniqueId(FPMImplements.getCurrent().getGameProfile(player).getId());
        }
    }
}

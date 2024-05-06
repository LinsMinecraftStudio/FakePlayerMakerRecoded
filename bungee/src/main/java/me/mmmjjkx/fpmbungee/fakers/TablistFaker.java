package me.mmmjjkx.fpmbungee.fakers;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.tab.TabList;

import java.util.UUID;

public class TablistFaker {
    private final Configuration config;

    public TablistFaker(Configuration config) {
        this.config = config;
    }

    public void addTabListName(String name) {
        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            UserConnection user = (UserConnection) player;
            TabList tabList = user.getTabListHandler();
            PlayerListItem item = new PlayerListItem();
            item.setAction(PlayerListItem.Action.ADD_PLAYER);
            PlayerListItem.Item item1 = new PlayerListItem.Item();
            UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

            //PlayerPublicKey key = new PlayerPublicKey(Long.MAX_VALUE, generateRandomBytes(), generateRandomBytes());

            item1.setUsername(name);
            item1.setUuid(uuid);


            if (config.getBoolean("fakers.tablist.useFakePlayerPrefix", false)) {
                String prefix = config.getString("fakePlayerPrefix", "");
                String displayName = prefix + name;
                ComponentBuilder builder = new ComponentBuilder(displayName);
                item1.setDisplayName(builder.build());
            }

            item.setItems(new PlayerListItem.Item[]{item1});

            tabList.onUpdate(item);
        }
    }

    public void removeTabListName(String name) {

    }

    private byte[] generateRandomBytes() {
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Math.random() * 256);
        }
        return bytes;
    }
}

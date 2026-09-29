package net.pvpbattles.client;

import net.minecraft.client.MinecraftClient;

public final class PVPBattlesScreens {
    private PVPBattlesScreens() {}

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new PVPBattlesScreen(client.currentScreen));
    }
}

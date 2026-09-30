package net.pvpbattles.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PVPBattlesHud {
    private static final int MARGIN = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    /** 直近1秒間の左クリックの時刻 */
    private static final Deque<Long> clicks = new ArrayDeque<>();
    private static boolean attackWasDown = false;

    private PVPBattlesHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        if (!PVPBattlesConfig.modEnabled) {
            return;
        }

        // 左クリックを押した瞬間を数える (毎フレーム調べる)
        long now = System.currentTimeMillis();
        boolean attackDown = client.options.attackKey.isPressed();
        if (attackDown && !attackWasDown) {
            clicks.addLast(now);
        }
        attackWasDown = attackDown;
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) {
            clicks.removeFirst();
        }

        int y = MARGIN;

        if (PVPBattlesConfig.hudFpsEnabled) {
            drawLine(context, client, "FPS: " + client.getCurrentFps(), y);
            y += LINE_HEIGHT;
        }
        if (PVPBattlesConfig.hudCpsEnabled) {
            drawLine(context, client, "CPS: " + clicks.size(), y);
            y += LINE_HEIGHT;
        }
        if (PVPBattlesConfig.hudPingEnabled && !client.isInSingleplayer()) {
            drawLine(context, client, "Ping: " + getPing(client) + "ms", y);
        }
    }

    private static void drawLine(DrawContext context, MinecraftClient client, String text, int y) {
        context.drawTextWithShadow(client.textRenderer, Text.literal(text), MARGIN, y, TEXT_COLOR);
    }

    private static int getPing(MinecraftClient client) {
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler == null || client.player == null) {
            return 0;
        }
        PlayerListEntry entry = handler.getPlayerListEntry(client.player.getUuid());
        return entry == null ? 0 : entry.getLatency();
    }
}

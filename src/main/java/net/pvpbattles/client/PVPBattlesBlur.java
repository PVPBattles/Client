package net.pvpbattles.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public final class PVPBattlesBlur {
    private PVPBattlesBlur() {}

    /** インベントリ・チェスト・クラフト画面など (HandledScreen) の背景をぼかす */
    public static void apply(Screen screen, DrawContext context) {
        if (!PVPBattlesConfig.modEnabled || !PVPBattlesConfig.blurEnabled) {
            return;
        }
        if (!(screen instanceof HandledScreen<?>)) {
            return;
        }
        if (MinecraftClient.getInstance().world == null) {
            return;
        }
        context.applyBlur();
    }
}

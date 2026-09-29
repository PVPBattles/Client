package net.pvpbattles.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.SimpleOption;

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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }

        // 強さ 0 のときはぼかさない
        int level = PVPBattlesConfig.blurStrength;
        if (level <= 0) {
            return;
        }

        // Minecraft標準の「メニュー背景のぼかし」(0〜10) に合わせる
        SimpleOption<Integer> option = client.options.getMenuBackgroundBlurriness();
        if (option.getValue() != level) {
            option.setValue(level);
        }

        context.applyBlur();
    }
}

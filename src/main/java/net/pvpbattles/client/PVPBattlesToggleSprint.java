package net.pvpbattles.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public final class PVPBattlesToggleSprint {
    /** ダッシュが入りっぱなしの状態か */
    private static boolean toggled = false;

    private PVPBattlesToggleSprint() {}

    private static boolean isActive() {
        return PVPBattlesConfig.modEnabled && PVPBattlesConfig.toggleSprintEnabled;
    }

    public static void register() {
        // サーバーやワールドに入ったとき、設定がOFFでも Toggle Sprint をONにして、ダッシュを入れる
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (PVPBattlesConfig.modEnabled) {
                PVPBattlesConfig.toggleSprintEnabled = true;
                toggled = true;
            }
        });

        // 抜けたときは状態をリセットする
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            toggled = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinding sprintKey = client.options.sprintKey;
            KeyBinding toggleKey = PVPBattlesKeybinds.getToggleSprintKey();

            // 専用キーが押された回数を数える (機能がOFFでも、押された分は消費する)
            int presses = 0;
            while (toggleKey.wasPressed()) {
                presses++;
            }

            if (!isActive()) {
                if (toggled) {
                    toggled = false;
                    sprintKey.setPressed(false);
                }
                return;
            }

            if (client.player == null || client.currentScreen != null) {
                return;
            }

            // 押された回数が奇数なら切り替える
            if (presses % 2 == 1) {
                toggled = !toggled;
                if (!toggled) {
                    sprintKey.setPressed(false);
                }
                client.player.sendMessage(
                        Text.literal("Toggle Sprint: " + (toggled ? "ON" : "OFF")), true);
            }

            if (toggled) {
                sprintKey.setPressed(true);
            }
        });
    }
}

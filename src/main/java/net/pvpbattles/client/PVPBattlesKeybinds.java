package net.pvpbattles.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class PVPBattlesKeybinds {
    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("pvpbattles", "keybinds"));

    private static KeyBinding openMenu;
    private static KeyBinding toggleSprint;

    private PVPBattlesKeybinds() {}

    public static KeyBinding getToggleSprintKey() {
        return toggleSprint;
    }

    public static void register() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpbattles.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));

        // 初期設定は未設定。キー割り当てから好きなキーを選ぶ
        toggleSprint = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpbattles.toggle_sprint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                // 他の画面 (チャット等) が開いている間は開かない
                if (client.currentScreen == null) {
                    PVPBattlesScreens.open();
                }
            }
        });
    }
}

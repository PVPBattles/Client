package net.pvpbattles.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class PVPBattlesClient implements ClientModInitializer {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 4;

    @Override
    public void onInitializeClient() {
        PVPBattlesConfig.load();
        PVPBattlesKeybinds.register();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((s, context, mouseX, mouseY, delta) ->
                    PVPBattlesBlur.apply(s, context));

            ScreenEvents.afterRender(screen).register((s, context, mouseX, mouseY, delta) ->
                    PVPBattlesOverlay.render(context, s.width, s.height));

            if (screen instanceof TitleScreen) {
                addTitleButton(screen, scaledWidth, scaledHeight);
            }
        });
    }

    private static void addTitleButton(Screen screen, int width, int height) {
        int left = width / 2 - BUTTON_WIDTH / 2;
        int right = left + BUTTON_WIDTH;

        int bottom = -1;
        for (ClickableWidget w : Screens.getButtons(screen)) {
            boolean inCenterColumn = w.getX() < right && w.getX() + w.getWidth() > left;
            if (inCenterColumn) {
                bottom = Math.max(bottom, w.getY() + w.getHeight());
            }
        }
        if (bottom < 0) {
            bottom = height / 4 + 48 + 72 + 12 + BUTTON_HEIGHT;
        }

        int y = Math.min(bottom + GAP, height - BUTTON_HEIGHT - GAP);

        Screens.getButtons(screen).add(
                ButtonWidget.builder(Text.literal("PVPBattles"), b -> PVPBattlesScreens.open())
                        .dimensions(left, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
    }
}

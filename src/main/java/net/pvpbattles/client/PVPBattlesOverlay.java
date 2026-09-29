package net.pvpbattles.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class PVPBattlesOverlay {
    private static final Identifier LOGO =
            Identifier.of("pvpbattles", "textures/gui/pvpbattles_logo.png");
    private static final int LOGO_SIZE = 64;
    private static final int MARGIN = 8;

    private PVPBattlesOverlay() {}

    public static void render(DrawContext context, int screenWidth, int screenHeight) {
        // 画面が小さすぎる場合は、GUIを邪魔しないよう描画しない
        if (screenWidth < LOGO_SIZE * 3 || screenHeight < LOGO_SIZE * 2) {
            return;
        }
        int x = screenWidth - LOGO_SIZE - MARGIN;
        int y = screenHeight - LOGO_SIZE - MARGIN;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                LOGO,
                x, y,
                0.0F, 0.0F,
                LOGO_SIZE, LOGO_SIZE,
                LOGO_SIZE, LOGO_SIZE
        );
    }
}

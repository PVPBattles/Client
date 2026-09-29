package net.pvpbattles.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class PVPBattlesOverlay {
    private static final Identifier LOGO =
            Identifier.of("pvpbattles", "textures/gui/pvpbattles_logo.png");

    // テクスチャ画像の実サイズ (512x256)
    private static final int TEX_WIDTH = 512;
    private static final int TEX_HEIGHT = 256;

    // 画面上の表示サイズ
    private static final int LOGO_WIDTH = 96;
    private static final int LOGO_HEIGHT = 48;
    private static final int MARGIN = 8;

    private PVPBattlesOverlay() {}

    public static void render(DrawContext context, int screenWidth, int screenHeight) {
        if (!PVPBattlesConfig.modEnabled || !PVPBattlesConfig.logoEnabled) {
            return;
        }
        if (screenWidth < LOGO_WIDTH + MARGIN * 2 || screenHeight < LOGO_HEIGHT + MARGIN * 2) {
            return;
        }
        int x = screenWidth - LOGO_WIDTH - MARGIN;
        int y = screenHeight - LOGO_HEIGHT - MARGIN;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                LOGO,
                x, y,
                0.0F, 0.0F,
                LOGO_WIDTH, LOGO_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT
        );
    }
}

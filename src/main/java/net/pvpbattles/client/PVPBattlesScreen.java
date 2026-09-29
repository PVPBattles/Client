package net.pvpbattles.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PVPBattlesScreen extends Screen {
    private static final int BRAND_COLOR = 0xFF7E56E2;

    private final Screen parent;

    public PVPBattlesScreen(Screen parent) {
        super(Text.translatable("pvpbattles.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = Math.min(200, this.width - 40);
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("pvpbattles.screen.close"), b -> this.close())
                        .dimensions(this.width / 2 - w / 2, this.height - 40, w, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4, BRAND_COLOR);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}

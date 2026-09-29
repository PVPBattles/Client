package net.pvpbattles.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PVPBattlesCategoryScreen extends Screen {
    public enum Category {
        OPTIMIZATION("Optimization"),
        HUD("HUD"),
        SCREEN("Screen");

        public final String label;

        Category(String label) {
            this.label = label;
        }
    }

    private static final int BRAND_COLOR = 0xFF7E56E2;

    private final Screen parent;
    private final Category category;

    public PVPBattlesCategoryScreen(Screen parent, Category category) {
        super(Text.literal(category.label));
        this.parent = parent;
        this.category = category;
    }

    private static Text toggleText(String name, boolean value) {
        return Text.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    protected void init() {
        int w = Math.min(200, this.width - 40);
        int x = this.width / 2 - w / 2;
        int y = Math.max(50, this.height / 4 + 10);

        if (category == Category.SCREEN) {
            this.addDrawableChild(
                    ButtonWidget.builder(toggleText("Blur", PVPBattlesConfig.blurEnabled), b -> {
                                PVPBattlesConfig.blurEnabled = !PVPBattlesConfig.blurEnabled;
                                PVPBattlesConfig.save();
                                b.setMessage(toggleText("Blur", PVPBattlesConfig.blurEnabled));
                            })
                            .dimensions(x, y, w, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(toggleText("Logo", PVPBattlesConfig.logoEnabled), b -> {
                                PVPBattlesConfig.logoEnabled = !PVPBattlesConfig.logoEnabled;
                                PVPBattlesConfig.save();
                                b.setMessage(toggleText("Logo", PVPBattlesConfig.logoEnabled));
                            })
                            .dimensions(x, y + 24, w, 20)
                            .build()
            );
        }

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Back"), b -> this.close())
                        .dimensions(x, this.height - 40, w, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, Math.max(20, this.height / 4 - 15), BRAND_COLOR);
        if (category != Category.SCREEN) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Coming soon"), this.width / 2, this.height / 2, 0xFFAAAAAA);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}

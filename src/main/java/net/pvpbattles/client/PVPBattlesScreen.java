package net.pvpbattles.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PVPBattlesScreen extends Screen {
    private static final int BRAND_COLOR = 0xFF7E56E2;
    private static final int COLUMNS = 2;
    private static final int GAP = 4;

    private final Screen parent;
    private int titleY;

    public PVPBattlesScreen(Screen parent) {
        super(Text.translatable("pvpbattles.screen.title"));
        this.parent = parent;
    }

    private static Text toggleText(String name, boolean value) {
        return Text.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    protected void init() {
        PVPBattlesCategoryScreen.Category[] categories = PVPBattlesCategoryScreen.Category.values();

        int cellW = (Math.min(204, this.width - 40) - GAP) / COLUMNS;
        int fullW = cellW * COLUMNS + GAP;
        int x = this.width / 2 - fullW / 2;

        int rows = (categories.length + COLUMNS - 1) / COLUMNS;
        int total = 20 + 12 + (rows * 20 + (rows - 1) * GAP) + 8 + 20;
        int y = Math.max(40, (this.height - total) / 2 + 10);
        this.titleY = y - 18;

        this.addDrawableChild(
                ButtonWidget.builder(toggleText("Mod Enable", PVPBattlesConfig.modEnabled), b -> {
                            PVPBattlesConfig.modEnabled = !PVPBattlesConfig.modEnabled;
                            PVPBattlesConfig.save();
                            b.setMessage(toggleText("Mod Enable", PVPBattlesConfig.modEnabled));
                        })
                        .dimensions(x, y, fullW, 20)
                        .build()
        );
        y += 20 + 12;

        for (int i = 0; i < categories.length; i++) {
            PVPBattlesCategoryScreen.Category category = categories[i];
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int bx = x + col * (cellW + GAP);
            int by = y + row * (20 + GAP);

            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal(category.label), b ->
                                    this.client.setScreen(new PVPBattlesCategoryScreen(this, category)))
                            .dimensions(bx, by, cellW, 20)
                            .build()
            );
        }
        y += rows * 20 + (rows - 1) * GAP + 8;

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("pvpbattles.screen.close"), b -> this.close())
                        .dimensions(x, y, fullW, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.titleY, BRAND_COLOR);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}

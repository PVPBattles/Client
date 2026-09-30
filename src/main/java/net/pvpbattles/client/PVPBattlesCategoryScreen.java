package net.pvpbattles.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class PVPBattlesCategoryScreen extends Screen {
    public enum Category {
        OPTIMIZATION("Optimization"),
        HUD("HUD"),
        SCREEN("Screen"),
        GAMEPLAY("GamePlay");

        public final String label;

        Category(String label) {
            this.label = label;
        }
    }

    /** ぼかしの強さ (0〜10) のスライダー */
    private static class BlurSlider extends SliderWidget {
        BlurSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(),
                    PVPBattlesConfig.blurStrength / (double) PVPBattlesConfig.MAX_BLUR_STRENGTH);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int strength = (int) Math.round(this.value * PVPBattlesConfig.MAX_BLUR_STRENGTH);
            this.setMessage(Text.literal("Blur Strength: " + strength));
        }

        @Override
        protected void applyValue() {
            int strength = (int) Math.round(this.value * PVPBattlesConfig.MAX_BLUR_STRENGTH);
            PVPBattlesConfig.blurStrength = strength;
            // 1刻みにそろえる
            this.value = strength / (double) PVPBattlesConfig.MAX_BLUR_STRENGTH;
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

    private static Text serverText() {
        if (PVPBattlesConfig.currentServerAddress() == null) {
            return Text.literal("This server: join a server first");
        }
        return toggleText("This server: Optimizers", !PVPBattlesConfig.isServerBlocked());
    }

    private void addToggle(String name, BooleanSupplier getter, Consumer<Boolean> setter, int x, int y, int w) {
        this.addDrawableChild(
                ButtonWidget.builder(toggleText(name, getter.getAsBoolean()), b -> {
                            setter.accept(!getter.getAsBoolean());
                            PVPBattlesConfig.save();
                            b.setMessage(toggleText(name, getter.getAsBoolean()));
                        })
                        .dimensions(x, y, w, 20)
                        .build()
        );
    }

    @Override
    protected void init() {
        int w = Math.min(200, this.width - 40);
        int x = this.width / 2 - w / 2;
        int y = Math.max(50, this.height / 4 + 10);

        if (category == Category.OPTIMIZATION) {
            addToggle("Crystal Optimizer",
                    () -> PVPBattlesConfig.crystalOptimizerEnabled,
                    v -> PVPBattlesConfig.crystalOptimizerEnabled = v,
                    x, y, w);
            addToggle("Anchor Optimizer",
                    () -> PVPBattlesConfig.anchorOptimizerEnabled,
                    v -> PVPBattlesConfig.anchorOptimizerEnabled = v,
                    x, y + 24, w);

            // 今つないでいるサーバーで Optimizer を止める / 戻す
            ButtonWidget serverButton = ButtonWidget.builder(serverText(), b -> {
                        PVPBattlesConfig.toggleCurrentServerBlocked();
                        b.setMessage(serverText());
                    })
                    .dimensions(x, y + 56, w, 20)
                    .build();
            serverButton.active = PVPBattlesConfig.currentServerAddress() != null;
            this.addDrawableChild(serverButton);
        }

        if (category == Category.HUD) {
            addToggle("FPS",
                    () -> PVPBattlesConfig.hudFpsEnabled,
                    v -> PVPBattlesConfig.hudFpsEnabled = v,
                    x, y, w);
            addToggle("CPS",
                    () -> PVPBattlesConfig.hudCpsEnabled,
                    v -> PVPBattlesConfig.hudCpsEnabled = v,
                    x, y + 24, w);
            addToggle("Ping",
                    () -> PVPBattlesConfig.hudPingEnabled,
                    v -> PVPBattlesConfig.hudPingEnabled = v,
                    x, y + 48, w);
        }

        if (category == Category.SCREEN) {
            addToggle("Blur",
                    () -> PVPBattlesConfig.blurEnabled,
                    v -> PVPBattlesConfig.blurEnabled = v,
                    x, y, w);
            this.addDrawableChild(new BlurSlider(x, y + 24, w, 20));
            addToggle("Logo",
                    () -> PVPBattlesConfig.logoEnabled,
                    v -> PVPBattlesConfig.logoEnabled = v,
                    x, y + 48, w);
        }

        if (category == Category.GAMEPLAY) {
            addToggle("Toggle Sprint",
                    () -> PVPBattlesConfig.toggleSprintEnabled,
                    v -> PVPBattlesConfig.toggleSprintEnabled = v,
                    x, y, w);
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
    }

    @Override
    public void removed() {
        // スライダーの値は、画面を閉じるときに保存する
        PVPBattlesConfig.save();
        super.removed();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}

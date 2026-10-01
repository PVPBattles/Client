package net.pvpbattles.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PVPBattlesClient implements ClientModInitializer {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 4;

    /** Mod Menu など、ほかのModの処理が終わったあとに動かすための順番 */
    private static final Identifier LATE_PHASE = Identifier.of("pvpbattles", "late");

    @Override
    public void onInitializeClient() {
        PVPBattlesConfig.load();
        PVPBattlesKeybinds.register();
        PVPBattlesCrystalOptimizer.register();
        PVPBattlesAnchorOptimizer.register();
        PVPBattlesToggleSprint.register();
        PVPBattlesHud.register();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((s, context, mouseX, mouseY, delta) ->
                    PVPBattlesBlur.apply(s, context));

            ScreenEvents.afterRender(screen).register((s, context, mouseX, mouseY, delta) ->
                    PVPBattlesOverlay.render(context, s.width, s.height));
        });

        // タイトル画面のボタンは、ほかのModがボタンを並べ終えたあとに足す
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, LATE_PHASE);
        ScreenEvents.AFTER_INIT.register(LATE_PHASE, (client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                addTitleButton(screen, scaledWidth, scaledHeight);
            }
        });
    }

    private static ButtonWidget createButton(int left, int y) {
        return ButtonWidget.builder(Text.literal("PVPBattles"), b -> PVPBattlesScreens.open())
                .dimensions(left, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
    }

    /** 中央カラムの一番下に足す (ずらせないときの予備) */
    private static void addBelowAll(Screen screen, int left, int allBottom, int height) {
        int y = Math.min(allBottom + GAP, height - BUTTON_HEIGHT - GAP);
        Screens.getButtons(screen).add(createButton(left, y));
    }

    private static void addTitleButton(Screen screen, int width, int height) {
        int left = width / 2 - BUTTON_WIDTH / 2;
        int right = left + BUTTON_WIDTH;

        List<ClickableWidget> all = new ArrayList<>(Screens.getButtons(screen));

        // 中央カラムにあるボタンを、上から順に並べる
        List<ClickableWidget> column = new ArrayList<>();
        for (ClickableWidget w : all) {
            if (w.getX() < right && w.getX() + w.getWidth() > left) {
                column.add(w);
            }
        }
        if (column.isEmpty()) {
            addBelowAll(screen, left, height / 4 + 48 + 72 + 12 + BUTTON_HEIGHT, height);
            return;
        }
        column.sort(Comparator.comparingInt(ClickableWidget::getY));

        // 行ごとに見て、縦の隙間が一番大きい場所を探す (本体の12pxの隙間がここに当たる)
        int bestGap = GAP;
        int mainBottom = -1;
        int rowBottom = column.get(0).getY() + column.get(0).getHeight();
        for (int i = 1; i < column.size(); i++) {
            ClickableWidget w = column.get(i);
            if (w.getY() < rowBottom) {
                // 同じ行 (横に並んだボタン)
                rowBottom = Math.max(rowBottom, w.getY() + w.getHeight());
                continue;
            }
            int gap = w.getY() - rowBottom;
            if (gap > bestGap) {
                bestGap = gap;
                mainBottom = rowBottom;
            }
            rowBottom = w.getY() + w.getHeight();
        }
        int allBottom = rowBottom;

        // 大きな隙間が見つからないときは、一番下に足す
        if (mainBottom < 0) {
            addBelowAll(screen, left, allBottom, height);
            return;
        }

        // 隙間より下にある行 (設定 / 終了 / 言語 / アクセシビリティ)
        List<ClickableWidget> lower = new ArrayList<>();
        int lowerBottom = -1;
        for (ClickableWidget w : all) {
            if (w.getY() >= mainBottom) {
                lower.add(w);
                lowerBottom = Math.max(lowerBottom, w.getY() + w.getHeight());
            }
        }

        // 下の行を、PVPBattlesボタンの分だけずらす (間隔は4pxにそろえる)
        int shift = GAP + BUTTON_HEIGHT + GAP - bestGap;
        if (shift < 0) {
            shift = 0;
        }

        // 画面の下にはみ出す場合は、ずらさず、一番下に足す
        if (lowerBottom + shift > height - 2) {
            addBelowAll(screen, left, allBottom, height);
            return;
        }

        for (ClickableWidget w : lower) {
            w.setY(w.getY() + shift);
        }
        Screens.getButtons(screen).add(createButton(left, mainBottom + GAP));
    }
}

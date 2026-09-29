package net.pvpbattles.client;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.ActionResult;

public final class PVPBattlesCrystalOptimizer {
    private PVPBattlesCrystalOptimizer() {}

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // クライアント側でだけ動かす (シングルプレイのサーバー側は除外)
            if (!world.isClient()) {
                return ActionResult.PASS;
            }
            if (!PVPBattlesConfig.modEnabled || !PVPBattlesConfig.crystalOptimizerEnabled) {
                return ActionResult.PASS;
            }
            if (!(entity instanceof EndCrystalEntity) || !(world instanceof ClientWorld clientWorld)) {
                return ActionResult.PASS;
            }
            if (player.isSpectator()) {
                return ActionResult.PASS;
            }
            // 弱化などで攻撃力が0以下だと、クリスタルは壊れない
            if (player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) <= 0.0) {
                return ActionResult.PASS;
            }

            // サーバーの返事を待たずに、クリスタルをクライアント側で先に消す
            clientWorld.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);

            // 消えたクリスタルを狙ったままにならないように、照準の先を取り直す
            MinecraftClient.getInstance().gameRenderer.updateCrosshairTarget(1.0F);

            // 攻撃パケットは通常どおり送る
            return ActionResult.PASS;
        });
    }
}

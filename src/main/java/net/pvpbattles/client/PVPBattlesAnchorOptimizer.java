package net.pvpbattles.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PVPBattlesAnchorOptimizer {
    private static final int MAX_CHARGES = 4;

    /** 爆発する操作をしたアンカーの位置 (操作が終わったあとで消す) */
    private static BlockPos pendingExplosion = null;

    private PVPBattlesAnchorOptimizer() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // クライアント側でだけ動かす (シングルプレイのサーバー側は除外)
            if (!world.isClient()) {
                return ActionResult.PASS;
            }
            if (!PVPBattlesConfig.modEnabled || !PVPBattlesConfig.anchorOptimizerEnabled) {
                return ActionResult.PASS;
            }
            // 禁止に設定したサーバーでは動かさない
            if (PVPBattlesConfig.isServerBlocked()) {
                return ActionResult.PASS;
            }
            if (player.isSpectator()) {
                return ActionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!state.isOf(Blocks.RESPAWN_ANCHOR)) {
                return ActionResult.PASS;
            }

            // スニーク中に何か持っていると、ブロックは使われない
            boolean holdingSomething = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
            if (player.shouldCancelInteraction() && holdingSomething) {
                return ActionResult.PASS;
            }

            int charges = state.get(RespawnAnchorBlock.CHARGES);
            boolean holdingGlowstone = player.getStackInHand(hand).isOf(Items.GLOWSTONE);

            // チャージされる操作は、Minecraft本体がクライアント側で先に反映するので、何もしない
            if (holdingGlowstone && charges < MAX_CHARGES) {
                return ActionResult.PASS;
            }
            if (hand == Hand.MAIN_HAND
                    && player.getOffHandStack().isOf(Items.GLOWSTONE)
                    && charges < MAX_CHARGES) {
                return ActionResult.PASS;
            }

            // ネザー以外でチャージ済みのアンカーを使うと爆発する: 操作が終わったあとにアンカーを消す
            if (charges > 0 && !World.NETHER.equals(world.getRegistryKey())) {
                pendingExplosion = pos.toImmutable();
            }

            // 操作パケットは通常どおり送る (最終結果はサーバーが決める)
            return ActionResult.PASS;
        });

        // 操作の処理が終わったあと、同じtickの最後にアンカーを消す
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            BlockPos pos = pendingExplosion;
            if (pos == null) {
                return;
            }
            pendingExplosion = null;

            if (client.world != null && client.world.getBlockState(pos).isOf(Blocks.RESPAWN_ANCHOR)) {
                client.world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
        });
    }
}

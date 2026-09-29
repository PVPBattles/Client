package net.pvpbattles.client;

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

            // グロウストーンでチャージ: チャージを1つ増やす
            if (holdingGlowstone && charges < MAX_CHARGES) {
                world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, charges + 1), Block.NOTIFY_ALL);
                return ActionResult.PASS;
            }

            // メインハンドが別の物で、オフハンドのグロウストーンがチャージする場合は、オフハンドに任せる
            if (hand == Hand.MAIN_HAND
                    && player.getOffHandStack().isOf(Items.GLOWSTONE)
                    && charges < MAX_CHARGES) {
                return ActionResult.PASS;
            }

            // ネザー以外でチャージ済みのアンカーを使うと爆発する: 先にアンカーを消す
            if (charges > 0 && !World.NETHER.equals(world.getRegistryKey())) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }

            // 操作パケットは通常どおり送る (最終結果はサーバーが決める)
            return ActionResult.PASS;
        });
    }
}

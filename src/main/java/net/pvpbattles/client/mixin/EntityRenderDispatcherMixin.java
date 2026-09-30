package net.pvpbattles.client.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.pvpbattles.client.PVPBattlesCrystalOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpbattles$hideTargetedCrystal(Entity entity, Frustum frustum, double x, double y, double z,
                                                CallbackInfoReturnable<Boolean> cir) {
        // 攻撃ボタンを押した瞬間から、狙っているクリスタルの表示だけを止める
        if (PVPBattlesCrystalOptimizer.shouldHide(entity)) {
            cir.setReturnValue(false);
        }
    }
}

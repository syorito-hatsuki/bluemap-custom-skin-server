package dev.syoritohatsuki.bluemapcustomskinserver.mixin.de.bluecolored.bluemap.common.plugin.skins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.bluecolored.bluemap.common.plugin.skins.PlayerSkinUpdater;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerSkinUpdater.class)
public class PlayerSkinUpdaterMixin {
    @ModifyExpressionValue(
            method = "updateSkin",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/TimeUnit;toMillis(J)J"
            )
    )
    private long bypassRefreshTimer(long original, @Local(argsOnly = true) UUID playerUuid) {
        if (SkinUpdateQueue.INSTANCE.contains(playerUuid)) {
            BlueMapCustomSkinServerAddon.INSTANCE.getLogger().info("SkinUpdateQueue found uuid");
            return 0;
        }
        return original;
    }

    @Inject(
            method = "updateSkin",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private void removeFromQueue(UUID playerUuid, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        SkinUpdateQueue.INSTANCE.remove(playerUuid);
    }
}

package dev.syoritohatsuki.bluemapcustomskinserver.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.bluecolored.bluemap.common.plugin.skins.PlayerSkinUpdater;
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
    private long bypassRefreshTimer(long original, @Local(argsOnly = true) UUID playerUUID, @Local(name = "now") long now, @Local(name = "lastUpdate") long lastUpdate) {
        System.out.println("-------");
        System.out.println("Now: " + now);
        System.out.println("Last update: " + lastUpdate);
        System.out.println("Result: " + (now - lastUpdate));
        System.out.println("Original: " + original);
        System.out.println("-------");
        if (SkinUpdateQueue.INSTANCE.contains(playerUUID)) {
            System.out.println("SkinUpdateQueue found uuid");
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

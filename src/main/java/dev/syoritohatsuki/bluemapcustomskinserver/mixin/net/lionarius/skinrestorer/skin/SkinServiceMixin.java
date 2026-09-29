package dev.syoritohatsuki.bluemapcustomskinserver.mixin.net.lionarius.skinrestorer.skin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import net.lionarius.skinrestorer.skin.SkinService;
import net.lionarius.skinrestorer.skin.SkinValue;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkinService.class)
public class SkinServiceMixin {
    @Inject(
            method = "applySkin(Lnet/minecraft/server/level/ServerPlayer;Lnet/lionarius/skinrestorer/skin/SkinValue;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/lionarius/skinrestorer/util/TickedScheduler;cancel(Ljava/lang/Object;)V"
            )
    )
    private static void notifyBluemapAboutSkinChange(ServerPlayer player, SkinValue value, boolean save, CallbackInfoReturnable<Boolean> cir, @Local(name = "profile") GameProfile profile) {
        try {
            SkinUpdateQueue.INSTANCE.add(profile.id());
            SkinUpdateQueue.INSTANCE.getBluemapPluginInstance().getSkinUpdater().updateSkin(profile.id());
        } catch (Exception e) {
            BlueMapCustomSkinServerAddon.INSTANCE.getLogger().error(e.getLocalizedMessage());
        }
    }
}

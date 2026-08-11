package dev.syoritohatsuki.bluemapcustomskinserver.mixin.net.lionarius.skinrestorer.skin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import net.lionarius.skinrestorer.skin.SkinService;
import net.lionarius.skinrestorer.skin.SkinValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(SkinService.class)
public class SkinServiceMixin {
    @Inject(
            method = "applySkin(Lnet/minecraft/server/MinecraftServer;Ljava/lang/Iterable;Lnet/lionarius/skinrestorer/skin/SkinValue;Z)Ljava/util/Collection;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/lionarius/skinrestorer/util/TickedScheduler;cancel(Ljava/lang/Object;)V"
            )
    )
    private static void notifyBluemapAboutSkinChange(MinecraftServer server, Iterable<ServerPlayer> targets, SkinValue value, boolean save, CallbackInfoReturnable<Collection<ServerPlayer>> cir, @Local(name = "profile") GameProfile profile) {
        try {
            SkinUpdateQueue.INSTANCE.add(profile.id());
            SkinUpdateQueue.INSTANCE.getBluemapPluginInstance().getSkinUpdater().updateSkin(profile.id());
        } catch (Exception e) {
            BlueMapCustomSkinServerAddon.INSTANCE.getLogger().error(e.getLocalizedMessage());
        }
    }
}

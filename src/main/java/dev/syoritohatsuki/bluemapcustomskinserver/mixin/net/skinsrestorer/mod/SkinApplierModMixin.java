package dev.syoritohatsuki.bluemapcustomskinserver.mixin.net.skinsrestorer.mod;

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import net.minecraft.server.level.ServerPlayer;
import net.skinsrestorer.mod.SkinApplierMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinApplierMod.class)
public class SkinApplierModMixin {
    @Inject(method = "refresh", at = @At("TAIL"))
    private void notifyBluemapAboutSkinChange(ServerPlayer player, CallbackInfo ci) {
        try {
            SkinUpdateQueue.INSTANCE.add(player.getGameProfile().id());
            SkinUpdateQueue.INSTANCE.getBluemapPluginInstance().getSkinUpdater().updateSkin(player.getGameProfile().id());
        } catch (Exception e) {
            BlueMapCustomSkinServerAddon.INSTANCE.getLogger().error(e.getLocalizedMessage());
        }
    }
}

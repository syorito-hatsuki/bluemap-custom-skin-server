package dev.syoritohatsuki.bluemapcustomskinserver.mixin.de.bluecolored.bluemap.fabric;

import de.bluecolored.bluemap.common.plugin.Plugin;
import de.bluecolored.bluemap.fabric.FabricMod;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricMod.class)
public class FabricModMixin {
    @Shadow
    @Final
    private Plugin pluginInstance;

    @Inject(method = "onInitialize", at = @At(value = "HEAD"))
    private void catchPluginInstance(CallbackInfo ci) {
        SkinUpdateQueue.INSTANCE.registerBluemapPluginInstance(pluginInstance);
    }
}

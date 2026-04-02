package dev.syoritohatsuki.bluemapcustomskinserver.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.authlib.GameProfile;
import dev.syoritohatsuki.bluemapcustomskinserver.SkinUpdateQueue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MServerPlayerEntity_TailoredPlayerMixin extends Player {

    public MServerPlayerEntity_TailoredPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @TargetHandler(
            mixin = "org.samo_lego.fabrictailor.mixin.MServerPlayerEntity_TailoredPlayer",
            name = "fabrictailor_reloadSkin"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"))
    public void fabrictailor_reloadSkin(CallbackInfo ci) {
        SkinUpdateQueue.INSTANCE.add(getUUID());
        SkinUpdateQueue.INSTANCE.getBluemapPluginInstance().getSkinUpdater().updateSkin(getUUID());
    }
}
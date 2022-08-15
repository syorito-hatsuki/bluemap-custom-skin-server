package dev.syoritohatsuki.bluemapcustomskinserver.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.bluecolored.bluemap.common.plugin.skins.PlayerSkin;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.api.CustomApi;
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Mixin(PlayerSkin.class)
public abstract class CustomServerPlayerSkin {
    @Shadow
    @Final
    private UUID uuid;

    @Shadow
    protected abstract Reader requestProfileJson() throws IOException;

    @Shadow
    protected abstract String readTextureInfoJson(JsonElement json) throws IOException;

    @Shadow
    protected abstract String readTextureUrl(JsonElement json) throws IOException;

    public Future<BufferedImage> loadSkin() {
        var serverType = ConfigManager.INSTANCE.read().getServerType();
        BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("Server type: " + serverType.name());
        BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("USER: " + BlueMapCustomSkinServerAddon.INSTANCE.getPLAYER_LIST().get(uuid));
        BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("UUID: " + uuid);
        return switch (serverType) {
            case CUSTOM -> new CustomApi(uuid, BlueMapCustomSkinServerAddon.INSTANCE.getPLAYER_LIST().get(uuid)).getSkin();
            case MOJANG -> mojangApi();
        };
    }

    private CompletableFuture<BufferedImage> mojangApi() {
        var image = new CompletableFuture<BufferedImage>();
        new Thread(() -> {
            try (Reader reader = requestProfileJson()) {
                BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("Connecting to Mojang skin server");
                var textureInfoJson = readTextureInfoJson(JsonParser.parseReader(reader));
                var textureUrl = readTextureUrl(JsonParser.parseString(textureInfoJson));
                image.complete(ImageIO.read(new URL(textureUrl)));
            } catch (IOException exception) {
                BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().warn(exception.getMessage());
                image.completeExceptionally(exception);
            }
        }).start();
        return image;
    }
}

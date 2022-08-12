package dev.syoritohatsuki.bluemapcustomskinserver.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.bluecolored.bluemap.common.plugin.skins.PlayerSkin;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config;
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Mixin(PlayerSkin.class)
public abstract class CustomServerPlayerSkin {

    private final Config config = ConfigManager.INSTANCE.read();
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
        var image = new CompletableFuture<BufferedImage>();
        if (config.getEnableCustomServer()) {
            new Thread(() -> {
                try {
                    var server = getImageFromServer();
                    image.complete(ImageIO.read(new URL(server)));
                    BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("Connecting to custom skin server");
                    BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().info("Server URL: " + server);
                } catch (IOException | InterruptedException exception) {
                    BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().warn("Incorrect skin server or skin not found");
                    BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().warn(exception.getMessage());
                }
            }).start();
        } else {
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
        }
        return image;
    }

    private String getImageFromServer() throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .uri(URI.create(config.getCustomSkinServerUrl() + uuid))
                .build(), HttpResponse.BodyHandlers.ofString()).body();
    }
}

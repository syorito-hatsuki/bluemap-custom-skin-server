package dev.syoritohatsuki.bluemapcustomskinserver.mixin;

import de.bluecolored.bluemap.common.plugin.skins.PlayerSkin;
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon;
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Mixin(PlayerSkin.class)
public class CustomServerPlayerSkin {
    @Final
    private UUID uuid;

    public Future<BufferedImage> loadSkin() {
        CompletableFuture<BufferedImage> image = new CompletableFuture<>();
        new Thread(() -> {
            try {
                String textureUrl = this.getImageFromServer();
                image.complete(ImageIO.read(new URL(textureUrl)));
            } catch (IOException | InterruptedException ignore) {
                BlueMapCustomSkinServerAddon.INSTANCE.getLOGGER().error("INCORRECT SKIN SERVER");
            }
        }).start();
        return image;
    }

    private String getImageFromServer() throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .uri(URI.create(ConfigManager.INSTANCE.read().getCustomSkinServerUrl() + this.uuid))
                .build(), HttpResponse.BodyHandlers.ofString()).body();
    }
}

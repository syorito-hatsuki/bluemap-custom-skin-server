package dev.syoritohatsuki.bluemapcustomskinserver.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.ADMINS_CHECK
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import java.nio.file.Paths

private val minecraftRootPath = Paths.get("").toAbsolutePath().toString()

fun LiteralArgumentBuilder<ServerCommandSource>.getAbstractPath(): LiteralArgumentBuilder<ServerCommandSource> {
    return requires(CommandManager.requirePermissionLevel(ADMINS_CHECK)).then(
        CommandManager.literal("get-abstract-path").executes { context ->
            context.source.sendFeedback({
                Text.literal(minecraftRootPath).styled {
                    return@styled it.withClickEvent(ClickEvent.CopyToClipboard(minecraftRootPath)).withHoverEvent(
                        HoverEvent.ShowText(Text.literal("Click to copy on clipboard"))
                    )
                }
            }, false)
            return@executes 1
        })
}
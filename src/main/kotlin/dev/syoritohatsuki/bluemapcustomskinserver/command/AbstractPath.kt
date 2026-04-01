package dev.syoritohatsuki.bluemapcustomskinserver.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.nio.file.Paths

private val minecraftRootPath = Paths.get("").toAbsolutePath().toString()

fun LiteralArgumentBuilder<ServerCommandSource>.getAbstractPath(vararg aliases: String = arrayOf("get-abstract-path")) {
    aliases.forEach { alias ->
        literal(alias) {
            executes { context ->
                context.source.sendFeedback({
                    Text.literal(minecraftRootPath).styled { style ->
                        style.withClickEvent(ClickEvent.CopyToClipboard("file://$minecraftRootPath")).withHoverEvent(
                            HoverEvent.ShowText(Text.literal("Click to copy on clipboard"))
                        ).withUnderline(true).withoutShadow().withColor(TextColor.fromRgb(-11184641))
                    }
                }, false)
                return@executes 1
            }
        }
    }
}
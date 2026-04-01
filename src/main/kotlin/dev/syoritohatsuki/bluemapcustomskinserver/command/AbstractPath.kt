package dev.syoritohatsuki.bluemapcustomskinserver.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.literal
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.TextColor
import java.nio.file.Paths

private val minecraftRootPath = Paths.get("").toAbsolutePath().toString()

fun LiteralArgumentBuilder<CommandSourceStack>.getAbstractPath(vararg aliases: String = arrayOf("get-abstract-path")) {
    aliases.forEach { alias ->
        literal(alias) {
            executes { context ->
                context.source.sendSuccess({
                    Component.literal(minecraftRootPath).withStyle { style ->
                        style.withClickEvent(ClickEvent.CopyToClipboard("file://$minecraftRootPath")).withHoverEvent(
                            HoverEvent.ShowText(Component.literal("Click to copy on clipboard"))
                        ).withUnderlined(true).withoutShadow().withColor(TextColor.fromRgb(-11184641))
                    }
                }, false)
                return@executes 1
            }
        }
    }
}
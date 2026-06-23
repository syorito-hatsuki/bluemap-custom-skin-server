package dev.syoritohatsuki.bluemapcustomskinserver.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.literal
import dev.syoritohatsuki.bluemapcustomskinserver.integration.IntegrationRegistry
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

fun LiteralArgumentBuilder<CommandSourceStack>.getAvailableIntegrations(vararg aliases: String = arrayOf("get-abstract-path")) {
    aliases.forEach { alias ->
        literal(alias) {
            executes { context ->
                context.source.sendSuccess({
                    Component.empty().apply {
                        append(Component.literal("Available integrations:\n"))

                        IntegrationRegistry.getIds().forEach { id ->
                            append(Component.literal("- "))
                            append(Component.literal("$id").withStyle { style ->
                                style.withClickEvent(ClickEvent.CopyToClipboard(id.toString()))
                                    .withHoverEvent(HoverEvent.ShowText(Component.literal("Click to copy on clipboard")))
                                    .withColor(ChatFormatting.BLUE)
                            })
                            append(
                                Component.literal(
                                    when (ConfigManager.read().integration) {
                                        id -> " [Current]\n"
                                        else -> "\n"
                                    }
                                ).withStyle { style ->
                                    style.withColor(ChatFormatting.GREEN)
                                })
                        }
                    }

                }, false)

                return@executes 1
            }
        }
    }
}
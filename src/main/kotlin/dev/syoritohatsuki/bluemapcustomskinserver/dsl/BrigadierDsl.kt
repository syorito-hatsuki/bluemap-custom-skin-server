package dev.syoritohatsuki.bluemapcustomskinserver.dsl

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.Commands
import net.minecraft.server.permissions.PermissionCheck
import net.minecraft.server.permissions.PermissionSetSupplier
import java.util.function.Predicate

/**
 * @author zeroeightysix
 * @see <a href="https://gist.github.com/zeroeightysix/cc982cc0526e9a367d7f42a1dd8c354e">Origin</a>
 * */
@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class BrigadierDsl

infix fun <S> CommandDispatcher<S>.register(builder: () -> LiteralArgumentBuilder<S>): LiteralCommandNode<S> =
    this.register(builder.invoke())

/**
 * Creates a new [LiteralArgumentBuilder] without a parent.
 */
fun <T> rootLiteral(
    name: String, block: (@BrigadierDsl LiteralArgumentBuilder<T>).() -> Unit
): LiteralArgumentBuilder<T> = LiteralArgumentBuilder.literal<T>(name).also(block)

/**
 * Appends a new [literal](LiteralArgumentBuilder) to `this` [ArgumentBuilder].
 *
 * @param name the name of the literal argument
 * @param block the receiver function for further construction of the literal argument
 */
fun <T> ArgumentBuilder<T, *>.literal(
    name: String, block: (@BrigadierDsl LiteralArgumentBuilder<T>).() -> Unit
): ArgumentBuilder<*, *> = then(rootLiteral(name, block))

/**
 * Appends a new required argument to `this` [ArgumentBuilder].
 *
 * @param name the name of the required argument
 * @param argument the type of required argument, for example [IntegerArgumentType]
 * @param block the receiver function for further construction of the required argument
 */
fun <S, T : ArgumentBuilder<S, T>, R> ArgumentBuilder<S, T>.argument(
    name: String, argument: ArgumentType<R>, block: (@BrigadierDsl RequiredArgumentBuilder<S, R>).() -> Unit
): T = then(RequiredArgumentBuilder.argument<S, R>(name, argument).also(block))

/**
 * A shorthand for appending a boolean required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.bool(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, Boolean>).() -> Unit
) = argument(name, BoolArgumentType.bool(), block)

/**
 * A shorthand for appending a double required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.double(
    name: String, block: RequiredArgumentBuilder<S, Double>.() -> Unit
) = argument(name, DoubleArgumentType.doubleArg(), block)

/**
 * A shorthand for appending a float required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.float(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, Float>).() -> Unit
) = argument(name, FloatArgumentType.floatArg(), block)

/**
 * A shorthand for appending a integer required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.integer(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, Int>).() -> Unit
) = argument(name, IntegerArgumentType.integer(), block)

/**
 * A shorthand for appending a `long` required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.long(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, Long>).() -> Unit
) = argument(name, LongArgumentType.longArg(), block)

/**
 * A shorthand for appending a string required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.string(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, String>).() -> Unit
) = argument(name, StringArgumentType.string(), block)

/**
 * A shorthand for appending a greedy string required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.greedyString(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, String>).() -> Unit
) = argument(name, StringArgumentType.greedyString(), block)

/**
 * A shorthand for appending a word required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.word(
    name: String, block: (@BrigadierDsl RequiredArgumentBuilder<S, String>).() -> Unit
) = argument(name, StringArgumentType.word(), block)

/**
 * Sets the executes callback for `this` [ArgumentBuilder]
 *
 * @param command the callback
 */
infix fun <S> ArgumentBuilder<S, *>.does(command: (@BrigadierDsl CommandContext<S>) -> Int): ArgumentBuilder<*, *> =
    executes(command)

/**
 * Gets the value of a (required) argument in the command hierarchy
 *
 * @see CommandContext.getArgument
 */
inline infix fun <reified R, S> String.from(ctx: CommandContext<S>): R = ctx.getArgument(this, R::class.java)

fun <S> ArgumentBuilder<S, *>.requiresPermission(
    check: PermissionCheck,
    block: (@BrigadierDsl PermissionScope<S>).() -> Unit
): ArgumentBuilder<*, *> where S : PermissionSetSupplier =
    PermissionScope(this, Commands.hasPermission<S>(check)).apply(block).parent

class PermissionScope<S>(
    val parent: ArgumentBuilder<S, *>,
    private val predicate: Predicate<S>
) {
    fun literal(
        name: String,
        block: (@BrigadierDsl LiteralArgumentBuilder<S>).() -> Unit
    ) {
        parent.literal(name) {
            requires(predicate)
            block()
        }
    }
}
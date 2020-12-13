package org.navgurukul.chat.features.command

import org.matrix.android.sdk.api.session.identity.ThreePid

/**
 * Represent a parsed command
 */
sealed class ParsedCommand {
    // This is not a Slash command
    object ErrorNotACommand : ParsedCommand()

    object ErrorEmptySlashCommand : ParsedCommand()

    // Unknown/Unsupported slash command
    class ErrorUnknownSlashCommand(val slashCommand: String) : ParsedCommand()

    // A slash command is detected, but there is an error
    class ErrorSyntax(val command: Command) : ParsedCommand()

    // Valid commands:

    class SendPlainText(val message: CharSequence) : ParsedCommand()
//    class SendEmote(val message: CharSequence) : ParsedCommand()
//    class SendRainbow(val message: CharSequence) : ParsedCommand()
//    class SendRainbowEmote(val message: CharSequence) : ParsedCommand()
    class BanUser(val userId: String, val reason: String?) : ParsedCommand()
    class UnbanUser(val userId: String, val reason: String?) : ParsedCommand()
    class SetUserPowerLevel(val userId: String, val powerLevel: Int?) : ParsedCommand()
    class Invite(val userId: String, val reason: String?) : ParsedCommand()
    class Invite3Pid(val threePid: ThreePid) : ParsedCommand()
    class JoinRoom(val roomAlias: String, val reason: String?) : ParsedCommand()
//    class PartRoom(val roomAlias: String, val reason: String?) : ParsedCommand()
    class ChangeTopic(val topic: String) : ParsedCommand()
    class KickUser(val userId: String, val reason: String?) : ParsedCommand()
    class ChangeDisplayName(val displayName: String) : ParsedCommand()
    class SetMarkdown(val enable: Boolean) : ParsedCommand()
//    object ClearScalarToken : ParsedCommand()
//    class SendSpoiler(val message: String) : ParsedCommand()
//    class SendShrug(val message: CharSequence) : ParsedCommand()
//    class VerifyUser(val userId: String) : ParsedCommand()
//    class SendPoll(val question: String, val options: List<String>) : ParsedCommand()
    object DiscardSession: ParsedCommand()
}

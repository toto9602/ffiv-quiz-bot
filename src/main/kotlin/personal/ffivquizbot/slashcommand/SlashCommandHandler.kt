package personal.ffivquizbot.slashcommand

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface SlashCommandHandler {
    val command: SlashCommands

    fun handleCommand(event: SlashCommandInteractionEvent?)
}
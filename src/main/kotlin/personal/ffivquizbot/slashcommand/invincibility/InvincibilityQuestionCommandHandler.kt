package personal.ffivquizbot.slashcommand.invincibility

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Service
import personal.ffivquizbot.eventwaiter.EventWaiterProvider
import personal.ffivquizbot.slashcommand.BaseSlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommands

@Service
class InvincibilityQuestionCommandHandler(
    private val eventWaiterProvider: EventWaiterProvider,
): SlashCommandHandler, BaseSlashCommandHandler()  {
    override val command = SlashCommands.INVINCIBILITY

    override fun handleCommand(event: SlashCommandInteractionEvent?) {
        if (event == null) return;


    }
}
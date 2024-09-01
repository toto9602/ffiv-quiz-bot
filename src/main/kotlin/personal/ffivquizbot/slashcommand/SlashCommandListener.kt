package personal.ffivquizbot.slashcommand

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.internal.immutableListOf
import org.springframework.stereotype.Service
import personal.ffivquizbot.slash_command.astrologian_skills.AstrologianSkillQuestionCommandHandler
import personal.ffivquizbot.slash_command.introduction.IntroductionCommandHandler
import personal.ffivquizbot.slash_command.job_question.JobQuestionCommandHandler

@Service
class SlashCommandListener(
    private val jobQuestionCommandHandler: JobQuestionCommandHandler,
    private val introductionCommandHandler:IntroductionCommandHandler,
    private val astrologianSkillQuestionCommandHandler: AstrologianSkillQuestionCommandHandler,
) :ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val name = event.name

       immutableListOf<SlashCommandHandler>(jobQuestionCommandHandler, introductionCommandHandler, astrologianSkillQuestionCommandHandler)

    }

}
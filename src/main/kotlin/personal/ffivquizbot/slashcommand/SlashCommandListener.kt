package personal.ffivquizbot.slashcommand

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.internal.immutableListOf
import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.astrologianskills.AstrologianSkillQuestionCommandHandler
import personal.ffivquizbot.slashcommand.introduction.IntroductionCommandHandler
import personal.ffivquizbot.slashcommand.jobquestion.JobQuestionCommandHandler

@Service
class SlashCommandListener(
    private val jobQuestionCommandHandler: JobQuestionCommandHandler,
    private val introductionCommandHandler:IntroductionCommandHandler,
    private val astrologianSkillQuestionCommandHandler: AstrologianSkillQuestionCommandHandler,
) :ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val name = event.name

        val handler = immutableListOf<SlashCommandHandler>(
            jobQuestionCommandHandler,
            introductionCommandHandler,
            astrologianSkillQuestionCommandHandler
        ).firstOrNull { handler -> handler.command.toString() == name }

        if (handler == null) {
            throw IllegalArgumentException("지원하지 않는 명령어가 입력되었습니다, ${name}")
        }

        handler.handleCommand(event)
    }
}
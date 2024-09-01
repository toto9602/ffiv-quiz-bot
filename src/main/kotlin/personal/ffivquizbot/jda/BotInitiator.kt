package personal.ffivquizbot.jda

import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.event_waiter.EventWaiterProvider
import personal.ffivquizbot.slash_command.SlashCommandListener
import personal.ffivquizbot.slash_command.SlashCommands
import java.util.*

@Service
@Slf4j
class BotInitiator(
    @Value("\${discord.botToken}")
     private val botToken:String,
    private val slashCommandListener: SlashCommandListener,
    private val eventWaiterProvider: EventWaiterProvider,
) {

    private val log = LoggerFactory.getLogger(BotInitiator::class.java)

    @PostConstruct
    fun runBot() {
        try {
            val jda = buildJDA()
            log.info("봇을 실행합니다, jda status = {}", jda.status)

            val commands = jda.updateCommands()

            commands
                .addCommands(
                    Commands.slash(SlashCommands.JOB_QUESTION.commandName, SlashCommands.JOB_QUESTION.description)
                        .addOptions(SlashCommands.JOB_QUESTION.options)

                ).addCommands(
                    Commands.slash(SlashCommands.INTRODUCTION.commandName, SlashCommands.INTRODUCTION.description)
                )
                .addCommands(
                    Commands.slash(SlashCommands.ASTROLOGIAN.commandName, SlashCommands.ASTROLOGIAN.description)
                        .addOptions(SlashCommands.ASTROLOGIAN.options)
                )
                .queue()
            log.info("Command 추가 완료!")
        } catch (e: Exception) {
            log.error("봇 실행 과정에서 오류가 발생했습니다.")
            e.printStackTrace()
        }
    }

    private fun buildJDA(): JDA {
        return JDABuilder.create(
            botToken,
            EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
        )
            .addEventListeners(slashCommandListener)
            .addEventListeners(eventWaiterProvider.eventWaiter)
            .build()
            .awaitReady()
    }
}

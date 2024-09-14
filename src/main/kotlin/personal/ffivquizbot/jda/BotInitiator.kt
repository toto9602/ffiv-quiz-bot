package personal.ffivquizbot.jda

import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.eventwaiter.EventWaiterProvider
import personal.ffivquizbot.slashcommand.SlashCommandListener
import personal.ffivquizbot.slashcommand.SlashCommands
import java.util.*

@Service
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

            val customCommands = SlashCommands.entries.toTypedArray()

            customCommands.forEach { command ->
                val commandToAdd = Commands.slash(command.commandName, command.description)

                if (command.optionList != null) {
                    command.optionList.forEach { option -> commandToAdd.addOptions(option) }
                }

                commands.addCommands(commandToAdd)
            }

            log.info("Command 추가 완료!")
        } catch (e: Exception) {
            log.error("봇 실행 과정에서 오류가 발생했습니다.")
            e.printStackTrace()
        }
    }

    private fun buildJDA(): JDA {
        return JDABuilder.create(
            botToken,
            EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT)
        )
            .addEventListeners(slashCommandListener)
            .addEventListeners(eventWaiterProvider.eventWaiter)
            .build()
            .awaitReady()
    }
}

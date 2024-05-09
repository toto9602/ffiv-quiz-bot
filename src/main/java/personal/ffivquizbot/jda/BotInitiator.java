package personal.ffivquizbot.jda;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.event_waiter.EventWaiterProvider;
import personal.ffivquizbot.slash_command.SlashCommandListener;
import personal.ffivquizbot.slash_command.SlashCommands;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotInitiator {

    @Value("${discord.botToken}")
    private String botToken;

    private final SlashCommandListener slashCommandListener;
    private final EventWaiterProvider eventWaiterProvider;


    @PostConstruct
    public void runBot()  {
        try {
            JDA jda = buildJDA();
            log.info("봇을 실행합니다, jda status = {}", jda.getStatus());

            CommandListUpdateAction commands = jda.updateCommands();

            commands.addCommands(
                    Commands.slash(SlashCommands.JOB_QUESTION.getCommandName(), SlashCommands.JOB_QUESTION.getDescription())
                            .addOptions(SlashCommands.JOB_QUESTION.getOptions())

            ).addCommands(
                    Commands.slash(SlashCommands.INTRODUCTION.getCommandName(), SlashCommands.INTRODUCTION.getDescription())
            );

            commands.queue();
            log.info("Command 추가 완료!");
        } catch (Exception e) {
            log.error("봇 실행 과정에서 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    private JDA buildJDA() throws LoginException, InterruptedException  {
            return JDABuilder.create(botToken, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .addEventListeners(slashCommandListener)
                    .addEventListeners(eventWaiterProvider.getEventWaiter())
                    .build()
                    .awaitReady();
    }

}

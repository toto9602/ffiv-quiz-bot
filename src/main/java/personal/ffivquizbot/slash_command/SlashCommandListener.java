package personal.ffivquizbot.slash_command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import personal.ffivquizbot.slash_command.introduction.IntroductionCommandHandler;
import personal.ffivquizbot.slash_command.job_question.JobQuestionCommandHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlashCommandListener extends ListenerAdapter {

    private final JobQuestionCommandHandler jobQuestionCommandHandler;
    private final IntroductionCommandHandler introductionCommandHandler;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String name = event.getName();

        log.info("/ 커맨드를 실행합니다., 커맨드 이름 = {}", name);

        if (name.equals(SlashCommands.JOB_QUESTION.getCommandName())) {
            jobQuestionCommandHandler.handleCommand(event);
        } else if (name.equals(SlashCommands.INTRODUCTION.getCommandName())) {
            introductionCommandHandler.handleCommand(event);
        } else {
            log.error("유효하지 않은 커맨드입니다, 커맨드 이름 = {}", name);
            throw new IllegalArgumentException(name);
        }
    }
}

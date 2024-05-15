package personal.ffivquizbot.slash_command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.slash_command.introduction.IntroductionCommandHandler;
import personal.ffivquizbot.slash_command.job_question.JobQuestionCommandHandler;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlashCommandListener extends ListenerAdapter {

    private final JobQuestionCommandHandler jobQuestionCommandHandler;
    private final IntroductionCommandHandler introductionCommandHandler;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String name = event.getName();

        SlashCommandHandler targetCommandHandler = Arrays.asList(jobQuestionCommandHandler, introductionCommandHandler).stream()
                .filter(slashCommandHandler -> slashCommandHandler.getCommand().getCommandName().equals(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        log.info("/ 커맨드를 실행합니다., 커맨드 이름 = {}", name);
        targetCommandHandler.handleCommand(event);
    }
}

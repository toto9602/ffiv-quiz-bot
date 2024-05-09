package personal.ffivquizbot.slash_command.introduction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.slash_command.SlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommands;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntroductionCommandHandler implements SlashCommandHandler {
    public SlashCommands getCommand() {
        return SlashCommands.INTRODUCTION;
    }

    public void handleCommand(SlashCommandInteractionEvent event) {
        event.reply("""
                안녕하세요! 파판14 관련 문제를 내 주는 봇입니다! 
                현재 지원하는 명령어를 확인해 주세요! :)
                0. /설명 : 저에 대해 다시 설명해 드려요!
                1. /잡 : 잡 문양을 보고, 어떤 잡인지 맞혀 봐요! (for. 뉴비 점성술사)
                """).queue();
    }
}

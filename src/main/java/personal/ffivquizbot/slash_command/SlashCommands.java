package personal.ffivquizbot.slash_command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;


@RequiredArgsConstructor
@Getter
public enum SlashCommands {
    JOB_QUESTION("잡", "파판14의 잡 문양 문제를 출제합니다.", true, new OptionData(OptionType.INTEGER, "question_count", "문제 개수를 설정합니다. (기본 : 19 종류 전체)").setRequiredRange(1, 19)),
    INTRODUCTION("설명", "사용 가능한 명령어를 안내합니다.", false, null);

    private final String commandName;
    private final String description;
    private final Boolean hasOptions;
    private final OptionData options;
}

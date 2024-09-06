package personal.ffivquizbot.slashcommand

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import okhttp3.internal.immutableListOf

enum class SlashCommands(
    val commandName:String,
    val description:String,
    val hasOptions:Boolean,
    val optionList:List<OptionData>?,
){
    INTRODUCTION("설명", "사용 가능한 명령어를 안내합니다.", false, null),
    JOB_QUESTION("잡", "파판14의 잡 문양 문제를 출제합니다.", true, immutableListOf(OptionData(OptionType.INTEGER, "question_count", "문제 개수를 설정합니다. (기본 : 19 종류 전체)").setRequiredRange(1, 19))),
    ASTROLOGIAN("점지", "점지 스킬의 카드 관련 문제를 출제합니다.", true, immutableListOf( OptionData(OptionType.INTEGER, "question_count", "문제 개수를 설정합니다. (기본 : 6 종류 전체").setRequiredRange(1,6)));
}

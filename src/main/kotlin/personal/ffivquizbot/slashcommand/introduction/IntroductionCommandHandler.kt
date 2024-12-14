package personal.ffivquizbot.slashcommand.introduction

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Service
import personal.ffivquizbot.emojis.Emojis
import personal.ffivquizbot.slashcommand.SlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommands

@Service
class IntroductionCommandHandler(
    override val command: SlashCommands = SlashCommands.INTRODUCTION
) : SlashCommandHandler {

    override fun handleCommand(event: SlashCommandInteractionEvent?) {
        event?.reply("안녕하세요! " + Emojis.HAND.emojiString + " " + Emojis.HAND.emojiString + "\n"
                + "파판 챗봇 힐꺼비입니다! " + Emojis.BOW.emojiString +"\n\n"
                + "현재 지원하는 명령어를 확인해 주세요! " + Emojis.SMILEY.emojiString + " " + Emojis.SMILEY.emojiString + "\n\n"
                + "0. /설명 : 봇에 대해 다시 설명해 드려요!" + "\n"
                + "1. /잡 : 잡 문양을 보고, 어떤 잡인지 맞혀 봐요!" + "\n"
                + "2. /점지 : '점지' 스킬로 획득 가능한 카드를 보고, 카드에 맞는 효과를 골라 봐요!" + "\n\n"
                + "팁) '그만' 을 입력하시면 풀고 있는 문제를 즉시 중단할 수 있어요!")
            ?.queue()

    }
}
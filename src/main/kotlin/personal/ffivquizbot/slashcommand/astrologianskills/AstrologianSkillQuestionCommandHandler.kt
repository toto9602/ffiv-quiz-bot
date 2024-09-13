package personal.ffivquizbot.slashcommand.astrologianskills

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import personal.ffivquizbot.emojis.Emojis
import personal.ffivquizbot.eventwaiter.EventWaiterProvider
import personal.ffivquizbot.slashcommand.SlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommands
import personal.ffivquizbot.slashcommand.astrologianskills.enums.DrawSkills
import personal.ffivquizbot.slashcommand.questionutils.getCorrectRate
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

@Service
class AstrologianSkillQuestionCommandHandler(
    private val skillQuestionProvider: SkillQuestionProvider,
    private val skillIconFilePathBuilder: AstrologianSkillIconFilePathBuilder,
    private val eventWaiterProvider: EventWaiterProvider,
) : SlashCommandHandler {
    override val command = SlashCommands.ASTROLOGIAN

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handleCommand(event: SlashCommandInteractionEvent?) {
        if (event == null) return;

        val questionCountOption = event.getOption("question_count")

        val questionCount = questionCountOption?.asInt ?: 6
        val randomSkillList = this.skillQuestionProvider.getRandomSkillList(questionCount)
        val jobQuestionReply = "[ 점지 ] 관련 문제를 ${questionCount}개 출제할게요!\n" +
                "5초 뒤 시작합니다! ${Emojis.ROCKET.emojiString} ${Emojis.ROCKET.emojiString}"

        event.reply(jobQuestionReply).queue()
        log.info("reply 전송 완료!")

        sleep(4000)

        this.processJobQuestion(
            targetChannel =  event.channel,
            targetUser =  event.user,
            drawSkills = randomSkillList,
            jobIndex = 0,
            total = questionCount,
            correctCount = 0
        )
    }

    private fun processJobQuestion(
        targetChannel: MessageChannel,
        targetUser: User,
        drawSkills: List<DrawSkills>,
        jobIndex: Int,
        total: Int,
        correctCount: Int
    ) {
        try {
            val drawSkillToAsk = drawSkills.get(jobIndex)

            val iconFilePath = skillIconFilePathBuilder.buildPath(drawSkillToAsk)

            val embed = EmbedBuilder()
                .setImage(iconFilePath)
                .setDescription(drawSkillToAsk.skillName)

            val questionDescription = "[ ${jobIndex + 1}번 문제 ] "

            val randomListWithAnswer = this.getRandomListWithAnswer(drawSkillToAsk)

            val builder = StringSelectMenu.create("Select")
                .setPlaceholder("높은 효과를 주는 직업군 / 맞는 징조를 골라주세요!")

            for (skill in randomListWithAnswer) {
                builder.addOption(toAnswer(skill), toAnswer(skill))
            }

            val menu = builder.addOption("문제 풀이를 중단합니다.", "문제 풀이를 중단합니다.")
                .build()

            targetChannel.sendMessage(questionDescription)
                .setActionRow(menu)
                .setEmbeds(embed.build())
                .queue { message -> eventWaiterProvider.eventWaiter.waitForEvent(
                        GenericSelectMenuInteractionEvent::class.java,
                         { event -> !event.user.isBot && event.user == targetUser
                    },
                            { event ->

                            val selectedOptions = event.interaction.values.first()


                            if (selectedOptions == "문제 풀이를 중단합니다") {
                                log.info("중단 명령어 확인, 문제 출제를 중단합니다...")

                                val solvedCount = jobIndex;
                                event.reply(
                                    "${Emojis.STOP.emojiString} 문제 풀이를 중단합니다! ${Emojis.STOP.emojiString}\n" +
                                            "총 풀이한 문제 수 : ${solvedCount} / ${total}, 정답률 : ${
                                                getCorrectRate(
                                                    correctCount,
                                                    total
                                                )
                                            }"
                                )
                                    .queue()


                            } else if (selectedOptions == toAnswer(drawSkillToAsk)) {
                                log.info("정답! 정답 count를 높이고, 메시지를 전송합니다")

                                event.reply("정답입니다! ${Emojis.CLAP.emojiString} ${Emojis.CLAP.emojiString}")
                                    .queue()

                                val updatedCorrectCount = correctCount + 1

                                if (!this.isLast(jobIndex, total)) {
                                    log.info("문제가 남았습니다! 다음 문제를 출제합니다.")
                                    val updatedJobIndex = jobIndex + 1

                                    this.processJobQuestion(
                                        targetChannel = targetChannel,
                                        targetUser = targetUser,
                                        drawSkills = drawSkills,
                                        jobIndex = updatedJobIndex,
                                        total = total,
                                        correctCount = updatedCorrectCount
                                    )
                                } else {
                                    // 출제 끝
                                    targetChannel.sendMessage(
                                        "\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString} \n" +
                                                "정답률 : ${getCorrectRate(updatedCorrectCount, total)}"
                                    )
                                        .queue()
                                }
                            } else {
                                // 오답
                                event.reply(
                                    "땡! ${Emojis.WOMAN_X.emojiString} ${Emojis.WOMAN_X.emojiString}\n " +
                                            "정답은 [ ${drawSkillToAsk.target.name} ] 입니다!"
                                )
                                    .queue()

                                if (!this.isLast(jobIndex, total)) {
                                    log.info("문제가 남았습니다! 다음 문제를 출제합니다.")
                                    val updatedJobIndex = jobIndex + 1

                                    this.processJobQuestion(
                                        targetChannel = targetChannel,
                                        targetUser = targetUser,
                                        drawSkills = drawSkills,
                                        jobIndex = updatedJobIndex,
                                        total = total,
                                        correctCount = correctCount
                                    )

                                } else {
                                    // 출제 끝
                                    targetChannel.sendMessage(
                                        "\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString}\n" +
                                                "정답률 : ${getCorrectRate(correctCount, total)}"
                                    )
                                        .queue()
                                }
                            }
                        },
                    20,
                    TimeUnit.SECONDS,
                    {
                        log.info("TIMEOUT! ${drawSkillToAsk.skillName}" )

                    targetChannel.sendMessage("${Emojis.CLOCK.emojiString} 시간 초과입니다!\n" +
                            "${Emojis.CLOCK.emojiString} 정답은 [ ${drawSkillToAsk.target.name} ] ! :)")
                        .queue()

                        if (this.isLast(jobIndex, total)) {
                            targetChannel.sendMessage("\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString}\n" +
                                    "정답률 : ${getCorrectRate(correctCount, total)}")
                                .queue()
                        } else {
                            val updatedJobIndex = jobIndex + 1
                            this.processJobQuestion(
                                targetChannel = targetChannel,
                                targetUser = targetUser,
                                drawSkills = drawSkills,
                                jobIndex = updatedJobIndex,
                                total = total,
                                correctCount
                            )
                        }

                })
                }
        } catch (e: Exception) {
            println(e.printStackTrace())
            targetChannel
                .sendMessage("메시지 전송 과정에서 오류가 발생했습니다.. ${Emojis.SMILING_TEAR.emojiString} 개발자를 추궁해 주세요...")
                .queue()

        }
    }

    private fun toAnswer(skill: DrawSkills):String {
        return "${skill.target.targetDescription} / ${skill.sign.signName}"
    }

    private fun isLast(jobIndex: Int, total: Int): Boolean = (jobIndex + 1) == total

    private fun sleep(milliseconds: Int) {
        try {
            Thread.sleep(milliseconds.toLong())
        } catch (e: Exception) {
            log.error("Thread sleep 중 예외 발생", e)
            e.printStackTrace()
        }
    }

    private fun getRandomListWithAnswer(answer: DrawSkills):List<DrawSkills> {
        val drawSkills = DrawSkills.values().toMutableList()
        drawSkills.shuffle()

        val optionCandidates = drawSkills.subList(0, 4).toTypedArray()

        val answerIdx = optionCandidates.indexOfFirst { it.skillName == answer.skillName }

        return if (answerIdx != -1) {
            optionCandidates.toList()
        } else {
            val newOptionCandidates = optionCandidates.take(3).toMutableList()
            newOptionCandidates.add(answer)
            newOptionCandidates.shuffle()

            newOptionCandidates
        }
    }
}
package personal.ffivquizbot.slashcommand.jobquestion

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import personal.ffivquizbot.emojis.Emojis
import personal.ffivquizbot.eventwaiter.EventWaiterProvider
import personal.ffivquizbot.slashcommand.BaseSlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommandHandler
import personal.ffivquizbot.slashcommand.SlashCommands
import personal.ffivquizbot.slashcommand.jobquestion.enums.FFIVJobs
import personal.ffivquizbot.slashcommand.questionutils.getCorrectRate
import java.util.concurrent.TimeUnit

@Service
class JobQuestionCommandHandler(
    private val jobQuestionProvider:JobQuestionProvider,
    private val jobIconFilePathBuilder: JobIconFilePathBuilder,
    private val eventWaiterProvider:EventWaiterProvider,
) : SlashCommandHandler, BaseSlashCommandHandler() {
    override val command = SlashCommands.JOB_QUESTION;

    private val log = LoggerFactory.getLogger(JobQuestionCommandHandler::class.java)

    private val TOTAL_JOB_QUESTION_COUNT = 19;

    override fun handleCommand(event: SlashCommandInteractionEvent?) {
        if (event == null) return;

        val questionCountOption = event.getOption("question_count")

        val questionCount = questionCountOption?.asInt ?: this.TOTAL_JOB_QUESTION_COUNT

        val randomJobList = jobQuestionProvider.getRandomJobList(questionCount)

        val jobQuestionReply =
            "[ 잡 ] 관련 문제를 ${questionCount}개 출제할게요!\n 5초 뒤 시작합니다! ${Emojis.ROCKET.emojiString} ${Emojis.ROCKET.emojiString}"

        event.reply(jobQuestionReply).queue()

        log.info("reply 전송 완료!")

        processJobQuestion(
            targetChannel = event.channel,
            targetUser = event.user,
            jobs = randomJobList,
            jobIndex = 0,
            total = questionCount,
            correctCount = 0
        )
    }

    private fun processJobQuestion(
        targetChannel: MessageChannel,
        targetUser: User,
        jobs: List<FFIVJobs>,
        jobIndex: Int,
        total: Int,
        correctCount: Int
    ) {
        try {
            val jobToAsk = jobs.get(jobIndex)

            val iconFilePath = jobIconFilePathBuilder.buildPath(jobToAsk)

            val embed = EmbedBuilder()
                .setImage(iconFilePath)
                .setDescription("스킬 아이콘")

            val questionDescription = "[ ${(jobIndex + 1)}번 문제 ]"

            targetChannel.sendMessage(questionDescription)
                .setEmbeds(embed.build())
                .queue { message ->
                    eventWaiterProvider.eventWaiter.waitForEvent(
                        MessageReceivedEvent::class.java,
                        { event: MessageReceivedEvent ->
                            event.author.isBot == false && event.author == targetUser
                        },
                        { event: MessageReceivedEvent ->
                                val authorMsg = event.message.contentRaw
                                if (authorMsg == super.circuitBreakCommand) {
                                    log.info("중단 명령어 확인, 문제 출제를 중단합니다..")
                                    val solvedCount = jobIndex
                                    targetChannel.sendMessage(
                                        "${Emojis.STOP.emojiString} 문제 출제를 중단합니다! ${Emojis.STOP.emojiString}\n" +
                                                "총 풀이한 문제 수 : ${solvedCount} / ${total}, 정답률 : ${
                                                    getCorrectRate(
                                                        correctCount,
                                                        total
                                                    )
                                                }"
                                    )
                                        .queue()
                                } else {
                                    // 정상 케이스
                                    if (authorMsg == jobToAsk.jobName) { // 정답
                                        log.info("정답! 정답 count를 높이고, 메시지를 전송합니다.")
                                        targetChannel
                                            .sendMessage("정답입니다!! ${Emojis.CLAP.emojiString} ${Emojis.CLAP.emojiString}")
                                            .queue()

                                        val updatedCorrectCount = correctCount + 1

                                        if (!this.isLast(jobIndex = jobIndex, total = total)) {
                                            log.info("문제가 남았습니다! 다음 문제를 출제합니다.")
                                            val updatedJobIndex = jobIndex + 1

                                            this.processJobQuestion(
                                                targetChannel = targetChannel,
                                                targetUser = targetUser,
                                                jobs = jobs,
                                                jobIndex = updatedJobIndex,
                                                total = total,
                                                correctCount = updatedCorrectCount
                                            )
                                        } else {
                                            // 출제 끝
                                            targetChannel
                                                .sendMessage(
                                                    "- - - - - - - - - - - - - - - - -\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString}\n 정답률 : ${
                                                        getCorrectRate(
                                                            updatedCorrectCount,
                                                            total
                                                        )
                                                    }"
                                                )
                                                .queue()
                                        }

                                    } else {
                                        // 오답
                                        targetChannel.sendMessage("땡~! ${Emojis.WOMAN_X.emojiString} ${Emojis.WOMAN_X.emojiString} 정답은 [ ${jobToAsk.jobName} ] 입니다!").queue()

                                        if (!isLast(jobIndex, total)) {
                                            log.info("문제가 남았습니다! 다음 문제를 출제합니다.")
                                            val updatedJobIndex = jobIndex + 1

                                            this.processJobQuestion(
                                                targetChannel = targetChannel,
                                                targetUser = targetUser,
                                                jobs = jobs,
                                                jobIndex = updatedJobIndex,
                                                total = total,
                                                correctCount = correctCount
                                            )

                                        } else {
                                            // 출제 끝
                                            targetChannel
                                                .sendMessage(
                                                    "- - - - - - - - - - - - - - - - -\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString}\n 정답률 : ${
                                                        getCorrectRate(
                                                            correctCount,
                                                            total
                                                        )
                                                    }"
                                                )
                                                .queue()
                                        }
                                    }
                                }
                        },
                        10,
                        TimeUnit.SECONDS,
                            {
                                log.info("TIMEOUT! ${jobToAsk.jobName}")

                                targetChannel
                                    .sendMessage("${Emojis.CLOCK.emojiString} 시간 초과입니다! ${Emojis.CLOCK.emojiString} 정답은 [ ${jobToAsk.jobName} ] ! :)")
                                    .queue()

                                // 다음 문제 출제
                                if (!this.isLast(jobIndex, total)) {
                                    val updatedJobIndex = jobIndex + 1
                                    this.processJobQuestion(
                                        targetChannel = targetChannel,
                                        targetUser = targetUser,
                                        jobs = jobs,
                                        jobIndex = updatedJobIndex,
                                        total = total,
                                        correctCount = correctCount
                                    )
                                } else {
                                    targetChannel
                                        .sendMessage(
                                            "- - - - - - - - - - - - - - - - -\n\n ${Emojis.PARTYING_FACE.emojiString} 모든 문제가 끝났습니다! ${Emojis.PARTYING_FACE.emojiString}\n 정답률 : ${
                                                getCorrectRate(
                                                    correctCount,
                                                    total
                                                )
                                            }"
                                        )
                                        .queue()
                                }
                            }
                    )
                }

            sleep(1000)
        } catch (e: Exception) {
            targetChannel
                .sendMessage("메시지 전송 과정에서 오류가 발생했습니다.. ${Emojis.SMILING_TEAR.emojiString} 개발자를 추궁해 주세요...")
                .queue()
            e.printStackTrace()
        }
    }

    private fun isLast(jobIndex: Int, total: Int):Boolean {
        return (jobIndex + 1) == total
    }

    private fun sleep(milliseconds: Int) {
        try {
            Thread.sleep(milliseconds.toLong())
        } catch (e: Exception) {
            log.error("Thread sleep 중 예외 발생", e)
            e.printStackTrace()
        }
    }
}

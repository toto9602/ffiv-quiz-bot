package personal.ffivquizbot.slash_command.job_question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.emojis.Emojis;
import personal.ffivquizbot.event_waiter.EventWaiterProvider;
import personal.ffivquizbot.slash_command.SlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobQuestionCommandHandler implements SlashCommandHandler {

    private final JobQuestionProvider jobQuestionProvider;

    private final EventWaiterProvider eventWaiterProvider;

    public SlashCommands getCommand() {
        return SlashCommands.JOB_QUESTION;
    }

    public void handleCommand(SlashCommandInteractionEvent event) {
        OptionMapping questionCountOption = event.getOption("question_count");
        int questionCount = questionCountOption != null ? questionCountOption.getAsInt() : 19;

        ArrayList<FFIVJobs> randomJobList = jobQuestionProvider.getRandomJobList(questionCount);

        String jobQuestionReply = "[ 잡 ] 관련 문제를 " +
                questionCount +
                "개 출제할게요!\n 5초 뒤 시작합니다!" + Emojis.ROCKET.getEmojiString() + " " + Emojis.ROCKET.getEmojiString();


        event.reply(jobQuestionReply).queue();
        log.info("reply 전송 완료!");

        sleep(4000);

        // TODO : 삭제
        for (FFIVJobs job : randomJobList) {
            System.out.println(job);
        }

        processJobQuestion(event.getChannel(), event.getUser(), randomJobList, 0, questionCount, 0);
    }

    private void processJobQuestion(MessageChannel targetChannel, User targetUser, ArrayList<FFIVJobs> jobs, int jobIndex, int total, int correctCount) {
        try {
            FFIVJobs jobToAsk = jobs.get(jobIndex);

            EmbedBuilder embed = new EmbedBuilder()
                    .setImage(jobToAsk.getJobIconUrl())
                    .setDescription("잡 문양");

            System.out.println("파일 업로드 객체 생성");

            String questionDescription = "[ " + Integer.valueOf(jobIndex+1).toString() + "번 문제" + " ]";

            targetChannel.sendMessage(questionDescription)
                    .setEmbeds(embed.build())
                    .queue(
                            message -> eventWaiterProvider.getEventWaiter().waitForEvent(
                                    MessageReceivedEvent.class,
                                    e -> { // Condition
                                        if (e.getAuthor().isBot()) {
                                            return false;
                                        }

                                        return e.getAuthor().equals(targetUser);
                                    },
                                    e -> { // Action
                                        String authorMessage = e.getMessage().getContentRaw();
                                        if (authorMessage.equals(jobToAsk.getJobName())) { // 정답
                                            log.info("정답! 정답 count를 높이고, 메시지를 전송합니다.");

                                            targetChannel.sendMessage("정답입니다!!! " + Emojis.CLAP.getEmojiString() + " "  + Emojis.CLAP.getEmojiString()).queue();

                                            int updatedCorrectCount = correctCount + 1;

                                            if (!isLast(jobIndex, total)) {
                                                log.info("문제가 남았습니다! 다음 문제를 출제합니다.");
                                                int updatedJobIndex = jobIndex + 1;

                                                processJobQuestion(targetChannel, targetUser, jobs, updatedJobIndex, total, updatedCorrectCount);
                                            } else {
                                                // 출제 끝
                                                targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(updatedCorrectCount, total)).queue();
                                            }
                                        } else { // 오답
                                            targetChannel.sendMessage("땡~! " + Emojis.WOMAN_X.getEmojiString() + " " + Emojis.WOMAN_X.getEmojiString() + " " + "정답은 [ " + jobToAsk.getJobName() + " ] 입니다!").queue();

                                            if (!isLast(jobIndex, total)) {
                                                log.info("문제가 남았습니다! 다음 문제를 출제합니다.");
                                                int updatedJobIndex = jobIndex + 1;

                                                processJobQuestion(targetChannel, targetUser, jobs, updatedJobIndex, total, correctCount);
                                            } else {
                                                // 출제 끝
                                                targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(correctCount, total)).queue();
                                            }
                                        }
                                        },
                                    10, TimeUnit.SECONDS,
                                    () -> { // Timeout Action
                                        log.info("TIMEOUT!" + jobToAsk.getJobName());

                                        targetChannel.sendMessage(Emojis.CLOCK.getEmojiString() + " " + "시간 초과입니다!" + Emojis.CLOCK.getEmojiString() + " " + "정답은 [ " + jobToAsk.getJobName() +" ] ! :)").queue();
                                        if (isLast(jobIndex, total)) {
                                            System.out.println("출제를 종료합니다...");
                                            return;
                                        }

                                        int updatedJobIndex = jobIndex + 1;
                                        processJobQuestion(targetChannel, targetUser, jobs, updatedJobIndex, total, correctCount);
                                    }
                            )
                    );
            sleep(1000);
            System.out.println("다음 문제로 진입!!!");
        } catch (Exception e) {
            targetChannel.sendMessage("메시지 전송 과정에서 오류가 발생했습니다.." + Emojis.SMILING_TEAR.getEmojiString() + " " + "개발자를 추궁해 주세요...").queue();
            e.printStackTrace();
        }
    }

    private boolean isLast(int jobIndex, int total) {
        return (jobIndex + 1) == total;
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Thread sleep 중 예외 발생", e);
            e.printStackTrace();
        }
    }

    private String getCorrectRate(int correctCount, int total) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance();

        percentFormat.setMinimumFractionDigits(0);
        percentFormat.setMaximumFractionDigits(2);
        log.info("correctCount = {}", correctCount);
        log.info("total = {}", total);

        double rate = (double) correctCount / (double) total;

        return percentFormat.format(rate);
    }

}

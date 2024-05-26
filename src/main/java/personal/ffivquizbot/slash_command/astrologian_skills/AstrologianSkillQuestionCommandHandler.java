package personal.ffivquizbot.slash_command.astrologian_skills;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.emojis.Emojis;
import personal.ffivquizbot.event_waiter.EventWaiterProvider;
import personal.ffivquizbot.slash_command.BaseSlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommands;
import personal.ffivquizbot.slash_command.astrologian_skills.enums.DrawSkills;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class AstrologianSkillQuestionCommandHandler  extends BaseSlashCommandHandler implements SlashCommandHandler {
    private final SkillQuestionProvider skillQuestionProvider;
    private final EventWaiterProvider eventWaiterProvider;

    public SlashCommands getCommand() {
        return SlashCommands.ASTROLOGIAN;
    }

    public void handleCommand(SlashCommandInteractionEvent event) {
        OptionMapping questionCountOption = event.getOption("question_count");

        int questionCount = questionCountOption != null ? questionCountOption.getAsInt() : 6;

        ArrayList<DrawSkills> randomJobList = skillQuestionProvider.getRandomSkillList(questionCount);

        String jobQuestionReply = "[ 점지 ] 관련 문제를 " +
                questionCount +
                "개 출제할게요!\n 5초 뒤 시작합니다!" + Emojis.ROCKET.getEmojiString() + " " + Emojis.ROCKET.getEmojiString();


        event.reply(jobQuestionReply).queue();
        log.info("reply 전송 완료!");

        sleep(4000);

        // TODO : 삭제
        for (DrawSkills job : randomJobList) {
            System.out.println(job);
        }

        processJobQuestion(event.getChannel(), event.getUser(), randomJobList, 0, questionCount, 0);
    }

    private void processJobQuestion(MessageChannel targetChannel, User targetUser, ArrayList<DrawSkills> drawSkills, int jobIndex, int total, int correctCount) {
        try {
            DrawSkills drawSkillToAsk = drawSkills.get(jobIndex);

            EmbedBuilder embed = new EmbedBuilder()
                    .setImage(drawSkillToAsk.getSkillIconUrl())
                    .setDescription("스킬 아이콘");

            System.out.println("파일 업로드 객체 생성");

            String questionDescription = "[ " + Integer.valueOf(jobIndex+1).toString() + "번 문제" + " ]";
            System.out.println("questionDescription = " + questionDescription);

            SelectMenu menu = SelectMenu.create("Select")
                    .setPlaceholder("높은 효과를 주는 직업군 / 맞는 징조를 골라주세요!")
                    .addOption("근딜 & 방어 / 해의 징조", "근딜 & 방어 / 해의 징조")
                    .addOption("원딜 & 회복 / 달의 징조", "원딜 & 회복 / 달의 징조")
                    .addOption("근딜 & 방어 / 별의 징조", "근딜 & 방어 / 별의 징조")
                    .addOption("근딜 & 방어 / 달의 징조", "근딜 & 방어 / 달의 징조")
                    .addOption("문제 풀이를 중단합니다", "문제 풀이를 중단합니다.")
                    .build();

            System.out.println("메시지를 전송합니다" + "[ " + Integer.valueOf(jobIndex+1).toString() + "번 문제" + " ]");

            targetChannel.sendMessage(questionDescription)
                    .setActionRow(menu)
                    .setEmbeds(embed.build())
                    .queue(
                            message -> eventWaiterProvider.getEventWaiter().waitForEvent(
                                    SelectMenuInteractionEvent.class,
                                    e -> { // Condition
                                        System.out.println(e);
                                        System.out.println("event의 조건을 조회합니다");
                                        System.out.println(e.getUser());

                                        if (e.getUser().isBot()) {
                                            return false;
                                        }
                                        return e.getUser().equals(targetUser);
                                    },
                                    e -> { // Action
                                        System.out.println("SelectMenuInteractionEvent를 수신했습니다" );
                                        String selected = e.getMessage().toString();
                                        System.out.println("selected" + selected);

                                        if (selected.equals(super.getCircuitBreakCommand())) {
                                            log.info("중단 명령어 확인, 문제 출제를 중단합니다...");

                                            int solvedCount = jobIndex;
                                            targetChannel.sendMessage(Emojis.STOP.getEmojiString() + " 문제 출제를 중단합니다! " + Emojis.STOP.getEmojiString() +
                                                            "\n총 풀이한 문제 수 : " + solvedCount + " / " + total + ", 정답률 : " + getCorrectRate(correctCount, total))
                                                    .queue();

                                            return;
                                        }

                                        if (selected.equals(drawSkillToAsk.getAnswer())) { // 정답
                                            log.info("정답! 정답 count를 높이고, 메시지를 전송합니다.");

                                            e.getTextChannel().sendMessage("정답입니다!!! " + Emojis.CLAP.getEmojiString() + " "  + Emojis.CLAP.getEmojiString()).queue();

                                            int updatedCorrectCount = correctCount + 1;

                                            if (!isLast(jobIndex, total)) {
                                                log.info("문제가 남았습니다! 다음 문제를 출제합니다.");
                                                int updatedJobIndex = jobIndex + 1;

                                                processJobQuestion(targetChannel, targetUser, drawSkills, updatedJobIndex, total, updatedCorrectCount);
                                            } else {
                                                // 출제 끝
                                                targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(updatedCorrectCount, total)).queue();
                                            }
                                        } else { // 오답
                                            e.getTextChannel().sendMessage("땡~! " + Emojis.WOMAN_X.getEmojiString() + " " + Emojis.WOMAN_X.getEmojiString() + " " + "정답은 [ " + drawSkillToAsk.getSkillName() + " ] 입니다!").queue();

                                            if (!isLast(jobIndex, total)) {
                                                log.info("문제가 남았습니다! 다음 문제를 출제합니다.");
                                                int updatedJobIndex = jobIndex + 1;

                                                processJobQuestion(targetChannel, targetUser, drawSkills, updatedJobIndex, total, correctCount);
                                            } else {
                                                // 출제 끝
                                                targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(correctCount, total)).queue();
                                            }
                                        }
                                    },
                                    10, TimeUnit.SECONDS,
                                    () -> { // Timeout Action
                                        log.info("TIMEOUT!" + drawSkillToAsk.getSkillName());

                                        targetChannel.sendMessage(Emojis.CLOCK.getEmojiString() + " " + "시간 초과입니다!" + Emojis.CLOCK.getEmojiString() + " " + "정답은 [ " + drawSkillToAsk.getAnswer() +" ] ! :)").queue();
                                        if (isLast(jobIndex, total)) {
                                            System.out.println("출제를 종료합니다...");
                                            return;
                                        }

                                        int updatedJobIndex = jobIndex + 1;
                                        processJobQuestion(targetChannel, targetUser, drawSkills, updatedJobIndex, total, correctCount);
                                    }
                            )
                    );
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

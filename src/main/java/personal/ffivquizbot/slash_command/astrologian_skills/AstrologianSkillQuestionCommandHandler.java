package personal.ffivquizbot.slash_command.astrologian_skills;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.springframework.stereotype.Service;
import personal.ffivquizbot.emojis.Emojis;
import personal.ffivquizbot.event_waiter.EventWaiterProvider;
import personal.ffivquizbot.slash_command.BaseSlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommandHandler;
import personal.ffivquizbot.slash_command.SlashCommands;
import personal.ffivquizbot.slash_command.astrologian_skills.enums.DrawSkills;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


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

        processJobQuestion(event.getChannel(), event.getUser(), randomJobList, 0, questionCount, 0);
    }

    private void processJobQuestion(MessageChannel targetChannel, User targetUser, ArrayList<DrawSkills> drawSkills, int jobIndex, int total, int correctCount) {
        try {
            DrawSkills drawSkillToAsk = drawSkills.get(jobIndex);

            EmbedBuilder embed = new EmbedBuilder()
                    .setImage(drawSkillToAsk.getSkillIconUrl())
                    .setDescription(drawSkillToAsk.getSkillName());

            String questionDescription = "[ " + Integer.valueOf(jobIndex+1).toString() + "번 문제" + " ]";

            List<DrawSkills> randomListWithAnswer = getRandomListWithAnswer(drawSkillToAsk);
            SelectMenu.Builder builder = SelectMenu.create("Select")
                    .setPlaceholder("높은 효과를 주는 직업군 / 맞는 징조를 골라주세요!");

            for (DrawSkills skill : randomListWithAnswer) {
                builder.addOption(skill.getAnswer(), skill.getAnswer());
            }

            SelectMenu menu = builder.addOption("문제 풀이를 중단합니다.", "문제 풀이를 중단합니다.")
                    .build();

            targetChannel.sendMessage(questionDescription)
                    .setActionRow(menu)
                    .setEmbeds(embed.build())
                    .queue(
                            message -> eventWaiterProvider.getEventWaiter().waitForEvent(
                                    SelectMenuInteractionEvent.class,
                                    e -> { // Condition
                                        if (e.getUser().isBot()) {
                                            return false;
                                        }
                                        return e.getUser().equals(targetUser);
                                    },
                                    e -> { // Action
                                        List<SelectOption> selectedOptions = e.getSelectedOptions();
                                        String selected = selectedOptions.get(0).getValue();

                                        if (selected.equals("문제 풀이를 중단합니다")) {
                                            log.info("중단 명령어 확인, 문제 출제를 중단합니다...");

                                            int solvedCount = jobIndex;
                                            e.reply(Emojis.STOP.getEmojiString() + " 문제 출제를 중단합니다! " + Emojis.STOP.getEmojiString() +
                                                            "\n총 풀이한 문제 수 : " + solvedCount + " / " + total + ", 정답률 : " + getCorrectRate(correctCount, total))
                                                    .queue();

                                            return;
                                        }

                                        if (selected.equals(drawSkillToAsk.getAnswer())) { // 정답
                                            log.info("정답! 정답 count를 높이고, 메시지를 전송합니다.");


                                            e.reply("정답입니다!!! " + Emojis.CLAP.getEmojiString() + " "  + Emojis.CLAP.getEmojiString()).queue();

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
                                            e.reply("땡! " + Emojis.WOMAN_X.getEmojiString() + " " + Emojis.WOMAN_X.getEmojiString() + " " + "\n정답은 [ " + drawSkillToAsk.getAnswer() + " ] 입니다!").queue();

                                            if (!isLast(jobIndex, total)) {
                                                log.info("문제가 남았습니다! 다음 문제를 출제합니다.");
                                                int updatedJobIndex = jobIndex + 1;

                                                processJobQuestion(targetChannel, targetUser, drawSkills, updatedJobIndex, total, correctCount);
                                            } else {
                                                // 출제 끝
                                                targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(correctCount, total)).queue();
                                                return;
                                            }
                                        }
                                    },
                                    20, TimeUnit.SECONDS,
                                    () -> { // Timeout Action
                                        log.info("TIMEOUT!" + drawSkillToAsk.getSkillName());

                                        targetChannel.sendMessage(Emojis.CLOCK.getEmojiString() + " " + "시간 초과입니다!\n" + Emojis.CLOCK.getEmojiString() + " " + "정답은 [ " + drawSkillToAsk.getAnswer() +" ] ! :)").queue();
                                        if (isLast(jobIndex, total)) {
                                            System.out.println("출제를 종료합니다...");
                                            targetChannel.sendMessage("\n\n" + Emojis.PARTYING_FACE.getEmojiString() + " 모든 문제가 끝났습니다! " + Emojis.PARTYING_FACE.getEmojiString() + "\n" + "정답률 : " + getCorrectRate(correctCount, total)).queue();

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

    private List<DrawSkills> getRandomListWithAnswer(DrawSkills answer) {
        DrawSkills[] values = DrawSkills.values();
        ArrayList<DrawSkills> drawSkills = new ArrayList<>(Arrays.asList(values));
        Collections.shuffle(drawSkills);

        DrawSkills[] optionCandidates = drawSkills.subList(0, 4).toArray(new DrawSkills[4]);

        int answerIdx = IntStream.range(0, optionCandidates.length)
                .filter(i -> optionCandidates[i].getSkillName().equals(answer.getSkillName()))
                .findFirst()
                .orElse(-1);

        if (answerIdx != -1) {
            System.out.println("랜덤 목록에 정답이 포함되어 있습니다." + answerIdx);
            return Arrays.asList(optionCandidates);
        }

        // 없으면
        System.out.println("랜덤 목록에 정답이 없습니다" + answerIdx);
        System.out.println("정답 : " + answer.getAnswer());

        ArrayList<DrawSkills> newOptionCandidates = new ArrayList<>(Arrays.asList(optionCandidates).subList(0, 3));

        newOptionCandidates.add(answer);

        Collections.shuffle(newOptionCandidates);

        return newOptionCandidates;
    }

}

package personal.ffivquizbot.slash_command.astrologian_skills;

import org.springframework.stereotype.Service;
import personal.ffivquizbot.slash_command.astrologian_skills.enums.DrawSkills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Service
public class SkillQuestionProvider {
    public ArrayList<DrawSkills> getRandomSkillList(Integer elementCount) {
        ArrayList<DrawSkills> jobListCopy = new ArrayList<>(Arrays.asList(DrawSkills.values()));

        Random random = new Random();

        ArrayList<DrawSkills> extractedRandoms = new ArrayList<DrawSkills>();

        for (int i = 0; i < elementCount; i++) {
            int randomIndex = random.nextInt(jobListCopy.size());

            extractedRandoms.add(jobListCopy.get(randomIndex));

            jobListCopy.remove(randomIndex);
        }

        return extractedRandoms;
    }
}

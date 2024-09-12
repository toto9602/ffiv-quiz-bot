package personal.ffivquizbot.slashcommand.astrologianskills

import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.astrologianskills.enums.DrawSkills

@Service
class SkillQuestionProvider {
    fun getRandomSkillList(elementCount: Int): List<DrawSkills> {
        return DrawSkills.values().toList().shuffled().take(elementCount)
    }
}

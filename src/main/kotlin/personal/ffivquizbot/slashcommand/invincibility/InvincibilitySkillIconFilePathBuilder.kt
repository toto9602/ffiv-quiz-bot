package personal.ffivquizbot.slashcommand.invincibility

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.astrologianskills.enums.DrawSkills

@Service
class InvincibilitySkillIconFilePathBuilder(
    @Value("\${aws.basePath}")
    private val basePath:String,
) {
    fun buildPath(skill:): String {
        var dirName = this.getDirName();

        return "${basePath}/${dirName}/${skill.urlSuffix }"
    }
}
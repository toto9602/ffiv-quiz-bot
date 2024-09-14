package personal.ffivquizbot.slashcommand.invincibility

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.astrologianskills.enums.DrawSkills
import personal.ffivquizbot.slashcommand.invincibility.enums.InvincibilitySkills

@Service
class InvincibilitySkillIconFilePathBuilder(
    @Value("\${aws.basePath}")
    private val basePath:String,
) {
    fun buildPath(skill:InvincibilitySkills): String {
        var dirName = this.getDirPath()

        return "${basePath}/${dirName}/${skill.iconUrlSuffix}"
    }

    private fun getDirPath():String {
        return "invincibility"
    }
}
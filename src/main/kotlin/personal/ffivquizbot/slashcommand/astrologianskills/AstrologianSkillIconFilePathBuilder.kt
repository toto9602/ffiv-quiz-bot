package personal.ffivquizbot.slashcommand.astrologianskills

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.astrologianskills.enums.DrawSkills

@Service
class AstrologianSkillIconFilePathBuilder(
    @Value("\${aws.basePath}")
    private val basePath: String
) {
    fun buildPath(skill: DrawSkills):String {
        val dirName = this.getDirName()

        return "${basePath}/${dirName}/${skill.skillIconUrlSuffix}"
    }

    private fun getDirName():String {
        return "ast-skills"
    }
}
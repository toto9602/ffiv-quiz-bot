package personal.ffivquizbot.slashcommand.jobquestion

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.jobquestion.enums.FFIVJobs
import personal.ffivquizbot.slashcommand.jobquestion.enums.JobCategory

@Service
class JobIconFilePathBuilder(
    @Value("\${aws.basePath}")
    private val basePath: String,
) {

    fun buildPath(job: FFIVJobs):String {
        val dirName = this.getDirName(job.jobCategory)

        return "${basePath}/${dirName}/${job.jobIconUrlSuffix}"
    }

    private fun getDirName(jobCategory: JobCategory):String {
        return when (jobCategory) {
            JobCategory.DPS -> "dps"
            JobCategory.TANK -> "tank"
            JobCategory.LIMITED -> "limited"
            JobCategory.HEAL -> "healer"
        }
    }
}
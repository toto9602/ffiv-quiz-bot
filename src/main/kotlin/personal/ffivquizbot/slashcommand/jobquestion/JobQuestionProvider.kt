package personal.ffivquizbot.slashcommand.jobquestion

import org.springframework.stereotype.Service
import personal.ffivquizbot.slashcommand.jobquestion.enums.FFIVJobs

@Service
class JobQuestionProvider {
    fun getRandomJobList(elementCount: Int): List<FFIVJobs> {
        return FFIVJobs.values().toList().shuffled().take(elementCount)
    }
}

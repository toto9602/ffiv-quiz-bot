package personal.ffivquizbot.slashcommand.jobquestion

import org.springframework.stereotype.Service

@Service
class JobQuestionProvider {
    fun getRandomJobList(elementCount: Int): List<FFIVJobs> {
        return FFIVJobs.values().toList().shuffled().take(elementCount)
    }
}

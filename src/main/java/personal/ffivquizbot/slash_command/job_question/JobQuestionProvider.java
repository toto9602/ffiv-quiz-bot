package personal.ffivquizbot.slash_command.job_question;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Service
public class JobQuestionProvider {

    public ArrayList<FFIVJobs> getRandomJobList(Integer elementCount) {
        ArrayList<FFIVJobs> jobListCopy = new ArrayList<>(Arrays.asList(FFIVJobs.values()));

        Random random = new Random();

        ArrayList<FFIVJobs> extractedRandoms = new ArrayList<FFIVJobs>();

        for (int i = 0; i < elementCount; i++) {
            int randomIndex = random.nextInt(jobListCopy.size());

            extractedRandoms.add(jobListCopy.get(randomIndex));

            jobListCopy.remove(randomIndex);
        }

        return extractedRandoms;
    }
}

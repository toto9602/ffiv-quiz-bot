package personal.ffivquizbot.health;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Data
@NoArgsConstructor
public class HealthResponse {
    Boolean serverRunning;
    JDA.Status botStatus;

    public HealthResponse(Boolean serverRunning, JDA.Status botStatus) {
        this.serverRunning = serverRunning;
        this.botStatus = botStatus;
    }
}

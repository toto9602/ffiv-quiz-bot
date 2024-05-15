package personal.ffivquizbot.slash_command;

import lombok.Getter;

public abstract class BaseSlashCommandHandler {
    @Getter
    private final String circuitBreakCommand = "중단!";
}

package personal.ffivquizbot.slash_command.astrologian_skills.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DrawSign {
    SUN("해의 징조"),
    MOON("달의 징조"),
    STAR("별의 징조");

    private final String signName;
}

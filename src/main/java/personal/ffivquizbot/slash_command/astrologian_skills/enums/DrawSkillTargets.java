package personal.ffivquizbot.slash_command.astrologian_skills.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DrawSkillTargets {
    DEFENSE_CLOSE("방어 및 근거리"),
    HEAL_FAR("회복 및 원거리");

    private final String targetDescription;
}

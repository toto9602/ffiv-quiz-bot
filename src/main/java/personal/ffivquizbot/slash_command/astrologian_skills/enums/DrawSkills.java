package personal.ffivquizbot.slash_command.astrologian_skills.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @description 점지(Draw) 시 발동 가능한 스킬 목록
 */
@RequiredArgsConstructor
@Getter
public enum DrawSkills {
    BALANCE("아제마의 균형", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSign.SUN),
    BOLE("세계수의 줄기", "", DrawSkillTargets.HEAL_FAR, DrawSign.SUN),
    ARROW("오쉬온의 화살", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSign.MOON),
    SPEAR("할로네의 창", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSign.STAR),
    EWER("살리아크의 물병", "", DrawSkillTargets.HEAL_FAR, DrawSign.MOON),
    SPIRE("비레고의 탑", "", DrawSkillTargets.HEAL_FAR, DrawSign.STAR);

    private final String skillName;
    private final String skillIconUrl;
    private final DrawSkillTargets targets;
    private final DrawSign sign;

}

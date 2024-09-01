package personal.ffivquizbot.slashcommand.astrologianskills.enums


enum class DrawSkills(
    val skillName:String,
    val skillIconUrl:String,
    val target:DrawSkillTargets,
    val sign:DrawSigns,
) {
    BALANCE("아제마의 균형", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.SUN),
    BOLE("세계수의 줄기", "", DrawSkillTargets.HEAL_FAR, DrawSigns.SUN),
    ARROW("오쉬온의 화살", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.MOON),
    SPEAR("할로네의 창", "", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.STAR),
    EWER("살리아크의 물병", "", DrawSkillTargets.HEAL_FAR, DrawSigns.MOON),
     SPIRE("비레고의 탑", "", DrawSkillTargets.HEAL_FAR, DrawSigns.STAR),
}


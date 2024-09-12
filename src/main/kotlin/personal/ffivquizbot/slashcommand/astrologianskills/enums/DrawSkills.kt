package personal.ffivquizbot.slashcommand.astrologianskills.enums


enum class DrawSkills(
    val skillName:String,
    val skillIconUrlSuffix:String,
    val target:DrawSkillTargets,
    val sign:DrawSigns,
) {
    BALANCE("아제마의 균형", "Azema.png", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.SUN),
    BOLE("세계수의 줄기", "WorldTree.png", DrawSkillTargets.HEAL_FAR, DrawSigns.SUN),
    ARROW("오쉬온의 화살", "Oshion.png", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.MOON),
    SPEAR("할로네의 창", "Halone.png", DrawSkillTargets.DEFENSE_CLOSE, DrawSigns.STAR),
    EWER("살리아크의 물병", "Saliak.png", DrawSkillTargets.HEAL_FAR, DrawSigns.MOON),
     SPIRE("비레고의 탑", "Birego.png", DrawSkillTargets.HEAL_FAR, DrawSigns.STAR),
}


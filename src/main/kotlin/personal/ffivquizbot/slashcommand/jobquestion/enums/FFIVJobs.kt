package personal.ffivquizbot.slashcommand.jobquestion.enums

enum class FFIVJobs(
    val jobName:String,
    val jobIconUrlSuffix:String,
    val jobCategory: JobCategory,
) {
    DARK_KNIGHT("암흑기사", "DarkKnight.png", JobCategory.TANK),
    GUN_BREAKER("건브레이커", "Gunbreaker.png", JobCategory.TANK),
    PALADIN("나이트", "Paladin.png", JobCategory.TANK),
    WARRIOR("전사", "Warrior.png", JobCategory.TANK),
    ASTROLOGIAN("점성술사", "Astrologian.png", JobCategory.HEAL),
    SAGE("현자", "Sage.png", JobCategory.HEAL),
    SCHOLAR("학자", "Scholar.png", JobCategory.HEAL),
    WHITE_MAGE("백마도사", "WhiteMage.png", JobCategory.HEAL),
    BARD("음유시인", "Bard.png", JobCategory.DPS),
    BLACK_MAGE("흑마도사", "BlackMage.png", JobCategory.DPS),
    DANCER("무도가", "Danger.png", JobCategory.DPS),
    DRAGOON("용기사", "Dragoon.png", JobCategory.DPS),
    MACHINIST("기공사", "Machinist.png", JobCategory.DPS),
    MONK("몽크", "Monk.png", JobCategory.DPS),
    NINJA("닌자", "Ninja.png", JobCategory.DPS),
    REAPER("리퍼", "Reaper.png", JobCategory.DPS),
    RED_MAGE("적마도사", "RedMage.png", JobCategory.DPS),
    SAMURAI("사무라이", "Samurai.png", JobCategory.DPS),
    SUMMONER("소환사", "Summoner.png", JobCategory.DPS),
    BLUE_MAGE("청마도사", "BlueMage.png", JobCategory.LIMITED),
}

package personal.ffivquizbot.slash_command.job_question;

import lombok.Getter;

@Getter
public enum FFIVJobs {
    DARK_KNIGHT("암흑기사", ""),
    GUN_BREAKER("건브레이커", ""),
    PALADIN("나이트", ""),
    WARRIOR("전사", ""),
    ASTROLOGIAN("점성술사", ""),
    SAGE("현자", ""),
    SCHOLAR("학자", ""),
    WHITE_MAGE("백마도사", ""),
    BARD("음유시인", ""),
    BLACK_MAGE("흑마도사", ""),
    DANCER("무도가", ""),
    DRAGOON("용기사", ""),
    MACHINIST("기공사", ""),
    MONK("몽크", ""),
    NINJA("닌자", ""),
    REAPER("리퍼", ""),
    RED_MAGE("적마도사", ""),
    SAMURAI("사무라이", ""),
    SUMMONER("소환사", ""),
    BLUE_MAGE("청마도사", "");

    private final String jobName;

    private final String jobIconUrl;

    FFIVJobs(String jobName, String jobIconUrl) {
        this.jobName = jobName;
        this.jobIconUrl = jobIconUrl;
    }

}


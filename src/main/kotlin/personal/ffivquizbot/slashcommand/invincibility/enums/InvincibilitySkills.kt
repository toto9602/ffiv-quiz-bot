package personal.ffivquizbot.slashcommand.invincibility.enums

enum class InvincibilitySkills(
    val skillName:String,
    val iconUrlSuffix:String,
    val description:String,
    val job:String,
) {
    INVINCIBLE("천하무적", "invincible.jpg", "10초 동안 일부를 제외한 대부분의 피해를 무효화합니다.", "나이트"),
    EXPLOSION("폭발 유성", "", "자신의 HP를 1로 만들고, 8초 동안 일부를 제외한 대부분의 피해를 무효화합니다.", "건브레이커"),
    DEAD_OR_ALIVE("산송장", "", "10초 안에 전투불능이 될 만한 피해를 받으면 HP가 1 남고 움직이는 시체 상태가 됩니다.", "암흑기사"),
    BATTLE("일대일 결투", "", "8초 동안 일브를 제외한 어떤 공격을 받아도 자신의 HP가 1 미만으로 떨어지지 않습니다. 적을 대상으로 실행한 경우에는 대상을 이동 불가 상태로 만듭니다.", "전사")
}
package personal.ffivquizbot.emojis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Emojis {
    MAN_O(":man_gesturing_ok:"),
    MAN_X(":man_gesturing_no:"),
    WOMAN_O(":woman_gesturing_ok:"),
    WOMAN_X(":woman_gesturing_no:"),
    ROCKET(":rocket:"),
    CLOCK(":alarm_clock:"),
    CLAP(":clap:"),
    SMILING_TEAR(":smiling_face_with_tear:");

    private final String emojiString;

}

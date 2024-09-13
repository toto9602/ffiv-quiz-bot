package personal.ffivquizbot.slashcommand.questionutils

import java.text.NumberFormat


fun getCorrectRate(correctCount: Int, total: Int):String {
    val percentFormat = NumberFormat.getPercentInstance()

    percentFormat.minimumFractionDigits = 0
    percentFormat.maximumFractionDigits = 2

    val rate = (correctCount.toDouble() / total.toDouble())

    return percentFormat.format(rate)
}
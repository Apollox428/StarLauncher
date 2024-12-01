package utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun parseColoredLog(log: String): AnnotatedString {
    val parts = log.split("§")
    var lastStyle = SpanStyle(fontSize = 14.sp)
    val annotatedString = buildAnnotatedString {
        for (part in parts) {
            if (part.isNotBlank()) {
                val colorChar = part[0]
                val style = getStyleForChar(colorChar, lastStyle)
                if (!log.startsWith(part)) {
                    val remainingText = part.substring(1)
                    withStyle(getStyleForChar(colorChar, lastStyle)) {
                        append(remainingText)
                    }
                } else {
                    withStyle(getStyleForChar('r', lastStyle)) {
                        append(part)
                    }
                }
                if (colorChar in '0'..'9' || colorChar in 'a'..'f' || colorChar in 'l'..'o') {
                    lastStyle = style
                }
            }
        }
    }
    return annotatedString
}

private fun getStyleForChar(colorChar: Char, baseStyle: SpanStyle): SpanStyle {
    return when (colorChar) {
        '0' -> baseStyle.copy(color = Color.Black)
        '1' -> baseStyle.copy(color = Color(0, 5, 170))
        '2' -> baseStyle.copy(color = Color(0, 170, 40))
        '3' -> baseStyle.copy(color = Color(0, 175, 175))
        '4' -> baseStyle.copy(color = Color(190, 0, 0))
        '5' -> baseStyle.copy(color = Color(190, 0, 165))
        '6' -> baseStyle.copy(color = Color(255, 165, 0))
        '7' -> baseStyle.copy(color = Color.LightGray)
        '8' -> baseStyle.copy(color = Color.Gray)
        '9' -> baseStyle.copy(color = Color(77, 86, 254))
        'a' -> baseStyle.copy(color = Color(0, 255, 103))
        'b' -> baseStyle.copy(color = Color.Cyan)
        'c' -> baseStyle.copy(color = Color(255, 68, 69))
        'd' -> baseStyle.copy(color = Color(255, 70, 250))
        'e' -> baseStyle.copy(color = Color.Yellow)
        'f' -> baseStyle.copy(color = Color.White)
        'r' -> baseStyle.copy(color = Color.White)
        'l' -> baseStyle.copy(color = baseStyle.color, fontWeight = FontWeight.Bold)
        'n' -> baseStyle.copy(color = baseStyle.color, textDecoration = TextDecoration.combine(
            listOf(
                baseStyle.textDecoration ?: TextDecoration.None,
                TextDecoration.Underline
            )
        ))
        'o' -> baseStyle.copy(color = baseStyle.color, fontStyle = FontStyle.Italic)
        'm' -> baseStyle.copy(color = baseStyle.color, textDecoration = TextDecoration.combine(
            listOf(
                baseStyle.textDecoration ?: TextDecoration.None,
                TextDecoration.LineThrough
            )
        ))
        else -> baseStyle
    }
}

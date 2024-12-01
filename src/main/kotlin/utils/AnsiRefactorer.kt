package utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

fun AnsiRefactorer(input: String): String {
    return input
        .replace("[?25h", "")
        .replace("←[0m", "§r")
        .replace("\u001B[?25l", "")
        .replace("\u001B[0K", "")
        .replace("←[92m", "§2")
        .replace("←[33m", "§6")
        .replace("←[31m", "§4")
        .replace("\u001B", "")
        .replace("[1A[40G", "")
        .replace("[1G", "")
        .replace("[3A", "")
        .replace("[1A[36G", "")
        .replace("[5G", "")
        .replace("[1A", "")
        .replace("[25G", "")
        .replace("[31G", "")
        .replace("[47G", "")
        .replace("[114A", "")
        .replace("[18A", "")
        .replace("[17A", "")
        .replace("[28A", "")
        .replace("[0m", "§r")
}

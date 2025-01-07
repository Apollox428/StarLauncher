using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using Windows.UI.Xaml.Documents;
using Windows.UI.Xaml.Media;

public class Line
{
    public string Text { get; set; }
    public Windows.UI.Text.FontWeight FontWeight { get; set; }
    public Windows.UI.Text.TextDecorations TextDecorations { get; set; }
    public SolidColorBrush Foreground { get; set; }
}


public class FormattedText
{
    public string Text { get; set; }
    public bool IsBold { get; set; }
    public bool IsUnderline { get; set; }
    public Windows.UI.Color ForegroundColor { get; set; }
}

public class AnsiCodeParser
{
    private bool bold = false;
    private bool underline = false;
    private Windows.UI.Color foregroundColor = Windows.UI.Colors.Black;

    private static readonly Dictionary<int, Windows.UI.Color> foregroundColorMap = new Dictionary<int, Windows.UI.Color>
    {
        { 30, Windows.UI.Colors.Black },
        { 31, Windows.UI.Colors.Red },
        { 32, Windows.UI.Colors.Green },
        { 33, Windows.UI.Colors.Yellow },
        { 34, Windows.UI.Colors.Blue },
        { 35, Windows.UI.Colors.Magenta },
        { 36, Windows.UI.Colors.Cyan },
        { 37, Windows.UI.Colors.White },
        { 39, Windows.UI.Colors.Black } // Reset
    };

    public List<FormattedText> ProcessAnsiCodes(string input)
    {
    var processedText = new List<FormattedText>();
    string[] lines = input.Split(new[] { "\r\n", "\n" }, StringSplitOptions.None);

    foreach (var line in lines)
    {
    string[] parts = Regex.Split(line, @"(\x1b\[.*?m)");

    foreach (var part in parts)
    {
    if (part.StartsWith("\x1b["))
    {
    ApplyEscapeCode(part);
    }
    else
    {
    processedText.Add(new FormattedText
    {
        Text = part,
        IsBold = bold,
        IsUnderline = underline,
        ForegroundColor = foregroundColor
    });
    }
    }

    // Add an empty line between lines
    processedText.Add(new FormattedText
    {
        Text = "",
        IsBold = false,
        IsUnderline = false,
        ForegroundColor = Windows.UI.Colors.Transparent
    });
    }

    return processedText;
    }


    private void ApplyEscapeCode(string escapeCode)
    {
    string codes = escapeCode.Substring(2, escapeCode.Length - 3);
    string[] codeParts = codes.Split(';');

    foreach (var code in codeParts)
    {
    if (int.TryParse(code, out int codeInt))
    {
    switch (codeInt)
    {
        case 0:
            bold = false;
            underline = false;
            foregroundColor = Windows.UI.Colors.Black;
            break;
        case 1:
            bold = true;
            break;
        case 4:
            underline = true;
            break;
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
            foregroundColor = foregroundColorMap[codeInt];
            break;
    }
    }
    }
    }
}


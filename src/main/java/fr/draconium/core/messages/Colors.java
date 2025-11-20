package fr.draconium.core.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors
{
	private final static Pattern pattern = Pattern.compile("#[A-Fa-f0-9]{6}");

	public static String setMessageHexadecimal(String message)
	{
		Matcher matcher = pattern.matcher(message);
		StringBuffer sb = new StringBuffer();

		while (matcher.find())
		{
			String color = message.substring(matcher.start(), matcher.end());
			String ansiColor = setColorHexadecimal(color);
			matcher.appendReplacement(sb, ansiColor);
		}

		matcher.appendTail(sb);
		return sb.toString();
	}

	private static String setColorHexadecimal(String hexColor)
	{
		// Convertir le code couleur hexadécimal en séquence ANSI
		return "\u001B[38;2;" + Integer.parseInt(hexColor.substring(1, 3), 16) + ";" + Integer.parseInt(hexColor.substring(3, 5), 16) + ";" + Integer.parseInt(hexColor.substring(5, 7), 16) + "m";
	}

	public static String removeColors(String input)
	{
		// Utilisation d'une expression régulière pour supprimer les codes ANSI
		String ansiPattern = "\u001B\\[[;\\d]*m";
		return input.replaceAll(ansiPattern, "");
	}
}

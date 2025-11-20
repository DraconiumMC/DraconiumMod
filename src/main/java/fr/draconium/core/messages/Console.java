package fr.draconium.core.messages;

import fr.draconium.core.DraconiumCore;

public class Console
{
	private static String prefix = "#566573[#e74c3cDraco#f7dc6fnium#566573] #FFFFFF";
	
	public static void setMessage(String message)
	{
		message = Colors.setMessageHexadecimal(prefix + message + "#FFFFFF");
		DraconiumCore.LOGGER.info(message);
	}
	
	public static void error(String message)
	{
		message = Colors.setMessageHexadecimal(prefix + "#E74C3C" + message + "#FFFFFF");
		DraconiumCore.LOGGER.info(message);
	}
	
	public static void debug(String message)
	{
		message = Colors.setMessageHexadecimal(prefix + "#5DADE2" + message + "#FFFFFF");
		DraconiumCore.LOGGER.info(message);
	}
}

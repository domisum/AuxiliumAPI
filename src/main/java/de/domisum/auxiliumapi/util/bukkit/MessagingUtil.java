package de.domisum.auxiliumapi.util.bukkit;

import de.domisum.auxiliumapi.util.java.annotations.APIUsage;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MessagingUtil
{

	@APIUsage
	public static void sendActionBarMessage(String message, Player... players)
	{
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \""+message+"\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);

		for(Player p : players)
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}

}

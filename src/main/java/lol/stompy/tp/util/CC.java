package lol.stompy.tp.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static final String CHAT_BAR = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(List<String> strings) {
        return strings.stream().map(CC::translate).collect(Collectors.toList());
    }

}
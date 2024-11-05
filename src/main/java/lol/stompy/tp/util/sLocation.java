package lol.stompy.tp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class sLocation {

    public static String locationToString(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location stringToLocation(String string) {
        String[] args = string.split(",");
        return new Location(Bukkit.getServer().getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

}
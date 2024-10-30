package lol.stompy.tp.profile;

import lol.stompy.tp.TP;
import lol.stompy.tp.util.cooldown.SimpleCooldown;
import lol.stompy.tp.util.sFile;
import lol.stompy.tp.util.sLocation;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
public class Profile {

    private final UUID uuid;

    private final HashMap<String, Location> warps;
    private final SimpleCooldown tpCooldown;;

    /**
     * Creates a profile for a uuid
     *
     * @param uuid uuid to create profile for
     * @param tpCooldown time for the tp cooldown
     */

    public Profile(UUID uuid, int tpCooldown) {
        this.uuid = uuid;

        this.warps = new HashMap<>();
        this.tpCooldown = new SimpleCooldown(tpCooldown);
    }

    /**
     * Creates a profile from configuration
     *
     * @param configurationSection section to create profile from
     * @param tpCooldown time for the tp cooldown
     */

    public Profile(ConfigurationSection configurationSection, int tpCooldown) {
        this.uuid = UUID.fromString(configurationSection.getName());
        this.tpCooldown = new SimpleCooldown(tpCooldown);
        this.warps = new HashMap<>();

        final List<String> strings = configurationSection.getStringList("warps");

        if (strings.isEmpty())
            return;

        strings.forEach(s -> {
            final String[] args = s.split(":");
            warps.put(args[0], sLocation.stringToLocation(args[1]));
        });

    }


    /**
     * saves a profile to the config
     *
     * @param sFile file to save the data to
     */

    @SneakyThrows
    public final void saveToConfig(sFile sFile) {
        final FileConfiguration fileConfiguration = sFile.getConfig();

        final List<String> stringList = new ArrayList<>();

        warps.forEach((s, location) -> {
            stringList.add(s + ":" + sLocation.locationToString(location));
        });

        fileConfiguration.set(uuid.toString() + ".warps", stringList);
        sFile.save();
    }

    /**
     * puts the player on cooldown
     */

    public final void putCooldown() {
        this.tpCooldown.put();
    }

    /**
     * checks if the cooldown expired
     *
     * @return {@link Boolean}
     */

    public final boolean isOnCooldown() {
        return this.tpCooldown.hasExpired();
    }

    /**
     * creates a tp for a location
     *
     * @param name name of tp
     * @param tp location
     */

    public final void createTp(String name, Location tp) {
        warps.put(name, tp);
    }

    /**
     * gets a warp from the name
     *
     * @param warp warp to find
     * @return {@link Optional<Location>}
     */

    public final Optional<Location> getWarp(String warp) {
        return Optional.ofNullable(warps.get(warp.toUpperCase()));
    }

    /**
     * outputs all warps names
     *
     * @return {@link String}
     */

    public final String warpsToString() {
        if (warps.isEmpty())
            return "None";

        final StringBuilder builder = new StringBuilder();
        builder.append("&aWarps&f: ");

        warps.keySet().forEach(s -> builder.append("&a").append(s).append(", "));

        return builder.toString();
    }

}

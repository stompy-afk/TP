package lol.stompy.tp.profile;

import lol.stompy.tp.request.TPRequest;
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
    private final SimpleCooldown tpCooldown;

    private final List<TPRequest> tpRequests;

    /**
     * Creates a profile for a uuid
     *
     * @param uuid       uuid to create profile for
     * @param tpCooldown time for the tp cooldown
     */

    public Profile(UUID uuid, int tpCooldown) {
        this.uuid = uuid;

        this.warps = new HashMap<>();
        this.tpCooldown = new SimpleCooldown(tpCooldown);
        this.tpRequests = new ArrayList<>();
    }

    /**
     * Creates a profile from configuration
     *
     * @param configurationSection section to create profile from
     * @param tpCooldown           time for the tp cooldown
     */

    public Profile(ConfigurationSection configurationSection, int tpCooldown) {
        this.uuid = UUID.fromString(configurationSection.getName());

        this.tpCooldown = new SimpleCooldown(tpCooldown);
        this.warps = new HashMap<>();
        this.tpRequests = new ArrayList<>();

        final List<String> strings = configurationSection.getStringList("warps");

        if (strings.isEmpty())
            return;

        strings.forEach(s -> {
            final String[] args = s.split(":");
            warps.put(args[0].toUpperCase(), sLocation.stringToLocation(args[1]));
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
        return !this.tpCooldown.hasExpired();
    }

    /**
     * creates a tp for a location
     *
     * @param name name of tp
     * @param tp   location
     */

    public final void createTp(String name, Location tp) {
        warps.put(name.toUpperCase(), tp);
    }

    /**
     * Deletes a TP of the player
     *
     * @param name name of tp to delete
     */

    public final void deleteTp(String name) {
        warps.remove(name.toUpperCase());
    }

    /**
     * Sends a TP request
     *
     * @param tpName   name of TP
     * @param receiver receiver of request
     */

    public final void sendRequest(String tpName, Profile receiver) {
        tpRequests.add(new TPRequest(tpName, this, receiver));
        receiver.addRequest(tpName, this);
    }

    /**
     * Adds a TP request
     *
     * @param tpName name of TP
     * @param sender sender of TP
     */

    public final void addRequest(String tpName, Profile sender) {
        tpRequests.add(new TPRequest(tpName, sender, this));
    }

    /**
     * Removes a request
     *
     * @param tpName request to remove
     */

    public final void removeRequest(String tpName) {
        tpRequests.removeIf(tpRequest -> tpRequest.getTpName().equalsIgnoreCase(tpName));
    }

    /**
     * Finds a TP request from a profile
     *
     * @param profile profile to find TP request from
     * @return {@link Optional<TPRequest>}
     */

    public final Optional<TPRequest> findRequest(Profile profile) {
        return tpRequests.stream().filter(tpRequest -> tpRequest.getSender().getUuid().equals(profile.getUuid())).findFirst();
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

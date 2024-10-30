package lol.stompy.tp.profile;

import lol.stompy.tp.TP;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ProfileHandler {

    private final Map<UUID, Profile> profileHashMap;
    private final TP tp;

    @Getter
    private final int cooldownDuration;

    /**
     * profile handler, handles all profile related stuff
     *
     * @param tp instance of main
     */

    public ProfileHandler(TP tp) {
        this.tp = tp;

        this.profileHashMap = new HashMap<>();
        this.cooldownDuration = tp.getConfig().getInt("settings.cooldown");
    }

    /**
     * Loads a profile
     *
     * @param uuid uuid to load profile of
     */

    public final void load(UUID uuid, boolean async) {

        if (async) {
            tp.getServer().getScheduler().runTaskAsynchronously(tp, () -> this.load(uuid, false));
            return;
        }

        final ConfigurationSection configurationSection = tp.getProfiles().getConfig().getConfigurationSection(uuid.toString());

        if (configurationSection == null) {
            final Profile profile = new Profile(uuid, cooldownDuration);

            profileHashMap.put(uuid, profile);
            this.save(profile, async);
            return;
        }

        profileHashMap.put(uuid, new Profile(configurationSection, cooldownDuration));
    }

    /**
     * Handles the removal of a profile
     *
     * @param profile profile to handle removal off
     * @param async to do task async or not
     */

    public final void handleRemoval(Profile profile, boolean async) {
        this.save(profile, async);
        profileHashMap.remove(profile.getUuid());
    }

    /**
     * saves a profile to the configuration file
     *
     * @param profile profile to save
     * @param async to do task async or not
     */

    private void save(Profile profile, boolean async) {

        if (async) {
            tp.getServer().getScheduler().runTaskAsynchronously(tp, () -> save(profile, false));
            return;
        }

        profile.saveToConfig(tp.getProfiles());
    }

    /**
     * Gets a profile from the map
     *
     * @param uuid uuid of profile
     * @return {@link Optional<Profile>}
     */

    public final Optional<Profile> getProfile(UUID uuid) {
        return Optional.ofNullable(profileHashMap.get(uuid));
    }

    /**
     * Gets all profiles
     *
      * @return {@link Collection<Profile>}
     */

    public final Collection<Profile> getProfiles() {
        return profileHashMap.values();
    }

}

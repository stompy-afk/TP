package lol.stompy.tp;

import lol.stompy.tp.profile.ProfileHandler;
import lol.stompy.tp.util.sFile;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TP extends JavaPlugin {

    @Getter
    private static TP instance;

    private ProfileHandler profileHandler;

    private sFile profiles;

    /**
     * plugin loading logic
     */

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
    }

    /**
     * plugin start-up logic
     */

    @Override
    public void onEnable() {
        this.profiles = new sFile(getDataFolder(), "profiles.yml");

        this.profileHandler = new ProfileHandler(this);
    }

}
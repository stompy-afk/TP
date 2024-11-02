package lol.stompy.tp;

import lol.stompy.tp.commands.TPCommand;
import lol.stompy.tp.profile.ProfileHandler;
import lol.stompy.tp.util.CC;
import lol.stompy.tp.util.sFile;
import lombok.Getter;
import me.vaperion.blade.Blade;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
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
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&aTP&7] &fLoading profiles..."));

        this.profiles = new sFile(getDataFolder(), "profiles.yml");
        this.profileHandler = new ProfileHandler(this);

        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&aTP&7] &fProfiles Loaded"), "\n",
                CC.translate("&7[&aTP&7] &fLoading commands..."));

        Blade.forPlatform(new BladeBukkitPlatform(this)).build().register(new TPCommand(this));
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&aTP&7] &fCommands Loaded"),
                "\n", CC.translate("&7[&aTP&7] &fWelcome - &5Discord&f: stompyafk"));
    }

    /**
     * plugin disabling logic
     */

    @Override
    public void onDisable() {
        profileHandler.getProfiles().forEach(profile -> profileHandler.handleRemoval(profile, false));
    }
}
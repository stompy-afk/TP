package lol.stompy.tp.commands;

import lol.stompy.tp.TP;
import lol.stompy.tp.profile.Profile;
import lol.stompy.tp.util.CC;
import lombok.AllArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class TPHelpCommand {

    private final TP tp;
    private final int maximumTps;

    public TPHelpCommand(TP tp) {
        this.tp = tp;
        this.maximumTps = tp.getConfig().getInt("settings.maximum-tps");
    }

    @Command("tp")
    public void help(@Sender CommandSender sender) {
        sender.sendMessage(CC.translate("&7[&aTP7&] &fTP Commands"));
        sender.sendMessage(CC.translate("&7-- &f/tp list &7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp create <name>&7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp delete <tp>&7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp send <player> <tp> &7--"));
    }

    @Command("tp list")
    public void list(@Sender Player sender) {
        final Optional<Profile> optionalProfile = tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile profile = optionalProfile.get();
        sender.sendMessage(CC.translate(profile.warpsToString()));
    }

    @Command("tp create")
    public void create(@Sender Player sender, @Name("name") String name) {
        final Optional<Profile> optionalProfile = tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile profile = optionalProfile.get();

        if (profile.getWarps().size() >= maximumTps) {
            sender.sendMessage(CC.translate("&cYou already reached the maximum allowed tp points!"));
            return;
        }

        if (profile.getWarp(name).isPresent()) {
            sender.sendMessage(CC.translate("&cYou already have a warp with this name!"));
            return;
        }

        profile.createTp(name, sender.getLocation());
        sender.sendMessage(CC.translate("&fCreated &aTP &fwith the name &a" + name + "&f!"));
    }


}

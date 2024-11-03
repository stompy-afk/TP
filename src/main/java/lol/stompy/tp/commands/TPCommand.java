package lol.stompy.tp.commands;

import lol.stompy.tp.TP;
import lol.stompy.tp.profile.Profile;
import lol.stompy.tp.request.TPRequest;
import lol.stompy.tp.util.CC;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.argument.Text;
import me.vaperion.blade.annotation.command.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TPCommand {

    private final TP tp;

    private final int maximumTps;
    private final boolean canInviteOthers;

    public TPCommand(TP tp) {
        this.tp = tp;

        this.maximumTps = tp.getConfig().getInt("settings.maximum-tps");
        this.canInviteOthers = tp.getConfig().getBoolean("settings.can-invite-others");
    }

    @Command("tp")
    public void help(@Sender CommandSender sender) {
        sender.sendMessage(CC.translate("&7[&aTP&7] &fTP Commands"));
        sender.sendMessage(CC.translate("&7-- &f/tp list &7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp to <tp> &7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp create <name>&7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp delete <tp>&7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp send <player> <tp> &7--"));
        sender.sendMessage(CC.translate("&7-- &f/tp accept <player> &7--"));
    }

    @Command("tp to")
    public void tpTo(@Sender Player sender, @Name("tp") @Text String tpName) {
        final Optional<Profile> optionalProfile = tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile profile = optionalProfile.get();

        if (profile.isOnCooldown()) {
            sender.sendMessage(CC.translate("&cYou are still on cooldown for " + profile.getTpCooldown().getTimeLeft()));
            return;
        }

        final Optional<Location> locationOptional = profile.getWarp(tpName);

        if (locationOptional.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a warp with this name!"));
            return;
        }

        sender.teleport(locationOptional.get());
        sender.sendMessage(CC.translate("&aTeleported you to warp " + tpName));

        profile.putCooldown();
    }

    @Command("tp list")
    public void tpList(@Sender Player sender) {
        final Optional<Profile> optionalProfile = tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile profile = optionalProfile.get();
        sender.sendMessage(CC.translate(profile.warpsToString()));
    }

    @Command("tp create")
    public void create(@Sender Player sender, @Name("name") @Text String name) {
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

    @Command("tp delete")
    public void delete(@Sender Player sender, @Name("tp") @Text String tp) {
        final Optional<Profile> optionalProfile = this.tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile profile = optionalProfile.get();

        if (profile.getWarp(tp).isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a warp with this name!"));
            return;
        }

        profile.deleteTp(tp);
        sender.sendMessage(CC.translate("&fDeleted TP &a" + tp + " &fsuccessfully"));
    }

    @Command("tp send")
    public final void tpSend(@Sender Player sender, @Name("player") Player player, @Name("tp") @Text String tpName) {

        if (!canInviteOthers) {
            sender.sendMessage(CC.translate("&cThe admin disabled this command!"));
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou can't use this command on yourself!"));
            return;
        }

        final Optional<Profile> optionalSenderProfile = this.tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalSenderProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Optional<Profile> optionalPlayerProfile = this.tp.getProfileHandler().getProfile(player.getUniqueId());

        if (optionalPlayerProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cThe profile of the player " + player.getName() + " is not available, please contact the admin!"));
            return;
        }

        final Profile profile = optionalSenderProfile.get();

        if (profile.getWarp(tpName).isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a warp with this name!"));
            return;
        }

        profile.sendRequest(tpName, optionalPlayerProfile.get());

        sender.sendMessage(CC.translate("&aSent tp request to " + player.getName() + " to the TP " + tpName + "!"));

        player.sendMessage(CC.translate("&aYou've received a tp request from " + sender.getName() + " to warp " + tpName));
        player.sendMessage(CC.translate("&aType &7/tp accept " + sender.getName() + " &a to accept the request!"));
    }

    @Command("tp accept")
    public final void accept(@Sender Player sender, @Name("player") Player player) {

        if (!canInviteOthers) {
            sender.sendMessage(CC.translate("&cThe admin disabled this command!"));
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou can't use this command on yourself!"));
            return;
        }

        final Optional<Profile> optionalSenderProfile = this.tp.getProfileHandler().getProfile(sender.getUniqueId());

        if (optionalSenderProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cYou don't have a profile, please contact the admin!"));
            return;
        }

        final Profile senderProfile = optionalSenderProfile.get();

        if (senderProfile.isOnCooldown()) {
            sender.sendMessage(CC.translate("&cYou are still on cooldown for " + senderProfile.getTpCooldown().getTimeLeft()));
            return;
        }

        final Optional<Profile> optionalPlayerProfile = this.tp.getProfileHandler().getProfile(player.getUniqueId());

        if (optionalPlayerProfile.isEmpty()) {
            sender.sendMessage(CC.translate("&cThe profile of the player " + player.getName() + " is not available, please contact the admin!"));
            return;
        }

        final Optional<TPRequest> tpRequest = senderProfile.findRequest(optionalPlayerProfile.get());

        if (tpRequest.isEmpty()) {
            sender.sendMessage(CC.translate("&cThis tp request has either not been sent or expired!"));
            return;
        }

        tpRequest.get().acceptRequest(sender);
        senderProfile.putCooldown();
    }

}

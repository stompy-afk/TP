package lol.stompy.tp.request;

import lol.stompy.tp.TP;
import lol.stompy.tp.profile.Profile;
import lol.stompy.tp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

@Getter
public class TPRequest {

    private final String tpName;

    private final Profile sender;
    private final Profile receiver;

    private final BukkitTask bukkitTask;

    /**
     * TP Request to send TP
     *
     * @param sender   sender of TP request
     * @param receiver receiver of TP request
     */

    public TPRequest(String tpName, Profile sender, Profile receiver) {
        this.tpName = tpName;
        this.sender = sender;
        this.receiver = receiver;

        this.bukkitTask = Bukkit.getServer().getScheduler().runTaskLater(TP.getInstance(), this::requestExpire,
                TP.getInstance().getConfig().getInt("expire-events") * 20L);
    }

    /**
     * Accepts a request
     */

    public final void acceptRequest(Player player) {
        if (bukkitTask != null)
            bukkitTask.cancel();

        final Server server = player.getServer();
        final Player playerSender = server.getPlayer(sender.getUuid());

        final Optional<Location> optionalTP = sender.getWarp(tpName);

        sender.removeRequest(tpName);
        receiver.removeRequest(tpName);

        if (optionalTP.isEmpty()) {
            player.sendMessage(CC.translate("&cTp no longer exists!"));
            if (playerSender != null)
                playerSender.sendMessage(CC.translate("&c" + player.getName() + "'s teleportation failed, as your tp " + tpName + " no longer exists!"));
            return;
        }

        player.teleport(optionalTP.get());
        player.sendMessage(CC.translate("&aTeleported to &f" + tpName));

        if (playerSender != null)
            playerSender.sendMessage(CC.translate("&aPlayer &f" + player.getName() + "&a was successfully teleported to &f" + tpName));
    }

    /**
     * Deletes a request
     */

    private void requestExpire() {
        sender.removeRequest(tpName);
        receiver.removeRequest(tpName);
    }

}

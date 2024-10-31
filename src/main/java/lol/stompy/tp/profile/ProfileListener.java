package lol.stompy.tp.profile;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class ProfileListener implements Listener {

    private final ProfileHandler profileHandler;

    /**
     * event handling profile loading
     *
     * @param event {@link AsyncPlayerPreLoginEvent}
     */

    @EventHandler
    public final void onAsyncPlayerJoinEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED))
            profileHandler.load(event.getUniqueId(), true);
    }

    /**
     * event running to handle removal of a profile
     *
     * @param event {@link PlayerQuitEvent}
     */

    @EventHandler
    public final void onPlayerQuitEvent(PlayerQuitEvent event) {
        profileHandler.getProfile(event.getPlayer().getUniqueId()).
                ifPresent(profile -> profileHandler.handleRemoval(profile, true));
    }

}

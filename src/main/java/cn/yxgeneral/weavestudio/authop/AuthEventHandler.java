package cn.yxgeneral.weavestudio.authop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AuthEventHandler implements Listener{
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
        if (Validator.isAuthed(event.getPlayer())) {
            ConfigManager.onLogout(event.getPlayer());
            Validator.removeAuthedPlayer(event.getPlayer());
        }
    }
}

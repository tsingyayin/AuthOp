package cn.yxgeneral.weavestudio.authop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthOp extends JavaPlugin {
    static AuthOp Instance;
    static boolean PAPI = false;
    CommandHandler CHandler = null;
    @Override
    public void onEnable() {

        // Plugin startup logic
        Instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceHolderAPI")!=null){
            getLogger().info("PlaceHolderAPI found, enabling PlaceHolderAPI support...");
            PAPI = true;
        }else{
            getLogger().info("PlaceHolderAPI not found, disabling PlaceHolderAPI support...");
            PAPI = false;
        }
        CHandler = new CommandHandler();
        Bukkit.getPluginCommand("authop").setExecutor(CHandler);
        Bukkit.getPluginCommand("authop").setTabCompleter(CHandler);
        Bukkit.getPluginManager().registerEvents(new AuthEventHandler(), this);
        ConfigManager.initConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getInstance() {
        return Instance;
    }
}

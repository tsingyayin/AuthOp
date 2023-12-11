package cn.yxgeneral.weavestudio.authop;

import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ConfigManager {
    private static int CodeLength = 8;
    private static int TickLimit = 1200;
    private static int SafeModeCount = 8;
    private static int CoolDown = 1200;
    private static String PluginPrefix;
    public static int getTickLimit() {
        return TickLimit;
    }
    public static int getCodeLength() {
        return CodeLength;
    }
    public static int getSafeModeCount() {
        return SafeModeCount;
    }
    public static int getCoolDown() {
        return CoolDown;
    }
    public static boolean inOpPlayers(Player p) {
        return AuthOp.getInstance().getConfig().getStringList("OpPlayers").contains(p.getName());
    }
    public static void reloadConfig(){
        AuthOp.getInstance().reloadConfig();
        initConfig();
    }
    public static void initConfig(){
        AuthOp.getInstance().saveDefaultConfig();
        CodeLength = AuthOp.getInstance().getConfig().getInt("CodeLength");
        TickLimit = AuthOp.getInstance().getConfig().getInt("TickLimit");
        SafeModeCount = AuthOp.getInstance().getConfig().getInt("SafeModeCount");
        CoolDown = AuthOp.getInstance().getConfig().getInt("CoolDown");
        PluginPrefix = AuthOp.getInstance().getConfig().getString("PluginPrefix");
    }
    public static void onLogin(Player player, String code){
        onEvent(player, "OnLogin", code, 0, 0);
    }
    public static void onSuccess(Player player){
        onEvent(player, "OnSuccess", "", 0, 0);
    }
    public static void onFail(Player player, int smc){
        onEvent(player, "OnFail", "", smc, 0);
    }
    public static void onSafeMode(Player player){
        onEvent(player, "OnSafeMode", "", 0, 0);
    }
    public static void onLogout(Player player){
        onEvent(player, "OnLogout", "", 0, 0);
    }
    public static void onEvent(Player player, String EventName, String code, int smc, int cd){
        List<String> msg = AuthOp.getInstance().getConfig().getStringList(EventName+".msg");
        for (int i = 0; i < msg.size(); i++) {
            sendPrefixMessage(player, replacePlaceHolder(player, msg.get(i), code, smc, cd));
        }
        List<String> cmd = AuthOp.getInstance().getConfig().getStringList(EventName+".cmd");
        for (int i = 0; i < cmd.size(); i++) {
            executeCommand(replacePlaceHolder(player, cmd.get(i), code, smc, cd));
        }
    }
    public static void executeCommand(String command){
        AuthOp.getInstance().getServer().dispatchCommand(
                AuthOp.getInstance().getServer().getConsoleSender(), command
        );
    }
    public static String replacePlaceHolder(Player player, String str, String code, int smc, int cd){
        str = str.
                replace("@p", player.getName()).
                replace("@c", code).
                replace("@t", String.valueOf(TickLimit)).
                replace("@s", String.valueOf(smc)).
                replace("@d", String.valueOf(cd)).
                replace("&", "§");
        if (AuthOp.PAPI) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str;
    }
    public static void sendPrefixMessage(CommandSender sender, String message){
        sender.sendMessage(PluginPrefix.replace("&", "§")+message.replace("&", "§"));
    }
    public static void sendConfigMessage(CommandSender sender, String configNode){
        sendPrefixMessage(sender, AuthOp.getInstance().getConfig().getString(configNode));
    }
    public static String getConfigMessage(String configNode){
        return AuthOp.getInstance().getConfig().getString(configNode).replace("&", "§");
    }
}

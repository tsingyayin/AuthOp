package cn.yxgeneral.weavestudio.authop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("authop")) {
            if (sender instanceof  Player){
                Player p = (Player) sender;
                if (!ConfigManager.inOpPlayers(p)){
                    ConfigManager.sendConfigMessage(sender, "Message.General.NoPermission");
                    return true;
                }
            }
            if (args.length == 0) {
                ConfigManager.sendPrefixMessage(sender, "AuthOP Plugin");
                ConfigManager.sendPrefixMessage(sender, "/authop login "+
                        ConfigManager.getConfigMessage("CommandHelp.Login"));
                ConfigManager.sendPrefixMessage(sender, "/authop in "+
                        ConfigManager.getConfigMessage("CommandHelp.Login"));
                ConfigManager.sendPrefixMessage(sender, "/authop logout "+
                        ConfigManager.getConfigMessage("CommandHelp.Logout"));
                ConfigManager.sendPrefixMessage(sender, "/authop out "+
                        ConfigManager.getConfigMessage("CommandHelp.Logout"));
                ConfigManager.sendPrefixMessage(sender, "/authop v <code> "+
                        ConfigManager.getConfigMessage("CommandHelp.V"));
                ConfigManager.sendPrefixMessage(sender, "/authop su <code> "+
                        ConfigManager.getConfigMessage("CommandHelp.V"));
                ConfigManager.sendPrefixMessage(sender, "/authop reload "+
                        ConfigManager.getConfigMessage("CommandHelp.Reload"));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("login") || args[0].equalsIgnoreCase("in")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (Validator.isAuthed(player)) {
                            ConfigManager.sendConfigMessage(sender, "Message.Login.AlreadyLogin");
                        } else {
                            if (!Validator.isPlayerInCoolDown(player)) {
                                String code = Validator.generateRandomVerificationCode();
                                ConfigManager.onLogin(player, code);
                                Validator.addCoolDownPlayer(player);
                            }else{
                                ConfigManager.sendConfigMessage(sender, "Message.Login.CoolDown");
                                ConfigManager.sendPrefixMessage(sender,String.valueOf(Validator.getCoolDownSecond(player)) +" s");
                            }
                        }
                    } else {
                        ConfigManager.sendConfigMessage(sender, "Message.General.MustBePlayer");
                    }
                } else if (args[0].equalsIgnoreCase("logout") || args[0].equalsIgnoreCase("out")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (Validator.isAuthed(player)) {
                            ConfigManager.onLogout(player);
                            Validator.removeAuthedPlayer(player);
                        } else {
                            ConfigManager.sendConfigMessage(sender, "Message.Logout.NotLogin");
                        }
                    } else {
                        ConfigManager.sendConfigMessage(sender, "Message.General.MustBePlayer");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("authop.reload")) {
                        AuthOp.getInstance().reloadConfig();
                        ConfigManager.reloadConfig();
                        ConfigManager.sendConfigMessage(sender, "Message.General.Reload");
                    } else {
                        ConfigManager.sendConfigMessage(sender, "Message.General.NoPermission");
                    }
                } else if (args[0].equalsIgnoreCase("v") || args[0].equalsIgnoreCase("su")) {
                    ConfigManager.sendConfigMessage(sender, "Message.V.NoCode");
                } else {
                    ConfigManager.sendConfigMessage(sender, "Message.General.UnknownCommand");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("v") || args[0].equalsIgnoreCase("su")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (Validator.isAuthed(player)) {
                            ConfigManager.sendConfigMessage(sender, "Message.Login.AlreadyLogin");
                        } else {
                            if (Validator.verifyCode(player, args[1])) {
                                ConfigManager.onSuccess(player);
                            } else {
                                ConfigManager.onFail(player, ConfigManager.getSafeModeCount()-Validator.getPlayerFailedCount(player));
                            }
                        }
                    } else {
                        ConfigManager.sendConfigMessage(sender, "Message.General.MustBePlayer");
                    }
                } else {
                    ConfigManager.sendConfigMessage(sender, "Message.General.UnknownParameter");
                }
            } else {
                ConfigManager.sendConfigMessage(sender, "Message.General.UnknownParameter");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("authop")) {
            if (args.length == 1) {
                List<String> rtn = new ArrayList<String>();
                rtn.add("in");
                rtn.add("out");
                rtn.add("login");
                rtn.add("logout");
                rtn.add("v");
                rtn.add("su");
                rtn.add("reload");
                return rtn;
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("v") || args[0].equalsIgnoreCase("su")) {
                    List<String> rtn = new ArrayList<String>();
                    rtn.add("<code>");
                    return rtn;
                }
            }
        }
        return null;
    }
}

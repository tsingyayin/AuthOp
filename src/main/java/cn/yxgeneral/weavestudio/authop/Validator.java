package cn.yxgeneral.weavestudio.authop;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Validator {
    private static final HashMap<String, Integer> VerificationCode = new HashMap<>();
    private static final HashMap<UUID, Integer> FailedCount = new HashMap<>();
    private static final HashMap<UUID, Integer> PlayerCoolDown = new HashMap<>();
    private static final ArrayList<String> AuthedPlayers = new ArrayList<>();
    public static boolean isPlayerInCoolDown(Player player){
        return PlayerCoolDown.containsKey(player.getUniqueId());
    }
    public static boolean addCoolDownPlayer(Player player){
        if (PlayerCoolDown.containsKey(player.getUniqueId())) {
            return false;
        } else {
            PlayerCoolDown.put(player.getUniqueId(), 0);
            return true;
        }
    }
    public static double getCoolDownSecond(Player player){
        if (PlayerCoolDown.containsKey(player.getUniqueId())) {
            return (double) (ConfigManager.getCoolDown()-PlayerCoolDown.get(player.getUniqueId()))/20;
        } else {
            return 0;
        }
    }
    public static String generateRandomVerificationCode(){
        //生成CodeLength长度的由大写字母和数字组成的字符
        StringBuilder rtn = new StringBuilder();
        for (int i = 0; i < ConfigManager.getCodeLength(); i++) {
            int temp = (int) (Math.random() * 2);
            int temp1;
            if (temp == 0) {
                temp1 = (int) (Math.random() * 10 + 48);
            } else {
                temp1 = (int) (Math.random() * 26 + 65);
            }
            rtn.append((char) temp1);
        }
        Set<String> CodeList = VerificationCode.keySet();
        if (CodeList.contains(rtn.toString())) {
            return generateRandomVerificationCode();
        } else {
            if (CodeList.size()==0) {
                VerificationCode.put(rtn.toString(), 0);
                onServerTick();
                return rtn.toString();
            }else {
                VerificationCode.put(rtn.toString(), 0);
                return rtn.toString();
            }
        }
    }
    public static void onServerTick(){
        new BukkitRunnable(){
            @Override
            public void run() {
                Set<String> CodeList = VerificationCode.keySet();
                Set<UUID> PlayerList = PlayerCoolDown.keySet();
                if (CodeList.size()==0 && PlayerList.size()==0) {
                    AuthOp.getInstance().getLogger().info(ConfigManager.getConfigMessage("Message.General.TimerStopped"));
                    cancel();
                }
                for (String code : CodeList) {
                    int count = VerificationCode.get(code);
                    if (count>=ConfigManager.getTickLimit()) {
                        AuthOp.getInstance().getLogger().warning(ConfigManager.getConfigMessage("Message.General.CodeExpired"));
                        VerificationCode.remove(code);
                        break;
                    } else {
                        VerificationCode.put(code, count+1);
                    }
                }
                for (UUID player : PlayerList) {
                    int count = PlayerCoolDown.get(player);
                    if (count>=ConfigManager.getCoolDown()) {
                        PlayerCoolDown.remove(player);
                        break;
                    } else {
                        PlayerCoolDown.put(player, count+1);
                    }
                }
            }
        }.runTaskTimer(AuthOp.getInstance(), 0, 1);
    }
    public static boolean verifyCode(Player player, String code){
        if (VerificationCode.containsKey(code)) {
            VerificationCode.remove(code);
            AuthedPlayers.add(player.getName());
            FailedCount.remove(player.getUniqueId());
            return true;
        } else {
            if (!FailedCount.containsKey(player.getUniqueId())) {
                FailedCount.put(player.getUniqueId(), 1);
            }else {
                FailedCount.put(player.getUniqueId(), FailedCount.get(player.getUniqueId()) + 1);
            }
            if (FailedCount.get(player.getUniqueId())>=ConfigManager.getSafeModeCount()) {
                ConfigManager.onSafeMode(player);
            }
            return false;
        }
    }
    public static int getPlayerFailedCount(Player player){
        return FailedCount.getOrDefault(player.getUniqueId(), 0);
    }
    public static void removeAuthedPlayer(Player player){
        AuthedPlayers.remove(player.getName());
    }
    public static boolean isAuthed(Player player){
        return AuthedPlayers.contains(player.getName());
    }
}

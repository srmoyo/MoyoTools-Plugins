package com.moyo.moyotools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Arrays;
import java.util.Random;

public class MoyoTools extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("moyo").setExecutor(this);
        getLogger().info("MoyoTools habilitado correctamente!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }
        Player p = (Player) sender;
        
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usa /moyo <text|ruleta|rayo|efecto|mob|random>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            
            case "text":
                if (args.length < 5) {
                    p.sendMessage(ChatColor.YELLOW + "Uso: /moyo text <color> <duracion_segundos> <mensaje>");
                    p.sendMessage(ChatColor.YELLOW + "Ejemplo: /moyo text RED 5 ¡Hola Mundo!");
                    return true;
                }
                
                ChatColor titleColor;
                try {
                    titleColor = ChatColor.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    p.sendMessage(ChatColor.RED + "Color no v lido. Usa: RED, GOLD, AQUA, etc.");
                    return true;
                }

                int duration;
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Duraci n inv lida. Debe ser un n mero.");
                    return true;
                }

                String msg = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[Moyo] " + ChatColor.RESET + msg);
                p.sendTitle(titleColor + "  MoyoTools  ", ChatColor.GOLD + msg, 10, duration * 20, 10);
                return true;

            case "rayo":
                Location loc = p.getLocation();
                p.getWorld().strikeLightning(loc);
                p.sendTitle(ChatColor.RED + "  \u26A1 RAYO INVOCADO \u26A1  ", "", 10, 40, 10);
                p.sendMessage(ChatColor.DARK_RED + "  Rayo invocado!");
                return true;
            
            case "efecto":
                if (args.length < 4) {
                    p.sendMessage(ChatColor.YELLOW + "Uso: /moyo efecto <efecto> <duracion> <nivel>");
                    return true;
                }
                try {
                    PotionEffectType type = PotionEffectType.getByName(args[1].toUpperCase());
                    int dur = Integer.parseInt(args[2]) * 20;
                    int amp = Integer.parseInt(args[3]) - 1;

                    if (type != null) {
                        p.addPotionEffect(new PotionEffect(type, dur, amp));
                        p.sendTitle(ChatColor.AQUA + "  ✨ EFECTO APLICADO ✨  ", ChatColor.GREEN + args[1], 10, dur, 10);
                        countdown(p, Integer.parseInt(args[2]));
                        p.sendMessage(ChatColor.GREEN + "Efecto aplicado: " + args[1]);
                    } else {
                        p.sendMessage(ChatColor.RED + "Efecto no encontrado.");
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Duraci n o nivel inv lido.");
                }
                return true;

            case "mob":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.YELLOW + "Uso: /moyo mob <nombre_del_mob>");
                    return true;
                }
                String mob = args[1];
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mythicmobs spawn " + mob + " " + p.getName());
                p.sendTitle(ChatColor.LIGHT_PURPLE + "  \u2694 MOB INVOCADO \u2694  ", ChatColor.GOLD + "¡Cuidado!", 10, 40, 10);
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Has invocado el mob: " + mob);
                return true;
            
            case "ruleta":
                return applyRandomEffect(p);

            case "random":
                return applyRandomEffect(p);

            default:
                p.sendMessage(ChatColor.GRAY + "Comando desconocido. Usa /moyo <text|ruleta|rayo|efecto|mob|random>");
                return true;
        }
    }

    private boolean applyRandomEffect(Player p) {
        Random r = new Random();
        int i = r.nextInt(5);
        String eventName = "";
        int duration = 5;

        switch (i) {
            case 0:
                p.getWorld().strikeLightning(p.getLocation());
                eventName = ChatColor.RED + "⚡ Rayo";
                break;
            case 1:
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 1));
                eventName = ChatColor.AQUA + "🏃 Velocidad";
                break;
            case 2:
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration * 20, 1));
                eventName = ChatColor.DARK_GRAY + "👁️ Ceguera";
                break;
            case 3:
                p.performCommand("mythicmobs spawn TestMob " + p.getName());
                eventName = ChatColor.LIGHT_PURPLE + "👹 Mob";
                break;
            case 4:
                p.setFireTicks(duration * 20);
                eventName = ChatColor.GOLD + "🔥 Fuego";
                break;
        }

        p.sendTitle(ChatColor.YELLOW + "  ¡RULETA ACTIVA!  ", eventName, 10, duration * 20, 10);
        countdown(p, duration);
        p.sendMessage(ChatColor.RED + "Evento aleatorio activado: " + eventName);

        return true;
    }

    private void countdown(Player p, int seconds) {
        new BukkitRunnable() {
            int time = seconds;

            @Override
            public void run() {
                if (time <= 0) {
                    p.sendTitle(ChatColor.RED + "  FIN!  ", "", 10, 20, 10);
                    cancel();
                    return;
                }
                p.sendTitle("", ChatColor.GOLD.toString() + ChatColor.BOLD + "⏰ " + time + "s", 0, 20, 0);
                time--;
            }
        }.runTaskTimer(this, 0, 20);
    }
}

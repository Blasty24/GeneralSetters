package com.blasty.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class PluginManager {
    private static PluginManager instance;
    private JavaPlugin plugin;

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask runPotionCheckTimer(long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean hasStrength2 = false;
                boolean hasSpeed2 = false;

                for (PotionEffect effect : p.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.STRENGTH && effect.getAmplifier() == 1) {
                        // Amplifier 1 = Strength II
                        hasStrength2 = true;
                    }
                    if (effect.getType() == PotionEffectType.SPEED && effect.getAmplifier() == 1) {
                        // Amplifier 1 = Speed II
                        hasSpeed2 = true;
                    }
                }

                // If player has both effects at level II
                if (hasStrength2) {
                    p.removePotionEffect(PotionEffectType.STRENGTH);
                } else if (hasSpeed2) {
                    p.removePotionEffect(PotionEffectType.SPEED);
                }
            }
        }, delay, period);
    }

    public BukkitTask runTaskTimer(long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player p : Bukkit.getOnlinePlayers()) {

                for (Material mat : ItemManager.limitList.keySet()) {

                    int limit = ItemManager.limitList.get(mat);

                    // 1. Count total amount in inventory
                    int total = 0;
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item != null && item.getType() == mat) {
                            total += item.getAmount();
                        }
                    }

                    // 2. If total exceeds limit, drop only the extra
                    if (total > limit) {
                        int excess = total - limit;

                        // Remove excess from inventory
                        for (ItemStack item : p.getInventory().getContents()) {
                            if (item == null)
                                continue;
                            if (item.getType() != mat)
                                continue;

                            int amount = item.getAmount();

                            if (amount <= excess) {
                                // Remove entire stack
                                p.getInventory().remove(item);
                                excess -= amount;
                            } else {
                                // Remove part of the stack
                                item.setAmount(amount - excess);
                                excess = 0;
                            }

                            if (excess <= 0)
                                break;
                        }

                        // Drop the excess
                        ItemStack dropped = new ItemStack(mat, total - limit);
                        p.getWorld().dropItem(p.getLocation(), dropped);
                    }
                }

            }

        }, delay, period);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target))
            return;
        if (!(event.getDamager() instanceof Player attacker))
            return;

        // Apply custom knockback
        applyKnockback(attacker, target);
    }

    public void applyKnockback(Player attacker, Player target) {
        if (!canStun(attacker)) {
            return;
        }

        // Vector from attacker to target
        Vector kb = target.getLocation().toVector()
                .subtract(attacker.getLocation().toVector())
                .normalize()
                .multiply(0.5); // tweak horizontal strength

        kb.setY(0.3); // small upward knock
        target.setVelocity(kb);
    }

    public boolean canStun(Player attacker) {
        float cd = attacker.getAttackCooldown(); // 0 = ready, 1 = fully ready
        return cd > 0.9f; // only allow if almost full attack
    }

}
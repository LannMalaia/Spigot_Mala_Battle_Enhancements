package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Rage 
{
	public static void Rage(Player player)
	{
		if(player.getInventory().getItemInMainHand().hasItemMeta())
		{
			if(player.getInventory().getItemInMainHand().getItemMeta().hasLore())
			{
				List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
				if(lores != null)
				{
					for(int count = 0; count < lores.size(); count++)
					{
						if(lores.get(count).matches(".*" + "버서크" + ".*"))
						{
							if(player.getMetadata("Berserk_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "버서크 쿨타임입니다.");
							}
							else
							{
								player.setMetadata("Berserk_Cooldown", new FixedMetadataValue(Main.mother, 1));
								player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 1, 1d, 1d, 0d, 0d);
								
								//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), player.getLocation(), 1, 1, 1, 0, 150);
								if(player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() < 2)
									player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
								player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 2));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§4§l버서크");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Berserk_Cooldown(player), 1200);
							}
						}
					}
				}
			}
		}
	}
}


class Berserk_Cooldown implements Runnable
{
	Player player;
	
	public Berserk_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Berserk_Cooldown", Main.mother);
	}
}

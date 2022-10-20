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

public class Arena_Swift 
{
	public static void Swift (Player player)
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
						if(lores.get(count).matches(".*" + "스위프트" + ".*"))
						{
							if(player.getMetadata("Swift_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "스위프트 쿨타임입니다.");
							}
							else
							{
								player.setMetadata("Swift_Cooldown", new FixedMetadataValue(Main.mother, 1));
								player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 1, 1d, 1d, 1d, 0d);
								//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), player.getLocation().clone().add(0, 1, 0), 1, 1, 1, 0, 150);


								if(player.hasPotionEffect(PotionEffectType.SPEED))
								{
									if(player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() < 3)
										player.removePotionEffect(PotionEffectType.SPEED);
								}
								player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 3));
								

								TitleManager.sendTitle(player, 0, 60, 0, "", "§4§l스위프트");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Swift_Cooldown(player), 1200);
							}
						}
					}
				}
			}
		}
	}
}


class Swift_Cooldown implements Runnable
{
	Player player;
	
	public Swift_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Swift_Cooldown", Main.mother);
	}
}

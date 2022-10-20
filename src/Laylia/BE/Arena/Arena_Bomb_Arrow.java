package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Bomb_Arrow
{
	public static void Arrow_Action(Player player)
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
						if(lores.get(count).matches(".*" + "Æø¹ß È­»ì" + ".*"))
						{
							if(player.getMetadata("Bomb_Arrow_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "Æø¹ß È­»ì ÄðÅ¸ÀÓÀÔ´Ï´Ù.");
							}
							else
							{
								player.setMetadata("Bomb_Arrow_Cooldown", new FixedMetadataValue(Main.mother, 1));
								player.setMetadata("Bomb_Arrow_Action", new FixedMetadataValue(Main.mother, 1));
								player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 150, 0d, 0d, 0d, 0d);
								//ParticleEffect.FIREWORKS_SPARK.send(Bukkit.getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0.1, 150);
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "¡×6¡×lÆø¹ß È­»ì ");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Bomb_Cooldown(player), 1200);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Bomb_Action(player), 400);
							}
						}
					}
				}
			}
		}
	}

	public static void Arrow_Shoot(Arrow arrow)
	{
		arrow.setMetadata("Bomb_Arrow", new FixedMetadataValue(Main.mother, 1));
	}
	
	public static void Arrow_Bomb(Arrow arrow)
	{
		arrow.getLocation().getWorld().createExplosion(arrow.getLocation(), 6, false);
	}
}

class Bomb_Action implements Runnable
{
	Player player;
	
	public Bomb_Action (Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Bomb_Arrow_Action", Main.mother);
	}
}

class Bomb_Cooldown implements Runnable
{
	Player player;
	
	public Bomb_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Bomb_Arrow_Cooldown", Main.mother);
	}
}

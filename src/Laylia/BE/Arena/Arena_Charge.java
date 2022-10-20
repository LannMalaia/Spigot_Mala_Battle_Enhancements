package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Charge
{
	public static void Charge(Player player)
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
						if(lores.get(count).matches(".*" + "대쉬" + ".*"))
						{
							if(player.getMetadata("Charge_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "대쉬 쿨타임입니다.");
							}
							else
							{
								player.setMetadata("Charge_Cooldown", new FixedMetadataValue(Main.mother, 1));
								player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 150, 0d, 0d, 0d, 0d);
								//ParticleEffect.FIREWORKS_SPARK.send(Bukkit.getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0.1, 150);
								player.setVelocity(new Vector(player.getLocation().getDirection().getX() * 2, 0.6, player.getLocation().getDirection().getZ() * 2));
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "§6§l대쉬");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Charge_Cooldown(player), 120);
							}
						}
					}
				}
			}
		}
	}
}


class Charge_Cooldown implements Runnable
{
	Player player;
	
	public Charge_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Charge_Cooldown", Main.mother);
	}
}

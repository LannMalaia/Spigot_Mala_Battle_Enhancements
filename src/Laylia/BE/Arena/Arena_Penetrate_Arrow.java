package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Arena_Penetrate_Arrow
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
						if(lores.get(count).matches(".*" + "���� ȭ��" + ".*"))
						{
							if(player.getMetadata("Penetrate_Arrow_Cooldown").size() != 0)
							{
								ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "���� ȭ�� ��Ÿ���Դϴ�.");
							}
							else
							{
								player.setMetadata("Penetrate_Arrow_Cooldown", new FixedMetadataValue(Main.mother, 1));
								player.setMetadata("Penetrate_Arrow_Action", new FixedMetadataValue(Main.mother, 1));
								player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 150, 0d, 0d, 0d, 0d);
								
								//ParticleEffect.FIREWORKS_SPARK.send(Bukkit.getOnlinePlayers(), player.getLocation(), 0, 0, 0, 0.1, 150);
								
								TitleManager.sendTitle(player, 0, 60, 0, "", "��6��l���� ȭ��");
	
								Bukkit.getScheduler().runTaskLater(Main.mother, new Penetrate_Cooldown(player), 600);
								Bukkit.getScheduler().runTaskLater(Main.mother, new Penetrate_Action(player), 400);
							}
						}
					}
				}
			}
		}
	}

	public static void Arrow_Shoot(Player player, Arrow arrow)
	{
		// ��ġ
		double pos_x = player.getLocation().getX();
		double pos_y = player.getLocation().getY() + 1.6; // ������
		double pos_z = player.getLocation().getZ();
		
		// ����
		double dir_x = player.getLocation().getDirection().getX();
		double dir_y = player.getLocation().getDirection().getY();
		double dir_z = player.getLocation().getDirection().getZ();
		
		//������
		double multiplier = 1;
		int range = 240;
		
		boolean is_blocked = false;
		
		//player_data.sendMessage("x : " + dir_x + ", y : " + dir_y + ", z : " + dir_z);
		
		player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
		for(int i = 3; i < range; i++)
		{
			if(is_blocked)
				break;
			for(double j = 0; j < 8; j++) // ��ƼŬ ��ȯ & ��� üũ
			{
				double x = pos_x + (dir_x * (multiplier * i + j / 8.0));
				double y = pos_y + (dir_y * (multiplier * i + j / 8.0));
				double z = pos_z + (dir_z * (multiplier * i + j / 8.0));
				
				Vector loc = new Vector(x, y, z);
				
				if (player.getWorld().getBlockAt(new Location(player.getWorld(), x, y, z)).getType().isSolid() || i == range - 1)
				{
					is_blocked = true;
				}
				for(Entity entity : player.getWorld().getNearbyEntities(loc.toLocation(player.getWorld()), 1, 1, 1))
				{
					if(entity instanceof LivingEntity)
					{
						LivingEntity c = (LivingEntity)entity;
						//Bukkit.getConsoleSender().sendMessage("" + c.getNoDamageTicks());
						if(c.getNoDamageTicks() <= 0)
							c.damage(20, player);
					}
				}
				player.getWorld().spawnParticle(Particle.CRIT, loc.toLocation(player.getWorld()), 1, 0d, 0d, 0d, 0d);
				//ParticleEffect.CRIT.send(Bukkit.getOnlinePlayers(), , 0, 0, 0, 0, 1);
				//player.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 1, 0, 0, 0, 0);
			}
		}
	}
}

class Penetrate_Action implements Runnable
{
	Player player;
	
	public Penetrate_Action (Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Penetrate_Arrow_Action", Main.mother);
	}
}

class Penetrate_Cooldown implements Runnable
{
	Player player;
	
	public Penetrate_Cooldown(Player p)
	{
		player = p;
	}
	
	public void run()
	{
		player.removeMetadata("Penetrate_Arrow_Cooldown", Main.mother);
	}
}

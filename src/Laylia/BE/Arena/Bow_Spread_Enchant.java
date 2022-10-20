package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import Laylia.BE.Main.Main;


public class Bow_Spread_Enchant
{
	public Bow_Spread_Enchant(Player player, Arrow arrow)
	{
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item != null)
		{
			if(item.getType() == Material.BOW && item.hasItemMeta())
			{
				ItemMeta meta = item.getItemMeta();
				if(meta.hasLore())
				{
					List<String> lores = meta.getLore();
					for(String lore : lores)
					{
						if(lore.contains("확산"))
						{
							if(lore.contains("V"))
							{
								Shoot_Spreading_Arrow(player, 21, 70d, arrow);
							}
							else if(lore.contains("IV"))
							{
								Shoot_Spreading_Arrow(player, 17, 60d, arrow);
							}
							else if(lore.contains("III"))
							{
								Shoot_Spreading_Arrow(player, 13, 50d, arrow);
							}
							else if(lore.contains("II"))
							{
								Shoot_Spreading_Arrow(player, 9, 40d, arrow);
							}
							else if(lore.contains("I"))
							{
								Shoot_Spreading_Arrow(player, 5, 30d, arrow);
							}
						}
						if(lore.contains("연사"))
						{
							if(lore.contains("V"))
							{
								Shoot_Multiple_Arrow(player, 10, arrow);
							}
							else if(lore.contains("IV"))
							{
								Shoot_Multiple_Arrow(player, 8, arrow);
							}
							else if(lore.contains("III"))
							{
								Shoot_Multiple_Arrow(player, 6, arrow);
							}
							else if(lore.contains("II"))
							{
								Shoot_Multiple_Arrow(player, 4, arrow);
							}
							else if(lore.contains("I"))
							{
								Shoot_Multiple_Arrow(player, 2, arrow);
							}
						}
					}
				}
			}
		}
	}
	
	void Shoot_Spreading_Arrow(Player player, int Count, double angle, Arrow a)
	{
		for (int i = 0; i < Count; i++)
		{
			  Arrow arrow = player.launchProjectile(Arrow.class);
			  arrow.setCritical(a.isCritical());
			  arrow.setGlowing(a.isGlowing());
			  arrow.setVelocity(a.getVelocity());
			  arrow.setBounce(false);
			  arrow.setKnockbackStrength(a.getKnockbackStrength());
			  arrow.setDamage(arrow.getDamage());
			  arrow.setMetadata("arrow_remove", new FixedMetadataValue(Main.mother, true));
			  double correction_angle = player.getLocation().getYaw() + 90d + angle / 2d - (angle / Count * 0.5) - angle / Count * i;
			  double sin = Math.sin(Math.toRadians(correction_angle));
			  double cos = Math.cos(Math.toRadians(correction_angle));
			  // 높이차 * sin(차이각)
			  double angle2 = player.getLocation().getDirection().getY() * Math.cos(Math.toRadians(angle / 2d - (angle / Count * 0.5) - angle / Count * i));
			  //Bukkit.getConsoleSender().sendMessage("" + player.getLocation().getYaw() + "//"+ (angle / 2d - (angle / Count * 0.5) - angle / Count * i) + "//" + angle2);
			  
			  //Location temp2 = player.getLocation().add(cos * 3d, angle2 * 3d + player.getEyeHeight() , sin * 3d);
			  //ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
			  
			  if((angle / 2d - (angle / Count * 0.5) - angle / Count * i) <= 1d && (angle / 2d - (angle / Count * 0.5) - angle / Count * i) >= -1d)
				  arrow.remove();
			  else
				  arrow.setVelocity(new Vector(cos, angle2, sin).multiply(a.getVelocity().distance(new Vector(0, 0, 0).multiply(-1d))));
		}
		
		/*
		Vector fake_dir = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw())), 3d * Math.cos(Math.PI), Math.sin(Math.toRadians(player.getLocation().getYaw()))).normalize();
		Vector x1 = new Vector(-fake_dir.getZ(), 0d, fake_dir.getX()).normalize();
		Vector x2 = fake_dir.crossProduct(x1).normalize();

		for(int i = 0; i < Count; i++)
		{
			//double x = (3d) * (Math.sin(Math.toRadians((180d - angle) / 2d) + (double)i / 90d * Math.toRadians(angle)));
			//double z = (3d) * (Math.cos(Math.toRadians((180d - angle) / 2d) + (double)i / 90d * Math.toRadians(angle)));
			//double x = (3d) * Math.sin(Math.toRadians((angle / 2d) + angle / Count * (double)i));
			//double z = (3d) * Math.cos(Math.toRadians((angle / 2d) + angle / Count * (double)i));
			double x = (3d) * (Math.sin(Math.toRadians((angle / -2d) + 90d + angle / Count * (double)i)));
			double z = (3d) * (Math.cos(Math.toRadians((angle / -2d) + 90d + angle / Count * (double)i)));
			Location temp2 = player.getLocation().add(x1.clone().multiply(x)).add(x2.clone().multiply(z));
			temp2.setDirection(new Vector(0, 0, 0));
			ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);

			Arrow arrow = player.launchProjectile(Arrow.class);
			arrow.setCritical(a.isCritical());
			arrow.setGlowing(a.isGlowing());
			arrow.setBounce(false);
			arrow.setKnockbackStrength(a.getKnockbackStrength());
			arrow.setVelocity(new Vector(temp2.getX() - player.getLocation().getX(), a.getVelocity().getY(), temp2.getZ() - player.getLocation().getZ()).multiply(2d));//.multiply(a.getVelocity()));
		}
		*/
	}

	void Shoot_Multiple_Arrow(Player player, int Count, Arrow a)
	{
		for(int i = 1; i < Count; i++)
		{
			 Arrow arrow = player.launchProjectile(Arrow.class);
			  arrow.setCritical(a.isCritical());
			  arrow.setGlowing(a.isGlowing());
			  arrow.setBounce(false);
			  arrow.setKnockbackStrength(a.getKnockbackStrength());
			  arrow.setDamage(arrow.getDamage());
			  arrow.setMetadata("arrow_remove", new FixedMetadataValue(Main.mother, true));

			  arrow.setVelocity(a.getVelocity().multiply(1d / (double)Count * (double)i));
		}
	}
}

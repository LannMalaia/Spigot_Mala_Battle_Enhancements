package Laylia.BE.LegacyEnchant;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gmail.nossr50.api.ExperienceAPI;

public class Wave_Power_Enchant
{
	public static void Sword_Wave_Attack(Player player)
	{
		List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		if(lores != null)
		{
			for(int count = 0; count < lores.size(); count++)
			{
				if(lores.get(count).matches(".*" + "검기" + ".*"))
				{
					if(lores.get(count).contains("III"))
					{
						Location loc = player.getLocation();
						loc.add(loc.getDirection().getX(), 1 + loc.getDirection().getY(), loc.getDirection().getZ());
						loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
						Sword_Wave_Attack_Progress(player, 2);
					}
					else if(lores.get(count).contains("II"))
					{
						Location loc = player.getLocation();
						loc.add(loc.getDirection().getX(), 1 + loc.getDirection().getY(), loc.getDirection().getZ());
						loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
						Sword_Wave_Attack_Progress(player, 1);
					}
					else if(lores.get(count).contains("I"))
					{
						Location loc = player.getLocation();
						loc.add(loc.getDirection().getX(), 1 + loc.getDirection().getY(), loc.getDirection().getZ());
						loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0d, 0d, 0d, 0d);
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f, 1);
						Sword_Wave_Ranged_Attack(player);
					}
				}
			}
		}
	}
	
	public static void Sword_Wave_Attack_Progress(Player player, int power)
	{
		double angle = 120;
		
		// 검기 그리기
		Vector fake_dir = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw())), 3d * Math.cos(Math.random() * Math.PI ), Math.sin(Math.toRadians(player.getLocation().getYaw()))).normalize();
		Vector x1 = new Vector(-fake_dir.getZ(), 0d, fake_dir.getX()).normalize();
		Vector x2 = fake_dir.crossProduct(x1).normalize();
		
		for(int i = 0; i < 90; i++)
		{
			double x = (2d + power * 3d) * (Math.sin(Math.toRadians((180d - angle) / 2d) + (double)i / 90d * Math.toRadians(angle)));
			double z = (2d + power * 3d) * (Math.cos(Math.toRadians((180d - angle) / 2d) + (double)i / 90d * Math.toRadians(angle)));
			Location temp2 = player.getLocation().add(x1.clone().multiply(x)).add(x2.clone().multiply(z)).add(0,1,0);
			temp2.setDirection(new Vector(0, 0, 0));
			temp2.getWorld().spawnParticle(Particle.CLOUD, temp2, 1, 0d, 0d, 0d, 0d);
			//ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
		}
		
		// 범위 판정
		List<Entity> entities = player.getWorld().getEntities();
		for(int i = 0; i < entities.size(); i++)
		{
			Entity temp = entities.get(i);
			Location loc = temp.getLocation();
			loc.subtract(player.getLocation());
			if(Math.sqrt(loc.getX() * loc.getX() + loc.getZ() * loc.getZ()) < (2d + power * 3d) && loc.getY() < 3 && loc.getY() > -2)
			{
				if(temp instanceof LivingEntity)
				{
					double vec = player.getLocation().getDirection().normalize().dot(loc.toVector().normalize());
					if(vec > Math.cos(Math.toRadians(angle / 2)))
					{
						int level = ExperienceAPI.getLevel(player, "swords");
						int damage = level / 300;
						LivingEntity temp2 = (LivingEntity)temp;
						temp2.damage(Math.min(6, damage), player);
					}
				}
			}
		}
	}

	public static void Sword_Wave_Ranged_Attack(Player player)
	{
		Vector p_dir = player.getLocation().getDirection();
		List<Entity> entities = player.getNearbyEntities(5.7d, 5.7d, 5.7d);
		boolean activated = false;
		for(Entity entity : entities)
		{
			if(entity instanceof LivingEntity && !activated)
			{
				Location loc = entity.getLocation();
				Location p_loc = player.getLocation();
				for(double i = 0; i < 5.7d; i += 0.1)
				{
					p_loc.add(p_dir.getX() * 0.1, p_dir.getY() * 0.1, p_dir.getZ() * 0.1);
					//player.sendMessage("loc\n" + p_dir.getX() + "\n" + p_dir.getY() + "\n" + p_dir.getZ());
					//player.sendMessage("p_loc\n" + p_dir.getX() + "\n" + p_dir.getY() + "\n" + p_dir.getZ());
					
					if(loc.getX() < p_loc.getX() + 0.5 && loc.getX() > p_loc.getX() - 0.5 && loc.getZ() < p_loc.getZ() + 0.5 && loc.getZ() > p_loc.getZ() - 0.5 && loc.getY() < p_loc.getY() + 1.2 && loc.getY() > p_loc.getY() - 1.2)
					{
						activated = true;
						int level = ExperienceAPI.getLevel(player, "swords");
						int damage = level / 300;
						((LivingEntity) entity).damage(Math.min(6, damage), player);
						break;
					}
				}
			}
		}
	}

	public static boolean Is_Has_Sword_Wave_One(Player player)
	{
		if(player.getInventory().getItemInMainHand().hasItemMeta())
		{
			List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
			if(lores != null)
			{
				for(int count = 0; count < lores.size(); count++)
				{
					if(lores.get(count).matches(".*" + "검기" + ".*"))
					{
						if(lores.get(count).contains("I"))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
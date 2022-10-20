package Laylia.BE.Archery;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import Laylia.BE.Main.Main;

public class Archery_Mastery
{
	// 화살 발사시
	public static void Arrow_Add_Damage_Meta(Player _player, Projectile _projectile, float _force)
	{
		int concent_value = Archery_Concentrate_Manager.Instance.Get_Player_Concentrate(_player);
		
		int mastery_damage = 0;
		int concentrate_damage = 0;
		_projectile.setMetadata("be.archery.arrow", new FixedMetadataValue(Main.mother, mastery_damage));
		
		// 마스터리 효과
		mastery_damage += _force * (int)Manager_Archery_Ability.Instance.Get_Data(_player, Manager_Archery_Ability.key_mastery_range_dmg);
		_projectile.setMetadata("be.archery.mastery_dmg", new FixedMetadataValue(Main.mother, mastery_damage));

		// 집중 효과
		if (concent_value > 0)
		{
			concentrate_damage += (1 + concent_value) * concent_value * 0.5d * mastery_damage;
			_projectile.setMetadata("be.archery.concent_dmg", new FixedMetadataValue(Main.mother, concentrate_damage));
			_projectile.setVelocity(_projectile.getVelocity().multiply(1.0 + 0.3 * concent_value));
			Bukkit.getScheduler().runTask(Main.mother, new Concentrate_Arrow_Effect(_projectile));
		}
	}
	
	// 벽&바닥에 맞을시
	public static void When_Hit_Arrow_On_Ground(Arrow arrow)
	{
		/*
		if(arrow.getShooter() instanceof Player)
		{
			//((Entity)arrow.getShooter()).sendMessage("바닥에 맞았다");
			Player player = (Player)arrow.getShooter();
			double damage = arrow.getDamage();
			damage += ExperienceAPI.getLevel(player, "archery") / 200;
			if(player.getMetadata("Mcmmo_Bow_Skill_Use").size() != 0)
			{
				damage *= (120 + (int)(ExperienceAPI.getLevel(player, "archery") / 100)) / 100d;
			}
			//player.sendMessage("" + arrow.spigot().getDamage());
			
			if(arrow.getMetadata("MC_Arrow_Bomb").size() != 0)
			{
				arrow.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, arrow.getLocation(), 1, 0d, 0d, 0d, 0d);
				arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2, 1);

				RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
				Permission perm = rsp.getProvider();
				perm.playerAdd((Player)arrow.getShooter(), "nocheatplus.checks.fight");

				for(Entity entity : arrow.getNearbyEntities(1.5, 1.5, 1.5))
				{
					if(entity instanceof Creature)
					{
						if(entity != arrow.getShooter())
						{
							((Creature)entity).damage(damage / 2d, arrow);
						}
					}
				}
				perm.playerRemove((Player)arrow.getShooter(), "nocheatplus.checks.fight");
				arrow.remove();
			}
			else if(arrow.getMetadata("MC_Arrow_Smoke").size() != 0)
			{
				for(Entity entity : arrow.getNearbyEntities(2, 2, 2))
				{
					if(entity instanceof Creature)
					{
						if(entity != arrow.getShooter())
						{
							((Creature)entity).setTarget(null);
							((Creature)entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
						}
					}
				}
				for(int i = 0; i < 30; i++)
				{
					Bukkit.getScheduler().runTaskLater(Main.mother, new Bow_Smoking(arrow.getLocation()), i * 4);
				}
				arrow.remove();
			}
		}
		*/
		
		if (arrow.hasMetadata("be.archery.concent_dmg"))
			arrow.remove();
	}
	
	// 원거리 공격시
	public static void When_Hit_Arrow_On_Entity(Player player, EntityDamageByEntityEvent event)
	{
		Arrow arrow = (Arrow)event.getDamager();
		if(arrow.getShooter() instanceof Player && event.getEntity() instanceof LivingEntity)
		{
			double damage = event.getDamage();
			LivingEntity target = (LivingEntity)event.getEntity();

			if(arrow.hasMetadata("arrow_no_time"))
			{
				Bukkit.getScheduler().runTaskLater(Main.mother, () -> {
					if(!target.isDead())
						target.setNoDamageTicks(0);				
				}, 1);
			}
			
			// 활로 쏜 화살이 아니면 스킵
			if(!arrow.hasMetadata("be.archery.arrow"))
				return;
			
			// 마스터리 효과
			if(arrow.hasMetadata("be.archery.mastery_dmg"))
				damage += arrow.getMetadata("be.archery.mastery_dmg").get(0).asInt();
		
			// 집중 효과
			if(arrow.hasMetadata("be.archery.concent_dmg"))
				damage += arrow.getMetadata("be.archery.concent_dmg").get(0).asInt();

			// 은밀 사격 효과
			if(target instanceof Mob)
			{
				if(((Mob)target).getTarget() != player)
				{
					target.getWorld().spawnParticle(Particle.COMPOSTER, event.getEntity().getLocation().clone().add(0, 1, 0),
							10, 0.2d, 0.2d, 0.2d, 0d);
					damage += (int)Manager_Archery_Ability.Instance.Get_Data(player, Manager_Archery_Ability.key_support_shot_dmg);
				}
			}
				
			event.setDamage(damage);

			/*
			double player_counter_angle = ((Creature)event.getEntity()).getEyeLocation().getYaw();
			double damager_angle =player.getEyeLocation().getYaw();
			double angle_range = 60;

			if(player_counter_angle < 0) player_counter_angle += 360;
			if(damager_angle < 0) damager_angle += 360;
			if(player_counter_angle - damager_angle < angle_range && player_counter_angle - damager_angle > -angle_range)
			{
				if(player.isSneaking() && ExperienceAPI.getLevel(player, "archery") >= 1000 && player.hasPermission("battleEnhancements.mcmmo.archery.sneak_shot"))
				{
					ActionBarAPI.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "[ 적의 뒤에 화살을 적중시켰다! ]");

					arrow.getLocation().getWorld().spawnParticle(Particle.CRIT, event.getEntity().getLocation().clone().add(0, 1, 0), 20, 0.2d, 0.2d, 0.2d, 0d);
					event.setDamage(event.getDamage() * 2.5d);
				}
			}
			
			if(arrow.getMetadata("MC_Arrow_Bomb").size() != 0)
			{
				arrow.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, arrow.getLocation(), 1, 0d, 0d, 0d, 0d);
				arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2, 0f);

				RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
				Permission perm = rsp.getProvider();
				perm.playerAdd((Player)arrow.getShooter(), "nocheatplus.checks.fight");
				
				for(Entity entity : event.getEntity().getNearbyEntities(2.5, 2.5, 2.5))
				{
					if(entity instanceof Animals)
					{
						continue;
					}
					if(entity instanceof Creature)
					{
						if(entity != arrow.getShooter() && entity != event.getEntity())
						{
							((Creature)entity).damage(event.getDamage() / 3d, arrow);
						}
					}
				}
								
				event.setDamage(event.getDamage());
				perm.playerRemove((Player)arrow.getShooter(), "nocheatplus.checks.fight");
				arrow.remove();
			}
			else if(arrow.getMetadata("MC_Arrow_Smoke").size() != 0)
			{
				for(Entity entity : arrow.getNearbyEntities(2, 2, 2))
				{
					if(entity instanceof Creature)
					{
						if(entity instanceof Animals)
						{
							continue;
						}
						if(entity != arrow.getShooter())
						{
							((Creature)entity).setAI(false);
							((Creature)entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
							((Creature)entity).setAI(true);
						}
					}
				}
				for(int i = 0; i < 30; i++)
				{
					Bukkit.getScheduler().runTaskLater(Main.mother, new Bow_Smoking(arrow.getLocation()), i * 4);
				}
				arrow.remove();
			}
			else if(arrow.getMetadata("MC_Arrow_SuperPoison").size() != 0)
			{
				for(Entity entity : arrow.getNearbyEntities(2, 2, 2))
				{
					if(entity instanceof Creature)
					{
						if(entity != arrow.getShooter())
						{
							((Creature)entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
						}
					}
				}
				for(int i = 0; i < 30; i++)
				{
					Bukkit.getScheduler().runTaskLater(Main.mother, new Bow_Smoking(arrow.getLocation()), i * 4);
				}
				arrow.remove();
			}
			*/
		}

	}
	
	// 근접 공격시
	public static double Set_Melee_Damage(Player player, EntityDamageByEntityEvent event)
	{
		// 활 든 거 맞소?
		Material handitem = player.getInventory().getItemInMainHand().getType();
		if (!Manager_Archery_Ability.Is_Bow(handitem))
			return event.getDamage();
		
		// 권한 있소?
		if(!(player.hasPermission(Manager_Archery_Ability.perm_mastery) || player.hasPermission("*")))
			return event.getDamage();

		// 피해량을 정합시다
		double damage = event.getDamage();
		// 보우 마스터리 데미지 추가
		damage += (int)Manager_Archery_Ability.Instance.Get_Data(player, Manager_Archery_Ability.key_mastery_melee_dmg);

		return damage;	
	}

	/*
	public static boolean Get_Sneak_Chance(Player player)
	{
		Material handitem = player.getInventory().getItemInMainHand().getType();
		switch(handitem)
		{
		default:
			return false;
		case BOW:
			if(player.hasPermission("battleEnhancements.mcmmo.archery.sneak_shot") || player.hasPermission("*"))
			{
				if(player.isSneaking() && ExperienceAPI.getLevel(player, "archery") >= 1000)
				{
					return true;
				}
			}
			return false;
		}
	}
	*/
	
	public static double Check_Bow_BackStep_Enchant(Player player)
	{
		double addictive_damage = 0;
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(player.getInventory().getArmorContents()[0]);
		items.add(player.getInventory().getArmorContents()[1]);
		items.add(player.getInventory().getArmorContents()[2]);
		items.add(player.getInventory().getArmorContents()[3]);
		items.add(player.getInventory().getItemInMainHand());
		items.add(player.getInventory().getItemInOffHand());
		for(ItemStack item : items)
		{
			if(item != null)
			{
				if(item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if(meta.hasLore())
					{
						List<String> lores = meta.getLore();
						if(lores != null)
						{
							for(int count = 0; count < lores.size(); count++)
							{
								if(lores.get(count).contains("[스킬강화] 후퇴 사격"))
								{
									if(lores.get(count).contains("III"))
									{
										addictive_damage += 5;
									}
									else if(lores.get(count).contains("II"))
									{
										addictive_damage += 4;
									}
									else if(lores.get(count).contains("I"))
									{
										addictive_damage += 3;
									}
								}
							}
						}
					}
				}
			}
		}
		return addictive_damage;
	}
}

/*
class Bow_Smoking implements Runnable
{
	Location loc;
	
	public Bow_Smoking(Location _loc)
	{
		loc = _loc;
	}
	
	public void run()
	{
		for(Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2))
		{
			if(entity instanceof Creature)
			{
				if(entity.getMetadata("cannot_target_anyone").size() == 0)
				{
					((Creature)entity).setTarget(null);
					entity.setMetadata("cannot_target_anyone", new FixedMetadataValue(Main.mother, true));
					((Creature)entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
					Bukkit.getScheduler().runTaskLater(Main.mother, new Bow_Cancel_Targeting(entity), 100);
				}
			}
			if(entity instanceof Player)
			{
				((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
			}
		}
		loc.getWorld().spawnParticle(Particle.SMOKE_LARGE,loc, 400, 2d, 2d, 2d, 0d);
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
	}
}

class Bow_Cancel_Targeting implements Runnable
{
	Entity entity;
	
	public Bow_Cancel_Targeting(Entity _entity)
	{
		entity = _entity;
	}
	
	public void run()
	{
		entity.removeMetadata("cannot_target_anyone", Main.mother);
	}
}
*/

class Concentrate_Arrow_Effect implements Runnable
{
	Projectile arrow;
	
	Vector before_loc, current_loc;
	double radius = 1.0;
	double sub_radius = 0.6;
	
	public Concentrate_Arrow_Effect(Projectile _arrow)
	{
		arrow = _arrow;
		before_loc = arrow.getLocation().toVector();
		current_loc = arrow.getLocation().toVector();

		if(arrow.hasMetadata("be.archery.concent_dmg"))
			radius += arrow.getMetadata("be.archery.concent_dmg").get(0).asInt() / 50d;
		radius = Math.min(4.5d, radius);
		sub_radius = radius * 0.6d;
		
	    Vector x1 = new Vector(-arrow.getVelocity().normalize().getZ(), 0.0D, arrow.getVelocity().normalize().getX()).normalize();
	    Vector x2 = arrow.getVelocity().normalize().crossProduct(x1).normalize();
	    
	    for (int i = 0; i < 90; i++)
	    {
	    	Location temp2 = arrow.getLocation().add(arrow.getVelocity().multiply(0.5d)).add(x1.clone().multiply(radius * Math.sin(i / 90.0D * 3.141592653589793D * 2.0D))).add(x2.clone().multiply(radius * Math.cos(i / 90.0D * 3.141592653589793D * 2.0D)));
	    	temp2.setDirection(new Vector(0, 0, 0));
	    	temp2.getWorld().spawnParticle(radius < 3.0 ? Particle.CRIT : Particle.END_ROD,
	    			temp2, 1, 0.0D, 0.0D, 0.0D, 0.0D);
	    	
	    	if(radius > 3.0)
	    	{
		    	temp2 = arrow.getLocation().add(arrow.getVelocity().multiply(2d)).add(x1.clone().multiply(sub_radius * Math.sin(i / 90.0D * 3.141592653589793D * 2.0D))).add(x2.clone().multiply(sub_radius * Math.cos(i / 90.0D * 3.141592653589793D * 2.0D)));
		    	temp2.setDirection(new Vector(0, 0, 0));
		    	temp2.getWorld().spawnParticle(Particle.END_ROD,
		    			temp2, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			    arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.6f, 1f);
	    	}
	    }
	    
	    arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.6f, 2f);
	}
	
	public void run()
	{
		if(arrow == null)
			return;
		if(arrow.isDead())
			return;
		if(!arrow.isValid())
			return;
		
		current_loc = arrow.getLocation().toVector();
		Vector gap = current_loc.clone().subtract(before_loc);
		double distance = current_loc.distance(before_loc);
		
		for(double i = 0; i < distance; i += 0.2)
		{
			Location loc = before_loc.clone().add(gap.clone().normalize().multiply(i)).toLocation(arrow.getWorld());
			arrow.getWorld().spawnParticle(radius <= 3.0 ? Particle.CRIT_MAGIC : Particle.FIREWORKS_SPARK, loc, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		}
		
		before_loc = current_loc.clone();
		Bukkit.getScheduler().runTaskLater(Main.mother, this, 2);
	}
}

/*
class Bow_Concentrate implements Runnable
{
	Player player;
	int power;
	int counter = -40;
	ItemStack item;
	
	public Bow_Concentrate(Player _player, int _power)
	{
		player = _player;
		power = _power;
		item = player.getInventory().getItemInMainHand();
	}
	
	public void run()
	{
		counter++;
		if(!Archery_Event.drawing_persons.contains(player.getName()))
		{
			return;
		}
		
		if(player.hasMetadata("battleEnhancements.bow.concentrate"))
		{
			// 집중도가 아직 적으면 더해줌
			if(counter > 20 + 10 * power)
			{
				if(power < Bow_Skill.Get_Concentrate_Level(player))
				{
					power++;
					counter = 0;
				}
			}
			player.setMetadata("battleEnhancements.bow.concentrate_level", new FixedMetadataValue(Main.mother, power));
			
			for(AttributeModifier am : player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers())
			{
				if(am.getName().equals("battleEnhancements.bow.concentrate"))
				{
					player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(am);
				}
			}
			player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier("battleEnhancements.bow.concentrate", -0.015 * power, Operation.ADD_NUMBER));
			
			Bukkit.getScheduler().runTaskLater(Main.mother, this, 1);
		}
	}
}

class Bow_Concentrate_Shot implements Runnable
{
	Player player;
	int power = 0;
	Location start_loc, final_loc;
	Vector dir;
	
	boolean is_first_move = true;
	
	public Bow_Concentrate_Shot(Player _player, int _power)
	{
		player = _player;
		power = _power;

		dir = player.getLocation().getDirection().clone();
		List<Block> temp = player.getLineOfSight(null, 30);

		start_loc = player.getEyeLocation().clone();
		final_loc = new Location(player.getWorld(), dir.getX() * 30d, dir.getY() * 30d , dir.getZ() * 30d );
		if(temp.size() != 0)
		{
			for(int i = 0; i < temp.size(); i++)
			{
				if(temp.get(i).getType().isSolid())
				{
					final_loc = temp.get(i).getLocation();
					break;
				}
			}
		}
	}
	
	public void run()
	{
		if(power <= 0) return;
		
		if(is_first_move)
		{
			is_first_move = false;

			player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 2, 2);
			Draw_Particle();
			Bukkit.getScheduler().runTaskLater(Main.mother, this, 20);
		}
		else
		{
			for(Damageable d : get_damageable_entity())
			{
				player.setMetadata("battleenhancements.bow.skill", new FixedMetadataValue(Main.mother, true));
				d.getWorld().playSound(d.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
				d.damage(power * 8, player);
				player.removeMetadata("battleenhancements.bow.skill", Main.mother);
			}
		}
	}
	
	void Draw_Particle()
	{
		Location temp = start_loc.clone();
		for(float i = 0f; i < start_loc.distance(final_loc); i += 0.1f)
		{
			temp.add(start_loc.getDirection().clone().multiply(0.1f));
			temp.getWorld().spawnParticle(Particle.CRIT, temp, 1, 0d, 0d, 0d, 0d);
		}
	}
	
	List<Damageable> get_damageable_entity()
	{
		List<Damageable> entities = new ArrayList<Damageable>();
		Location temp = start_loc.clone();
		
		for(float i = 0f; i < start_loc.distance(final_loc); i += 2f)
		{
			temp.add(start_loc.getDirection().clone().multiply(2f));
			for(Entity e : temp.getWorld().getNearbyEntities(temp, 2d, 2d, 2d))
			{
				if(e == player) continue;
				if(e instanceof Damageable)
				{
					entities.add((Damageable)e);
				}
			}
		}
		
		return entities;
	}
}
*/











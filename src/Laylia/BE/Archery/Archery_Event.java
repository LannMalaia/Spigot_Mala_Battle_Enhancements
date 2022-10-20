package Laylia.BE.Archery;


import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import Laylia.BE.Main.MMOlib_Event;

public class Archery_Event implements Listener
{
	@EventHandler
	public void mala_PlayerAction(PlayerInteractEvent event)
	{
		Manager_Archery_Ability maa = Manager_Archery_Ability.Instance;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			maa.Archery_Active_Ready(event.getPlayer());
		}
		if(event.getAction() == Action.RIGHT_CLICK_AIR)
		{
			maa.Archery_Concentrate_Ready(event.getPlayer());
		}
	}

	@EventHandler
	public void mala_PlayerChangeItem(PlayerItemHeldEvent event)
	{
		Archery_Concentrate_Manager.Instance.Remove_Player_Concentrate(event.getPlayer());
	}
	
	@EventHandler
	public void mala_Player_Shoot_Arrow(EntityShootBowEvent event)
	{
		
		if(event.getEntity() instanceof Player && event.getProjectile() instanceof Projectile)
		{
			Player player = (Player)event.getEntity();
			
			// 플레이어가 활로 쏜 게 아니라면 그냥 스킵해도 됨
			if (!Manager_Archery_Ability.Is_Bow(event.getBow().getType()))
				return;
						
			// 비행 중 사격 방지
			if(player.isFlying())
			{
				player.sendMessage(ChatColor.RED + "[ 비행중에는 공격이 불가합니다. ]");
				player.setFlying(false);
				event.setCancelled(true);
				return;
			}
			
			// 궁술에 대한 사항들 추가
			Archery_Mastery.Arrow_Add_Damage_Meta(player, (Projectile)event.getProjectile(), event.getForce());
			
			// 집중 목록에서 플레이어 제거
			Archery_Concentrate_Manager.Instance.Remove_Player_Concentrate(player);
			
			if(event.getProjectile() instanceof Arrow)
			{
				if(player.isFlying() && !((Player)event.getEntity()).hasPermission("*"))
				{
					player.sendMessage(ChatColor.RED + "비행중에는 사격이 불가능합니다.");
					event.getProjectile().remove();
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void mala_Arrow_Hit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Arrow)
		{
			if(!event.getEntity().getLocation().clone().add(event.getEntity().getVelocity()).getBlock().isEmpty())
			{
				Archery_Mastery.When_Hit_Arrow_On_Ground((Arrow)event.getEntity());
			}
		}
	}

	@EventHandler
	public void mala_PlayerLogin(PlayerLoginEvent event)
	{
		// 집중 목록에서 플레이어 제거
		Archery_Concentrate_Manager.Instance.Remove_Player_Concentrate(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void mala_EntityDamagedOnEntity(EntityDamageByEntityEvent event)
	{
		
		// 근거리 공격
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player
				&& event.getCause() == DamageCause.ENTITY_ATTACK)
		{
			// MMOLIB 특수, 만약 스킬이나 마법 공격이라면 스킵
			if(!MMOlib_Event.Attack_Is_NormalAttack((LivingEntity)event.getEntity()))
				return;
			
			Player player = (Player)event.getDamager();
			double damage = Archery_Mastery.Set_Melee_Damage(player, event);
			event.setDamage(damage);
		}
		
		// 원거리 공격
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Arrow
				&& event.getCause() == DamageCause.PROJECTILE)
		{
			// MMOLIB 특수, 만약 스킬이나 마법 공격이라면 스킵
			if(!MMOlib_Event.Attack_Is_NormalAttack((LivingEntity)event.getEntity()))
				return;
			
			if(((Arrow)event.getDamager()).getShooter() instanceof Player)
			{
				Player player = (Player)((Arrow)event.getDamager()).getShooter();
				Archery_Mastery.When_Hit_Arrow_On_Entity(player, event);
				
				/*
				LivingEntity entity = (LivingEntity)event.getEntity();
				entity.setMetadata("super_targeting", new FixedMetadataValue(Main.mother, true));
				entity.setTarget(player);
				
				for(Entity mob : entity.getNearbyEntities(7, 7, 7))
				{
					if(mob instanceof Creature)
					{
						mob.setMetadata("super_targeting", new FixedMetadataValue(Main.mother, true));
						if(((Creature)mob).getTarget() == null)
							((Creature)mob).setTarget((LivingEntity)((Arrow)event.getDamager()).getShooter());
					}
				}
				*/
			}
		}
	}

	@EventHandler
	public void mala_Move(PlayerMoveEvent event)
	{
		if(event.getFrom().getY() < event.getTo().getY())
		{
			// 스킬 사용
			Manager_Archery_Ability mma = Manager_Archery_Ability.Instance;
			mma.Archery_Active_Use(event.getPlayer());
		}
	}

	/* 타게팅 관련
	@EventHandler
	public void mala_Sneaking_Move(EntityTargetLivingEntityEvent event)
	{
		if(event.getEntity().hasMetadata("super_targeting"))
		{
			return;
		}
		if(event.getTarget() instanceof Player && !event.getEntity().hasMetadata("LE.Boss"))
		{
			Player player = (Player)event.getTarget();
			//if(event.getEntity().getLocation().distance(player.getLocation()) >= Math.min(4, (int)(14 - Math.min(10, ExperienceAPI.getLevel(player, "archery") / 200))) && Bow_Mastery.Get_Sneak_Chance(player))
			if(event.getEntity().getLocation().distance(player.getLocation()) >= 10 && Bow_Mastery.Get_Sneak_Chance(player))
			{
				ActionBarAPI.sendActionBar(player, "" + ChatColor.GRAY + ChatColor.BOLD + "[ �쟻�쓽 �뮘?���? 湲됱?���뻽�떎. ]");
				event.setCancelled(true);
			}
		}
		if(event.getEntity().getMetadata("cannot_target_anyone").size() != 0)
		{
			event.setCancelled(true);
		}
	}
	*/
}

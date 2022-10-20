package Laylia.BE.Crossbow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import Laylia.BE.Main.MMOlib_Event;

public class Crossbow_Event implements Listener
{
	@EventHandler
	public void mala_PlayerAction(PlayerInteractEvent event)
	{
		Manager_Crossbow_Ability mca = Manager_Crossbow_Ability.Instance;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			mca.Crossbow_Active_Ready(event.getPlayer());
		}
	}
	
	@EventHandler
	public void mala_Player_Shoot_Arrow(EntityShootBowEvent event)
	{
		if(event.getEntity() instanceof Player && event.getProjectile() instanceof Projectile)
		{
			Player player = (Player)event.getEntity();
			
			// 플레이어가 석궁으로 쏜 게 아니라면 그냥 스킵해도 됨
			if (!Manager_Crossbow_Ability.Is_Crossbow(event.getBow().getType()))
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
			Crossbow_Mastery.Arrow_Add_Damage_Meta(player, (Projectile)event.getProjectile(), event.getForce());
						
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
	
	@EventHandler
	public void mala_PlayerChangeItem(PlayerItemHeldEvent event)
	{
		Crossbow_Counting_Manager.Instance.Remove_Player_Counting(event.getPlayer());
	}

	@EventHandler
	public void mala_EntityDamagedOnEntity(EntityDamageByEntityEvent  event)
	{
		// 근거리 공격
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player
				&& event.getCause() == DamageCause.ENTITY_ATTACK)
		{
			// MMOLIB 특수, 만약 스킬이나 마법 공격이라면 스킵
			if(!MMOlib_Event.Attack_Is_NormalAttack((LivingEntity)event.getEntity()))
				return;
			
			Player player = (Player)event.getDamager();
			double damage = Crossbow_Mastery.Set_Melee_Damage(player, event);
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
				Crossbow_Mastery.When_Hit_Arrow_On_Entity(player, event);
			}
		}
	}

	@EventHandler
	public void mala_Move(PlayerMoveEvent event)
	{
		if(event.getFrom().getY() < event.getTo().getY())
		{
			// 스킬 사용
			Manager_Crossbow_Ability mca = Manager_Crossbow_Ability.Instance;
			mca.Crossbow_Active_Use(event.getPlayer());
		}
	}
}

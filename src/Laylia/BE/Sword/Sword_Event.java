package Laylia.BE.Sword;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import Laylia.BE.Main.MMOlib_Event;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;

public class Sword_Event implements Listener
{
	@EventHandler
	public void mala_PlayerAction(PlayerInteractEvent event)
	{		
		// 플레이어가 좌클릭
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			Manager_Sword_Ability msa = Manager_Sword_Ability.Instance;
			msa.Sword_Active_Use(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void mala_EntityDamagedOnEntity(EntityDamageByEntityEvent  event)
	{
		//Bukkit.broadcastMessage("" + event.getDamage());
		
		/*
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player
				&& event.getCause() == DamageCause.ENTITY_ATTACK)
		{
			// MMOLIB 특수, 만약 스킬이나 마법 공격이라면 스킵
			if(!MMOlib_Event.Attack_Is_NormalAttack((LivingEntity)event.getEntity()))
				return;
			
			Player player = (Player)event.getDamager();

			// 비행 중 공격 방지
			if(player.isFlying())
			{
				player.sendMessage(ChatColor.RED + "[ 비행중에는 공격이 불가합니다. ]");
				player.setFlying(false);
				event.setCancelled(true);
				return;
			}
			
			double damage = Sword_Mastery.Set_Damage(player, event);
			event.setDamage(damage);
		}
		*/
		if(event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity)
		{
			// 소드 마스터리 반격 처리
			Sword_Mastery.Counter(event);
		}
	}
	
	@EventHandler
	public void mala_PlayerAttack(PlayerAttackEvent event)
	{
		if(!MMOlib_Event.Attack_Is_NormalAttack(event.getAttack()))
			return;
		event.getDamage().add(Sword_Mastery.Get_Damage(event.getPlayer(), event.getEntity()));
		// Bukkit.broadcastMessage("axe damage = " + Axe_Mastery.Get_Damage(event.getPlayer()));
		
	}
}

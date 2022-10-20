package Laylia.BE.Axe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import Laylia.BE.Main.MMOlib_Event;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.AttackMetadata;

public class Axe_Event implements Listener
{
	
	@EventHandler
	public void mala_PlayerAction(PlayerInteractEvent event)
	{
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
			{
				Manager_Axe_Ability.Axe_Active_Ready(event.getPlayer());				
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void mala_EntityDamagedOnEntity(EntityDamageByEntityEvent  event)
	{
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player && event.getCause() == DamageCause.ENTITY_ATTACK)
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
		}
	}

	@EventHandler
	public void mala_PlayerAttack(PlayerAttackEvent event)
	{
		AttackMetadata ar = event.getAttack();
		// Bukkit.broadcastMessage("damage = " + ar.getDamage());
		// if (!(event.getEntity() instanceof Player))
		// 	return;

		// Bukkit.broadcastMessage("ar = " + ar.getTypes().toString());
		// 만약 스킬이나 마법 공격이라면 스킵
		if(!MMOlib_Event.Attack_Is_NormalAttack(ar))
			return;
		event.getDamage().add(Axe_Mastery.Get_Damage(event.getPlayer()));
		// Bukkit.broadcastMessage("final damage = " + ar.getDamage());
		
	}

	@EventHandler
	public void mala_EntityKilledByEntity(EntityDeathEvent event)
	{
		if(event.getEntity().getKiller() != null)
		{
			Player player = (Player)event.getEntity().getKiller();
			
			Axe_Mastery.Add_Axe_Buf(player);
		}
	}


	@EventHandler
	public void mala_Move(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
    	
		if (event.getFrom().getY() < event.getTo().getY())
		{
			Manager_Axe_Ability.Axe_Active_Use(event.getPlayer(), true);
		}

		if (event.getFrom().getY() > event.getTo().getY())
		{
			Block block = player.getLocation().add(0, -1, 0).getBlock();
	        if (!block.isEmpty() && block.getType().isSolid())
	        {
	            if(player.hasMetadata("BE_Mcmmo_Axe_Skill_Stomp"))
	            {
	            	Manager_Axe_Ability.Axe_Stomp(player);
	            }
	        }
		}
	}
	
}

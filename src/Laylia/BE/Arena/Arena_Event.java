package Laylia.BE.Arena;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import Laylia.BE.Arena.Arena_Attack_Aura;
import Laylia.BE.Arena.Arena_Bomb_Arrow;
import Laylia.BE.Arena.Arena_Charge;
import Laylia.BE.Arena.Arena_Healing_Aura;
import Laylia.BE.Arena.Arena_Mana_Shield;
import Laylia.BE.Arena.Arena_Mass_Heal;
import Laylia.BE.Arena.Arena_Penetrate_Arrow;
import Laylia.BE.Arena.Arena_Rage;
import Laylia.BE.Arena.Arena_Resist_Aura;
import Laylia.BE.Arena.Arena_Roar;
import Laylia.BE.Arena.Arena_Speed_Aura;
import Laylia.BE.Arena.Arena_Swift;
import Laylia.BE.Arena.No_Projectile_Barrier;


public class Arena_Event implements Listener
{
	@EventHandler
	public void mala_PlayerAction(PlayerInteractEvent event)
	{
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
			{
				if(event.getPlayer().isSneaking())
				{					
					Arena_Mass_Heal.Mass_Heal(event.getPlayer());
					Arena_Mana_Shield.Mana_Shield(event.getPlayer());
					No_Projectile_Barrier.Barrier_Action(event.getPlayer());
					Arena_Charge.Charge(event.getPlayer());
					Arena_Rage.Rage(event.getPlayer());
					Arena_Swift.Swift(event.getPlayer());
					Arena_Roar.Roar(event.getPlayer());
				}
			}
		}
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
			{
				if(event.getPlayer().isSneaking())
				{
					Arena_Penetrate_Arrow.Arrow_Action(event.getPlayer());
					Arena_Bomb_Arrow.Arrow_Action(event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void mala_Player_Shoot_Arrow(EntityShootBowEvent event)
	{
		if(event.getEntity().getMetadata("Penetrate_Arrow_Action").size() != 0)
		{
			if(event.getProjectile() instanceof Arrow && event.getEntity() instanceof Player)
			{
				Arena_Penetrate_Arrow.Arrow_Shoot((Player)event.getEntity(), (Arrow)event.getProjectile());
				event.setCancelled(true);
			}
		}
		else if(event.getEntity().getMetadata("Bomb_Arrow_Action").size() != 0)
		{
			if(event.getProjectile() instanceof Arrow && event.getEntity() instanceof Player)
			{
				Arena_Bomb_Arrow.Arrow_Shoot((Arrow)event.getProjectile());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void mala_Arrow_Hit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Arrow)
		{
			if(event.getEntity().getMetadata("Bomb_Arrow").size() != 0)
			{
				Arena_Bomb_Arrow.Arrow_Bomb((Arrow)event.getEntity());
			}
			if(event.getEntity().getMetadata("arrow_remove").size() != 0)
				event.getEntity().remove();
		}
		//Bukkit.getConsoleSender().sendMessage(event.getEntity().getName() + "에게 " + "의 데미지를 줬다");
	}
	
	
	@EventHandler
	public void mala_Itemrack_Damaged(EntityDamageEvent event)
	{
		if(event.getEntityType() == EntityType.ITEM_FRAME)
		{
			if(event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.CUSTOM || event.getCause() == DamageCause.PROJECTILE)
			{
				event.setCancelled(true);
			}
		}
		//Bukkit.getConsoleSender().sendMessage(event.getEntity().getName() + "에게 " + event.getDamage() + "의 데미지를 줬다");
	}
		
	@EventHandler
	public void mala_PlayerItemheld(PlayerItemHeldEvent event)
	{
		if(event.getPlayer().getInventory().getItem(event.getNewSlot()) != null)
		{
			Arena_Healing_Aura.Aura_Judge(event.getPlayer(), event.getNewSlot());
			Arena_Attack_Aura.Aura_Judge(event.getPlayer(), event.getNewSlot());
			Arena_Resist_Aura.Aura_Judge(event.getPlayer(), event.getNewSlot());
			Arena_Speed_Aura.Aura_Judge(event.getPlayer(), event.getNewSlot());
		}
	}
}

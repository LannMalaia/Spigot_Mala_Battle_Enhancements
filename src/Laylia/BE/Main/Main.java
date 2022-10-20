package Laylia.BE.Main;

import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import Laylia.BE.Sword.Manager_Sword_Ability;
import Laylia.BE.Archery.Manager_Archery_Ability;
import Laylia.BE.Axe.Axe_Event;
import Laylia.BE.Axe.Manager_Axe_Ability;
import Laylia.BE.Crossbow.Manager_Crossbow_Ability;
import Laylia.BE.Gauge.PG_Manager;
import Laylia.BE.Gauge.PlayerGauge_Inventory;


public class Main extends JavaPlugin
{
	public static JavaPlugin mother;
	
	@Override
	public void onEnable(){
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Laylia의 전투 향상 활성화!");
		mother = this;
		
		// Bukkit.getPluginManager().registerEvents(new Arena_Event(),  this);
		Bukkit.getPluginManager().registerEvents(new MMOlib_Event(),  this);
				
		// 스킬 매니저 생성
		new Manager_Sword_Ability();
		new Manager_Archery_Ability();
		new Manager_Crossbow_Ability();
		new Manager_Axe_Ability();
		
		// 쿨타임 매니저 가동
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, Manager_Timer.Get_Instance(), 100, Manager_Timer.update_tick);
		
		// 게이지 매니저 가동
		new PG_Manager();
		
		// Manager_BossbarGauge.Get_Instance().Initialize();
		// Bukkit.getScheduler().runTaskTimerAsynchronously(this, Manager_BossbarGauge.Get_Instance(), 100, Manager_BossbarGauge.update_tick);
	}
	
	@Override
	public void onDisable()
	{
		Manager_BossbarGauge.Get_Instance().Initialize();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Laylia의 전투 향상 b활성화..");
	}
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
		if(sender instanceof Player)
		{
			if(cmd.getName().equalsIgnoreCase("be-sword"))
	    	{
	    		Manager_Sword_Ability.Instance.Get_Desc((Player)sender);
	    		return true;
	    	}
	    	if(cmd.getName().equalsIgnoreCase("be-axe"))
	    	{
	    		Manager_Axe_Ability.Instance.Get_Desc((Player)sender);
	    		return true;
	    	}
	    	if(cmd.getName().equalsIgnoreCase("be-archery"))
	    	{
	    		Manager_Archery_Ability.Instance.Get_Desc((Player)sender);
	    		return true;
	    	}
	    	if(cmd.getName().equalsIgnoreCase("be-crossbow"))
	    	{
	    		Manager_Crossbow_Ability.Instance.Get_Desc((Player)sender);
	    		return true;
	    	}
	    	if(cmd.getName().equalsIgnoreCase("mmo-gauge-toggle"))
	    	{
	    		PlayerGauge_Inventory.Call_Inv((Player)sender);
	    		// Manager_BossbarGauge.Get_Instance().Toggle_Player((Player)sender);
	    		return true;
	    	}
		}
    	return false;
    }
}
















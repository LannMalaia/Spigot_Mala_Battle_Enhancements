package Laylia.BE.Gauge;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import Laylia.BE.Main.Main;

public class PG_Manager implements Listener
{
	public static PG_Manager Instance;
	
	public ArrayList<PlayerGauge> gauge_list = new ArrayList<PlayerGauge>();
	
	public PG_Manager()
	{
		Instance = this;
		Bukkit.getPluginManager().registerEvents(this, Main.mother);
		Bukkit.getScheduler().runTaskTimer(Main.mother, new Runnable()
		{
			@Override
			public void run() {
				Update();
				
		}}, 20, 2);
	}
	
	public PlayerGauge Get_Gauge(Player _player)
	{
		for (PlayerGauge pg : gauge_list)
		{
			if (pg.player == _player)
				return pg;
		}
		PlayerGauge pg = new PlayerGauge(_player);
		gauge_list.add(pg);
		return pg;
	}
	

	@EventHandler
	public void pg_when_click(InventoryClickEvent event)
	{
		if (event.getView().getTitle().equals("§1§l게이지 관리자"))
		{
			PlayerGauge_Inventory.When_Inv_Clicked((Player)event.getWhoClicked(), event.getCurrentItem());
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void pg_when_join(PlayerJoinEvent event)
	{
		PlayerGauge pg = Get_Gauge(event.getPlayer());
		pg.Update_Gauge();
	}
	
	public void Update()
	{
		for (int i = 0; i >= 0 && i < gauge_list.size(); i++)
		{
			PlayerGauge pg = gauge_list.get(i);
			if (!pg.player.isOnline())
			{
				pg.Remove();
				gauge_list.remove(pg);
				--i;
				continue;
			}
			pg.Update_Gauge();
		}
	}
}

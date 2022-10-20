package Laylia.BE.Main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import Laylia.BE.Base.Timer_Data;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.StatType;

public class Manager_BossbarGauge implements Runnable
{
	// 상수
	public static final long update_tick = 5;
	
	// 싱글톤
	private static Manager_BossbarGauge Instance; // 싱글톤 오브젝트
	public static Manager_BossbarGauge Get_Instance()
	{
		if(Instance == null)
			Instance = new Manager_BossbarGauge();
		
		return Instance;
	}
	private Manager_BossbarGauge()
	{
		Initialize();
	}
		
	// 변수
	ArrayList<Player> m_Player_List = new ArrayList<Player>(); // 게이지를 켜놓은 플레이어 리스트
	HashMap<Player, NamespacedKey> m_HP_Bars = new HashMap<Player, NamespacedKey>();
	HashMap<Player, NamespacedKey> m_Mana_Bars = new HashMap<Player, NamespacedKey>();
	HashMap<Player, NamespacedKey> m_Stamina_Bars = new HashMap<Player, NamespacedKey>();
	
	public void Initialize()
	{
		if (m_Player_List != null)
			for (Player player : m_Player_List)
				Remove_Bossbar(player);
		
		m_Player_List = new ArrayList<Player>();
		m_HP_Bars = new HashMap<Player, NamespacedKey>();
		m_Mana_Bars = new HashMap<Player, NamespacedKey>();
		m_Stamina_Bars = new HashMap<Player, NamespacedKey>();
	}
	
	// 매 시간 호출
	@Override
	public void run()
	{
		for(int i = 0; i < m_Player_List.size(); i++)
		{
			Player player = m_Player_List.get(i);
			if (!Check_Player_Online(player))
			{
				i--;
				continue;
			}

			PlayerData playerdata = MMOCore.plugin.dataProvider.getDataManager().get(player.getUniqueId());
			
			double max_hp = Math.round(playerdata.getStats().getStat(StatType.MAX_HEALTH) * 10) / 10.0;
			double hp = Math.round(playerdata.getPlayer().getHealth() * 10) / 10.0;
			if (!m_HP_Bars.containsKey(player))
			{
				NamespacedKey nsk = new NamespacedKey(Main.mother, "mmo.hp." + player.getUniqueId().toString());
				m_HP_Bars.put(player, nsk);
				Bukkit.createBossBar(nsk, "HP", BarColor.RED, BarStyle.SEGMENTED_6);
				Bukkit.getBossBar(m_HP_Bars.get(player)).setVisible(true);
				Bukkit.getBossBar(m_HP_Bars.get(player)).addPlayer(player);
			}
			double progress = Math.min(1.0, Math.max(0.0, hp / max_hp));
			Bukkit.getBossBar(m_HP_Bars.get(player)).setTitle("§f§l" + hp + " / " + max_hp);
			Bukkit.getBossBar(m_HP_Bars.get(player)).setProgress(progress);
			
			double max_mana = Math.round(playerdata.getStats().getStat(StatType.MAX_MANA) * 10) / 10.0;
			double mana = Math.round(playerdata.getMana() * 10) / 10.0;
			if (!m_Mana_Bars.containsKey(player))
			{
				NamespacedKey nsk = new NamespacedKey(Main.mother, "mmo.mana." + player.getUniqueId().toString());
				m_Mana_Bars.put(player, nsk);
				Bukkit.createBossBar(nsk, "Mana", BarColor.BLUE, BarStyle.SEGMENTED_6);
				Bukkit.getBossBar(m_Mana_Bars.get(player)).setVisible(true);
				Bukkit.getBossBar(m_Mana_Bars.get(player)).addPlayer(player);
			}
			progress = Math.min(1.0, Math.max(0.0, mana / max_mana));
			Bukkit.getBossBar(m_Mana_Bars.get(player)).setTitle("§b§l" + mana + " / " + max_mana);
			Bukkit.getBossBar(m_Mana_Bars.get(player)).setProgress(progress);
			
			double max_stamina = Math.round(playerdata.getStats().getStat(StatType.MAX_STAMINA) * 10) / 10.0;
			double stamina = Math.round(playerdata.getStamina() * 10) / 10.0;
			if (!m_Stamina_Bars.containsKey(player))
			{
				NamespacedKey nsk = new NamespacedKey(Main.mother, "mmo.stamina." + player.getUniqueId().toString());
				m_Stamina_Bars.put(player, nsk);
				Bukkit.createBossBar(nsk, "Stamina", BarColor.YELLOW, BarStyle.SEGMENTED_6);
				Bukkit.getBossBar(m_Stamina_Bars.get(player)).setVisible(true);
				Bukkit.getBossBar(m_Stamina_Bars.get(player)).addPlayer(player);
			}
			progress = Math.min(1.0, Math.max(0.0, stamina / max_stamina));
			Bukkit.getBossBar(m_Stamina_Bars.get(player)).setTitle("§e§l" + stamina + " / " + max_stamina);
			Bukkit.getBossBar(m_Stamina_Bars.get(player)).setProgress(progress);
		}
	}
	
	public void Toggle_Player(Player _player)
	{
		if (m_Player_List.contains(_player))
		{
			_player.sendMessage("§7자원 게이지를 껐어요.");
			m_Player_List.remove(_player);
			Remove_Bossbar(_player);
		}
		else
		{
			_player.sendMessage("§b자원 게이지를 켰어요.");
			m_Player_List.add(_player);
		}
	}
	
	public void Remove_Bossbar(Player _player)
	{
		if (m_HP_Bars.containsKey(_player))
		{
			Bukkit.getBossBar(m_HP_Bars.get(_player)).removeAll();
			Bukkit.getBossBar(m_HP_Bars.get(_player)).setVisible(false);
			Bukkit.removeBossBar(m_HP_Bars.get(_player));
			m_HP_Bars.remove(_player);
		}
		if (m_Mana_Bars.containsKey(_player))
		{
			Bukkit.getBossBar(m_Mana_Bars.get(_player)).removeAll();
			Bukkit.getBossBar(m_Mana_Bars.get(_player)).setVisible(false);
			Bukkit.removeBossBar(m_Mana_Bars.get(_player));
			m_Mana_Bars.remove(_player);
		}
		if (m_Stamina_Bars.containsKey(_player))
		{
			Bukkit.getBossBar(m_Stamina_Bars.get(_player)).removeAll();
			Bukkit.getBossBar(m_Stamina_Bars.get(_player)).setVisible(false);
			Bukkit.removeBossBar(m_Stamina_Bars.get(_player));
			m_Stamina_Bars.remove(_player);
		}
	}
	
	// 플레이어 온라인 체크 및 오프라인 시 목록에서 제거
	public boolean Check_Player_Online(Player _player)
	{
		if (m_Player_List.contains(_player) && _player.isOnline())
			return true;

		if (m_Player_List.contains(_player))
			m_Player_List.remove(_player);
		Remove_Bossbar(_player);
		
		_player.sendMessage("Not Exist");
		
		return false;
	}
}

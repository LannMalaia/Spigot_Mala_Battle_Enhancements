package Laylia.BE.Gauge;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import Laylia.BE.Main.Main;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.StatType;

public class PlayerGauge
{
	public static int GS_HP = 1;
	public static int GS_MANA = 2;
	public static int GS_STAMINA = 4;
	
	Player player;
	boolean SIMPLE;
	boolean HP;
	boolean Mana;
	boolean Stamina;

	NamespacedKey SIMPLE_nsk;
	NamespacedKey HP_nsk;
	NamespacedKey Mana_nsk;
	NamespacedKey Stamina_nsk;

	KeyedBossBar SIMPLE_bb;
	KeyedBossBar HP_bb;
	KeyedBossBar Mana_bb;
	KeyedBossBar Stamina_bb;
	
	PlayerData playerdata;
	
	public PlayerGauge(Player _player)
	{
		player = _player;
		HP = true;
		Mana = true;
		Stamina = false;

		SIMPLE_nsk = new NamespacedKey(Main.mother, "mmo.simple." + player.getUniqueId());
		HP_nsk = new NamespacedKey(Main.mother, "mmo.hp." + player.getUniqueId());
		Mana_nsk = new NamespacedKey(Main.mother, "mmo.mana." + player.getUniqueId());
		Stamina_nsk = new NamespacedKey(Main.mother, "mmo.stamina." + player.getUniqueId());

		SIMPLE_bb = Bukkit.getBossBar(SIMPLE_nsk);
		if (SIMPLE_bb == null)
			SIMPLE_bb = Bukkit.createBossBar(SIMPLE_nsk, "SIMPLE", BarColor.WHITE, BarStyle.SOLID);
		HP_bb = Bukkit.getBossBar(HP_nsk);
		if (HP_bb == null)
			HP_bb = Bukkit.createBossBar(HP_nsk, "HP", BarColor.RED, BarStyle.SEGMENTED_6);
		Mana_bb = Bukkit.getBossBar(Mana_nsk);
		if (Mana_bb == null)
			Mana_bb = Bukkit.createBossBar(Mana_nsk, "Mana", BarColor.BLUE, BarStyle.SEGMENTED_6);
		Stamina_bb = Bukkit.getBossBar(Stamina_nsk);
		if (Stamina_bb == null)
			Stamina_bb = Bukkit.createBossBar(Stamina_nsk, "Stamina", BarColor.YELLOW, BarStyle.SEGMENTED_6);
		
		Load_PG();
	}
	
	public void Remove()
	{
		Bukkit.removeBossBar(SIMPLE_nsk);
		Bukkit.removeBossBar(HP_nsk);
		Bukkit.removeBossBar(Mana_nsk);
		Bukkit.removeBossBar(Stamina_nsk);
	}
	
	// 파일 관리
	public void Load_PG()
	{
		try
		{
			// 폴더 설정
			File directory = Main.mother.getDataFolder();
			if (!directory.exists())
				directory.mkdir();
			File sub_dir = new File(directory, "GaugeDatas");
			if (!sub_dir.exists())
				sub_dir.mkdir();
			
			File load = new File(sub_dir, player.getUniqueId().toString() + ".yml");
			if (!load.exists())
			{
				load.createNewFile();
				
				// 파일 작성
				FileConfiguration file = YamlConfiguration.loadConfiguration(load);
				file.load(load);

				file.set("Simple_View", false);
				file.set("HP", true);
				file.set("Mana", true);
				file.set("Stamina", false);
				
				file.save(load);
			}
			else
			{
				FileConfiguration file = YamlConfiguration.loadConfiguration(load);
				file.load(load);

				SIMPLE = file.getBoolean("Simple_View");
				HP = file.getBoolean("HP");
				Mana = file.getBoolean("Mana");
				Stamina = file.getBoolean("Stamina");
				
				file.save(load);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void Save_PG()
	{
		try
		{
			// 폴더 설정
			File directory = Main.mother.getDataFolder();
			if (!directory.exists())
				directory.mkdir();
			File sub_dir = new File(directory, "GaugeDatas");
			if (!sub_dir.exists())
				sub_dir.mkdir();
			
			File load = new File(sub_dir, player.getUniqueId().toString() + ".yml");
			if (!load.exists())
				load.createNewFile();
				
			// 파일 작성
			FileConfiguration file = YamlConfiguration.loadConfiguration(load);
			file.load(load);

			file.set("Simple_View", SIMPLE);
			file.set("HP", HP);
			file.set("Mana", Mana);
			file.set("Stamina", Stamina);
			
			file.save(load);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Update_Gauge()
	{
		if (!player.isOnline())
			return;
		if (playerdata == null)
			playerdata = MMOCore.plugin.dataProvider.getDataManager().get(player.getUniqueId());
		// 수치 설정
		double max_hp = Math.round(playerdata.getStats().getStat(StatType.MAX_HEALTH) * 10) / 10.0;
		double hp = Math.round(playerdata.getPlayer().getHealth() * 10) / 10.0;
		double max_mana = Math.round(playerdata.getStats().getStat(StatType.MAX_MANA) * 10) / 10.0;
		double mana = Math.round(playerdata.getMana() * 10) / 10.0;
		double max_stamina = Math.round(playerdata.getStats().getStat(StatType.MAX_STAMINA) * 10) / 10.0;
		double stamina = Math.round(playerdata.getStamina() * 10) / 10.0;
		
		if (mana <= 0.0 || max_mana <= 0.0 || stamina <= 0.0 || max_stamina <= 0.0)
			return;
		
		// 보스바 길이 설정
		double progress = Math.min(1.0, Math.max(0.0, hp / max_hp));
		String hp_text = "§f§l" + hp + " / " + max_hp;
		if (player.hasMetadata("mala.mmoskill.stance.bloodstack"))
			hp_text += " -- §c§l" + player.getMetadata("mala.mmoskill.stance.bloodstack").get(0).asInt();
		HP_bb.setTitle(hp_text);
		HP_bb.setProgress(progress);
		progress = Math.min(1.0, Math.max(0.0, mana / max_mana));
		Mana_bb.setTitle("§b§l" + mana + " / " + max_mana);
		Mana_bb.setProgress(progress);
		String sta_text = "§e§l" + stamina + " / " + max_stamina;
		if (player.hasMetadata("mala.mmoskill.trick.trickstack"))
			sta_text += " -- §a§l" + player.getMetadata("mala.mmoskill.trick.trickstack").get(0).asInt();
		progress = Math.min(1.0, Math.max(0.0, stamina / max_stamina));
		Stamina_bb.setTitle(sta_text);
		Stamina_bb.setProgress(progress);
		
		String desc = "";
		if (HP)
		{
			desc += "§f[  §c" + (int)hp + "/" + (int)max_hp + "  §f]";
			if (player.hasMetadata("mala.mmoskill.stance.bloodstack"))
				desc += "§c[" + player.getMetadata("mala.mmoskill.stance.bloodstack").get(0).asInt() + "]";
		}
		if (Mana)
			desc += "§f[  §9" + (int)mana + "/" + (int)max_mana + "  §f]";
		if (Stamina)
		{
			desc += "§f[  §e" + (int)stamina + "/" + (int)max_stamina + "  §f]";
			if (player.hasMetadata("mala.mmoskill.trick.trickstack"))
				desc += "§a[" + player.getMetadata("mala.mmoskill.trick.trickstack").get(0).asInt() + "]";
		}
		
		SIMPLE_bb.setTitle(desc);
		SIMPLE_bb.setProgress(1.0);
		
		
		// 보스바 토글 설정
		if (!HP && !Mana && !Stamina)
		{
			SIMPLE_bb.removePlayer(player);
			HP_bb.removePlayer(player);
			Mana_bb.removePlayer(player);
			Stamina_bb.removePlayer(player);
		}
		else if (SIMPLE)
		{
			SIMPLE_bb.addPlayer(player);
			HP_bb.removePlayer(player);
			Mana_bb.removePlayer(player);
			Stamina_bb.removePlayer(player);
		}
		else
		{
			SIMPLE_bb.removePlayer(player);
			if (HP)
				HP_bb.addPlayer(player);
			else
				HP_bb.removePlayer(player);
			if (Mana)
				Mana_bb.addPlayer(player);
			else
				Mana_bb.removePlayer(player);
			if (Stamina)
				Stamina_bb.addPlayer(player);
			else
				Stamina_bb.removePlayer(player);
		}
	}

}

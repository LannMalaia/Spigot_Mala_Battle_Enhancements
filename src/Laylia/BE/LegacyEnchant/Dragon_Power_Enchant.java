package Laylia.BE.LegacyEnchant;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Dragon_Power_Enchant
{
	public enum Power_Type{SKIN, WIND_CUTTER};
	
	public static void Dragon_Death_Judge(Player player)
	{
		List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		if(lores != null)
		{
			for(int count = 0; count < lores.size(); count++)
			{
				if(lores.get(count).matches(".*" + "�巡�� ��Ų" + ".*"))
				{
					Location loc = player.getLocation();
					if(lores.get(count).contains("III"))
					{
						loc.add(0,  1.5, 0);
						//temp2.getWorld().spawnParticle(Particle.CLOUD, temp2, 1, 0d, 0d, 0d, 0d);
						//ParticleEffect.BLOCK_CRACK.send(Bukkit.getOnlinePlayers(), loc, 0.15, 0.15, 0.15, 0, 100);
						loc.add(0,  -1, 0);
						//ParticleEffect.PORTAL.send(Bukkit.getOnlinePlayers(), loc, 0, 0, 0, 0.2, 100);
						player.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 2);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tm msg -stay=50 " + player.getName() + " " + mala_Dragon_Conversation.Dragon_Death_Message(3, player.getName()));
					}
					else if(lores.get(count).contains("II"))
					{
						loc.add(0,  1.5, 0);
						//ParticleEffect.BLOCK_CRACK.sendData(Bukkit.getOnlinePlayers(), loc.getX(), loc.getY(), loc.getZ(), 0.15, 0.15, 0.15, 0d, 100, 173, (byte)0);
						loc.add(0,  -1, 0);
						//ParticleEffect.PORTAL.send(Bukkit.getOnlinePlayers(), loc, 0, 0, 0, 0.2, 100);
						player.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 2);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tm msg -stay=50 " + player.getName() + " " +  mala_Dragon_Conversation.Dragon_Death_Message(2, player.getName()));
					}
				}
			}
		}
	}
	
	public static void Dragon_Judge(Player player)
	{
		List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		if(lores != null)
		{
			for(int count = 0; count < lores.size(); count++)
			{
				if(lores.get(count).matches(".*" + "�巡�� ��Ų" + ".*"))
				{
					if(player.getMetadata("Dragon_Skin_Cooldown").size() != 0)
					{
						if(lores.get(count).contains("III"))
						{
							// ActionBarAPI.sendActionBar(player, mala_Dragon_Conversation.Dragon_Skin_Cooldown_Message(3));
						}
						else if(lores.get(count).contains("II"))
						{
							// ActionBarAPI.sendActionBar(player, mala_Dragon_Conversation.Dragon_Skin_Cooldown_Message(2));
						}
						else if(lores.get(count).contains("I"))
						{
							// ActionBarAPI.sendActionBar(player, mala_Dragon_Conversation.Dragon_Skin_Cooldown_Message(1));
						}
						return;
					}
					if(lores.get(count).contains("III"))
					{
						Dragon_Skin(player, 3);
					}
					else if(lores.get(count).contains("II"))
					{
						Dragon_Skin(player, 2);
					}
					else if(lores.get(count).contains("I"))
					{
						Dragon_Skin(player, 1);
					}
				}
			}
		}
	}

	
	public static void Dragon_Skin(Player player_data, int power)
	{
		String level = "";
		Location player_loc = player_data.getLocation();
		Collection<PotionEffect> pes = player_data.getActivePotionEffects();
		pes.clear();
		
		// �Ŀ��� ���缭 �̰����� ����
		switch(power)
		{
		case 1:
			// ActionBarAPI.sendActionBar(player_data, mala_Dragon_Conversation.Dragon_OK_Message(1));
			pes.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
			pes.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0));
			pes.add(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 3));
			player_data.getWorld().playSound(player_data.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.5f, 2);
			player_loc.add(0,  1, 0);
			//ParticleEffect.DRAGON_BREATH.send(Bukkit.getOnlinePlayers(),player_loc, 0.25, 0.25, 0.25, 0.025, 100);
			level = "I";
			break;
		case 2:
			// ActionBarAPI.sendActionBar(player_data, mala_Dragon_Conversation.Dragon_OK_Message(2));
			pes.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1800, 0));
			pes.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1800, 0));
			pes.add(new PotionEffect(PotionEffectType.ABSORPTION, 1800, 4));
			player_data.getWorld().playSound(player_data.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1);
			player_loc.add(0,  1, 0);
			//ParticleEffect.DRAGON_BREATH.send(Bukkit.getOnlinePlayers(),player_loc, 0.25, 0.25, 0.25, 0.025, 50);
			level = "II";
			break;
		case 3:
			level = "III";
			break;
		}
		player_data.setMetadata("Dragon_Skin_Cooldown", new FixedMetadataValue(Main.mother, 1));
				
		TitleManager.sendTitle(player_data, 0, 60, 0, "", "��6�巡�� ��Ų " + level);
		
		player_data.addPotionEffects(pes);
		
		Main.mother.getServer().getScheduler().runTaskLater(Main.mother,
				new Dragon_Skin_Cooldown(player_data), 3600);
	}
}

class Dragon_Skin_Cooldown implements Runnable
{
	Player p;
	
	public Dragon_Skin_Cooldown(Player player)
	{
		p = player;
	}
	
	public void run()
	{
		p.removeMetadata("Dragon_Skin_Cooldown", Main.mother);
	}
}

class mala_Dragon_Conversation
{
	public static String Dragon_Skin_Cooldown_Message(int lv)
	{
		String answer = "";
		switch(lv)
		{
		case 1:
			answer += "" + ChatColor.RED + ChatColor.BOLD + "[ ���� �̰� ���� ���̴ٰ� ���� �ߵ� �� ���� �� ����... ]";
			break;
		case 2:
			int rand = (int)Math.ceil(Math.random() * 5);
			switch(rand)
			{
			case 1:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ Į�� �̹��ϰ� �����Ѵ�. ]";
				break;
			case 2:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ Ȥ�� �ڰ� �ִ°ɱ�? ]";
				break;
			case 3:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ �ƹ��� ������ ����. ]";
				break;
			case 4:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ ���� �ΰ��� ���� �� �˾Ƶ�°ǰ�? ]";
				break;
			case 5:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ �ҷ��� ����� ����. ]";
				break;
			}
			break;
		case 3:
			break;
		}
		return answer;
	}
	
	public static String Dragon_Death_Message(int lv, String player_name)
	{
		String answer = "";
		switch(lv)
		{
		case 2:
			int rand = (int)Math.ceil(Math.random() * 3);
			switch(rand)
			{
			case 1:
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " �� ���� �� ���� �ֵθ� �ڰ��� ����!";
				break;
			case 2:
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " ...�� ������ ��� �� �� �ȿ� �����������?";
				break;
			case 3:
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " ���� �ΰ��� �����ϱ�.";
				break;
			}
			break;
		case 3:
			break;
		}
		return answer;
	}
	
	public static String Dragon_OK_Message(int lv)
	{
		String answer = "";
		switch(lv)
		{
		case 2:
			int rand = (int)Math.ceil(Math.random() * 3);
			switch(rand)
			{
			case 0:
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "���巡�";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "�� ���� ������ �ϴ±�.";
				break;
			case 1:
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "���巡�";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "�ܿ� �̷� �Ͽ� ���� �ҷ����ٴ�...";
				break;
			case 2:
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "���巡�";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "�ڱ� �� ������ ������ ��Ű��� �ΰ�!";
				break;
			}
			break;
		case 3:
			break;
		}
		return answer;
	}
}











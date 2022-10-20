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
				if(lores.get(count).matches(".*" + "드래곤 스킨" + ".*"))
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
				if(lores.get(count).matches(".*" + "드래곤 스킨" + ".*"))
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
		
		// 파워에 맞춰서 이것저것 변경
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
				
		TitleManager.sendTitle(player_data, 0, 60, 0, "", "§6드래곤 스킨 " + level);
		
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
			answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 지금 이걸 들이 마셨다간 몸이 견딜 수 없을 것 같다... ]";
			break;
		case 2:
			int rand = (int)Math.ceil(Math.random() * 5);
			switch(rand)
			{
			case 1:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 칼이 미묘하게 진동한다. ]";
				break;
			case 2:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 혹시 자고 있는걸까? ]";
				break;
			case 3:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 아무런 응답이 없다. ]";
				break;
			case 4:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 역시 인간의 말은 못 알아듣는건가? ]";
				break;
			case 5:
				answer += "" + ChatColor.RED + ChatColor.BOLD + "[ 불러도 대답이 없다. ]";
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
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " 네 놈은 이 검을 휘두를 자격이 없다!";
				break;
			case 2:
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " ...너 같은게 어떻게 날 이 안에 집어넣은거지?";
				break;
			case 3:
				answer += "" + ChatColor.DARK_GRAY + player_name + "{nl}" + ChatColor.GRAY + " 역시 인간은 나약하군.";
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
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "【드래곤】";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "또 나를 귀찮게 하는군.";
				break;
			case 1:
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "【드래곤】";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "겨우 이런 일에 나를 불러내다니...";
				break;
			case 2:
				answer += "" + ChatColor.GOLD + ChatColor.BOLD + "【드래곤】";
				answer += "" + ChatColor.WHITE + ChatColor.BOLD + " : ";
				answer += "" + ChatColor.YELLOW + ChatColor.BOLD + "자기 몸 정도는 스스로 지키라고 인간!";
				break;
			}
			break;
		case 3:
			break;
		}
		return answer;
	}
}











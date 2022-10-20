package Laylia.BE.Arena;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Laylia.BE.Main.Main;
import laylia_core.main.TitleManager;

public class Wither_Explosion_Power_Enchant 
{
	
	public static void Wither_Judge(Player player)
	{
		List<String> lores = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		if(lores != null)
		{
			for(int count = 0; count < lores.size(); count++)
			{
				if(lores.get(count).matches(".*" + "재앙의 화살" + ".*"))
				{
					if(player.getMetadata("Wither_Arrow_Cooldown").size() != 0)
					{
						ActionBarAPI.sendActionBar(player, "" + ChatColor.BLACK + ChatColor.BOLD + "재사용 대기시간입니다.");
						return;
					}
					if(lores.get(count).contains("III"))
					{
						Explosion_Arrow(player, 3);
					}
					else if(lores.get(count).contains("II"))
					{
						Explosion_Arrow(player, 2);
					}
					else if(lores.get(count).contains("I"))
					{
						Explosion_Arrow(player, 1);
					}
				}
			}
		}
	}
	
	static void Explosion_Arrow(Player player_data, int power)
	{
		// 위치
		double pos_x = player_data.getLocation().getX();
		double pos_y = player_data.getLocation().getY() + 1.6; // 눈높이
		double pos_z = player_data.getLocation().getZ();
		
		// 방향
		double dir_x = player_data.getLocation().getDirection().getX();
		double dir_y = player_data.getLocation().getDirection().getY();
		double dir_z = player_data.getLocation().getDirection().getZ();
		
		//설정값
		double multiplier = 1;
		int range = 240;
		
		boolean is_blocked = false;
		
		//player_data.sendMessage("x : " + dir_x + ", y : " + dir_y + ", z : " + dir_z);
		
		for(int i = 7; i < range; i++)
		{
			if(is_blocked)
			 break;
			for(double j = 0; j < 8; j++) // 파티클 소환 & 블록 체크
			{
				double x = pos_x + (dir_x * (multiplier * i + j / 8.0));
				double y = pos_y + (dir_y * (multiplier * i + j / 8.0));
				double z = pos_z + (dir_z * (multiplier * i + j / 8.0));
				
				Vector loc = new Vector(x, y, z);
				
				if (player_data.getWorld().getBlockAt(new Location(player_data.getWorld(), x, y, z)).getType().isSolid() || i == range - 1)
				{
					is_blocked = true;
				}
				player_data.getWorld().spawnParticle(Particle.LAVA, loc.toLocation(player_data.getWorld()), 1, 0d, 0d, 0d, 0d);
				//ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), loc.toLocation(player_data.getWorld()), 0, 0, 0, 0, 1);
				player_data.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 1, 0, 0, 0, 0);
			}
			
			Main.mother.getServer().getScheduler().runTaskLater(Main.mother, new Wither_Explosion_Arrow_Action(player_data, i, is_blocked, power), 0);
		}
		player_data.setMetadata("Wither_Arrow_Cooldown", new FixedMetadataValue(Main.mother, 1));

		// 쿨타임 바 세팅
		/*
		BossBar wither_cooldown = BossBarAPI.addBar(player_data,
				new TextComponent("재앙의 화살 재사용 대기시간"), // 텍스트
				BossBarAPI.Color.RED, // 컬러
				BossBarAPI.Style.PROGRESS, // 진행도
				1.0f, // 시작 프로그레스
				900 + 300 * power, // 시간
				2); //  시간 인터벌
		
		wither_cooldown.addPlayer(player_data);*/
		
		String level = "";
		switch(power)
		{
		case 1:
			level = "I";
			break;
		case 2:
			level = "II";
			break;
		case 3:
			level = "III";
			break;
		}
		TitleManager.sendTitle(player_data, 0, 60, 0, "", "§4§l재앙의 화살 " + level);
		
		Main.mother.getServer().getScheduler().runTaskLater(Main.mother,
				new Wither_Arrow_Cooldown(player_data), 1800 + 600 * power);
	}
}


class Wither_Explosion_Arrow_Action implements Runnable
{
	private static Player player;
	private boolean fin;
	private int count;
	private int pow;
	
	public Wither_Explosion_Arrow_Action(Player player_data, int i, boolean is_final, int power)
	{
		count = i;
		player = player_data;
		fin = is_final;
		
		if(is_final)
		{
			switch(power)
			{
			case 1:
				pow = 10;
				break;
			case 2:
				pow = 14;
				break;
			case 3:
				pow = 18;
				break;
			}
		}
		else
		{
			switch(power)
			{
			case 1:
				pow = 4;
				break;
			case 2:
				pow = 5;
				break;
			case 3:
				pow = 6;
				break;
			}
		}
	}
	
	public void run()
	{

		// 위치
		double pos_x = player.getLocation().getX();
		double pos_y = player.getLocation().getY() + 1.6; // 눈높이
		double pos_z = player.getLocation().getZ();
		
		// 방향
		double dir_x = player.getLocation().getDirection().getX();
		double dir_y = player.getLocation().getDirection().getY();
		double dir_z = player.getLocation().getDirection().getZ();

		//설정값
		double multiplier = 1;
		
		
		Location loc = new Location(player.getWorld(), pos_x + dir_x * multiplier * count, pos_y + dir_y * multiplier * count, pos_z + dir_z * multiplier * count);
		
		Main.mother.getServer().getScheduler().runTaskLater(Main.mother, new Wither_Start_Explode(player, loc, pow, fin), (long)(30 + 0.5 * count));
	}
}

class Wither_Start_Explode implements Runnable
{
	private static Player player;
	private Location location;
	private int pow;
	private boolean fin;
	
	public Wither_Start_Explode(Player player_data, Location loc, int power, boolean is_final)
	{
		player = player_data;
		location = loc;
		pow = power;
		fin = is_final;
	}
	
	public void run()
	{
		if(fin)
			location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 100, 0d, 0d, 0d, 0d);
		//ParticleEffect.EXPLOSION_HUGE.send(Bukkit.getOnlinePlayers(), location, pow / 2, pow / 2, pow / 2, 0, 100);
		player.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), pow, false, false);
		
		//player.getWorld().createExplosion(, 5f);
	}
}

class Wither_Arrow_Cooldown implements Runnable
{
	Player p;
	
	public Wither_Arrow_Cooldown(Player player)
	{
		p = player;
	}
	
	public void run()
	{
		p.removeMetadata("Wither_Arrow_Cooldown", Main.mother);
	}
}






















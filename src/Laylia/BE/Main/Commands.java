package Laylia.BE.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import Laylia.BE.Main.Main;


public final class Commands
{
	public static Player player_data;
	public static Main mother;
	
	void Elytra_Boost()
	{
		double dir_x = player_data.getLocation().getDirection().getX();
		double dir_y = player_data.getLocation().getDirection().getY();
		double dir_z = player_data.getLocation().getDirection().getZ();
		
		double power = 2.5;
		
		Vector x1 = new Vector(-player_data.getLocation().getDirection().normalize().getZ(), 0d, player_data.getLocation().getDirection().normalize().getX()).normalize();
		Vector x2 = player_data.getLocation().getDirection().normalize().crossProduct(x1).normalize();
		
		for(int i = 0; i < 90; i++)
		{
			Location temp2 = player_data.getLocation().add(x1.clone().multiply(3d * Math.sin((double)i / 90d * Math.PI * 2d))).add(x2.clone().multiply(3d * Math.cos((double)i / 90d * Math.PI * 2d)));
			temp2.setDirection(new Vector(0, 0, 0));
			temp2.getWorld().spawnParticle(Particle.CLOUD, temp2, 1, 0d, 0d, 0d, 0d);
			
			//ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), temp2, 0, 0, 0, 0, 1);
		}
		
		player_data.setVelocity(new Vector(dir_x * power, dir_y * power, dir_z * power));
	}

	void Back_Dash()
	{
		double pos_x = player_data.getLocation().getX();
		double pos_y = player_data.getLocation().getY();
		double pos_z = player_data.getLocation().getZ();

		double dir_x = player_data.getLocation().getDirection().getX();
		double dir_z = player_data.getLocation().getDirection().getZ();
		
		double power = 1.7;

		player_data.getWorld().spawnParticle(Particle.SMOKE_LARGE, pos_x, pos_y + 1, pos_z, 70, 0.5, 1, 0.5);
		
		player_data.setVelocity(new Vector(-dir_x * power, 0.3, -dir_z * power));
	}

}

class Optional
{
	public static void Set_Title(Player player, String msg, String element)
	{
		String command = "title ";
		command += player.getName() + " title ";
		command += "[{\"text\":\"" + msg + element + "}]";
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public static void Set_Subtitle(Player player, String msg, String element)
	{
		String command = "title ";
		command += player.getName() + " subtitle ";
		command += "[{\"text\":\"" + msg + element + "}]";
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public static void Set_Title_Time(Player player, int in_time, int maintain_time, int out_time)
	{
		String command = "title ";
		command += player.getName() + " times ";
		command += in_time + " " + maintain_time + " " + out_time;
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
}





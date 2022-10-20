package Laylia.BE.Base;

import org.bukkit.entity.Player;

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote 모든  어빌리티들의 근본이 되는 클래스
 * 이대로는 사용 불가능
 */
public abstract class Manager_Ability_Base
{	
	/**
	 * @author jimja
	 * @version 2020. 5. 5.
	 * @apiNote 해당 어빌리티에 대한 설명을 플레이어에게 전달
	 */
	public void Get_Desc(Player _player)
	{
		_player.sendMessage(Make_Description(_player));
	};

	/**
	 * @author jimja
	 * @version 2020. 5. 5.
	 * @apiNote 해당 어빌리티에 대한 설명을 생성
	 */
	protected abstract String Make_Description(Player _player);
	
	public abstract double Get_Data(Player _player, String _key);
}
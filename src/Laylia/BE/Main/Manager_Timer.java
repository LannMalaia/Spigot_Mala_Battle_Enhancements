package Laylia.BE.Main;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import Laylia.BE.Base.Timer_Data;


/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote 쿨타임 관리자
 */
public class Manager_Timer implements Runnable
{
	// 상수
	public static final long update_tick = 20;
	
	// 싱글톤
	private static Manager_Timer Instance; // 싱글톤 오브젝트
	public static Manager_Timer Get_Instance()
	{
		if(Instance == null)
			Instance = new Manager_Timer();
		
		return Instance;
	}
	private Manager_Timer()
	{
		m_Timer_List = new ArrayList<Timer_Data>();
	}
	
	// 변수
	ArrayList<Timer_Data> m_Timer_List; // 플레이어들의 쿨타임 리스트
	
	// 매 시간 호출
	@Override
	public void run()
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.Update_Timer(update_tick))
			{
				m_Timer_List.remove(data);
				i--;
			}
		}
	}
	
	// 메소드
	/**
	 * @apiNote 해당 타이머 객체가 살아 있는지를 확인한다
	 * @param _player 플레이어 객체
	 * @param _key 스킬 키 네임
	 * @param _alarm 쿨타임 중이라면 이를 알려준다
	 * @return 쿨타임 중이라면 true, 아니라면 false
	 */
	public boolean Is_Timer_Available(Player _player, String _key, boolean _alarm)
	{
		for(Timer_Data data : m_Timer_List)
		{
			// 리스트 내에 있는 데이터가 파라미터와 같은 경우 true 반환
			if(data.is_Similar(_player, _key))
			{
				if(_alarm)
					data.Send_Playing_Message_To_Player();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @apiNote 타이머 설정
	 * @param _data
	 */
	public void Set_Timer(Timer_Data _data)
	{
		for(Timer_Data data : m_Timer_List)
		{
			// 리스트 내에 이미 데이터가 있는 경우에는 쿨타임만 갱신
			if(data.is_Similar(_data))
			{
				data.Set_Timer(_data.Get_Timer());
				return;
			}
		}
		m_Timer_List.add(_data);
	}
	
	/**
	 * @apiNote 타이머 제거
	 * @param _data
	 */
	public void Remove_Timer(Player _player, String _key)
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.is_Similar(_player, _key))
			{
				m_Timer_List.remove(data);
				return;
			}
		}
	}
	
	/**
	 * @apiNote 타이머 증감
	 * @param _tick 1초 = 20틱, 음수라면 되려 타이머가 증가한다
	 */
	public void Sub_Timer(Player _player, String _key, long _tick)
	{
		for(int i = 0; i < m_Timer_List.size(); i++)
		{
			Timer_Data data = m_Timer_List.get(i);
			if(data.is_Similar(_player, _key))
			{
				if(data.Update_Timer(_tick))
				{
					m_Timer_List.remove(data);
					return;
				}
			}
		}
	}
}

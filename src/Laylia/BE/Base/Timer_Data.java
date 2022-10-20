package Laylia.BE.Base;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author jimja
 * @version 2020. 5. 5.
 * @apiNote 쿨타임 데이터
 */
public class Timer_Data
{
	private TIMER_TYPE m_Type; // 타이머 타입
	private Player m_Player; // 플레이어
	private String m_Skill_Name; // 스킬명
	private String m_Skill_Key; // 스킬명
	private long m_Remained_Timer; // 잔여 쿨타임
	
	/**
	 * @apiNote 쿨타임 정보 구조체
	 * @param _player 플레이어 오브젝트
	 * @param _name 스킬 이름
	 * @param _key 스킬 키 네임
	 * @param _Timer_ticks 쿨타임 틱 (1초 = 20틱)
	 */
	public Timer_Data(TIMER_TYPE _type, Player _player, String _name, String _key, long _Timer_ticks)
	{
		m_Type = _type;
		m_Player = _player;
		m_Skill_Name = new String(_name);
		m_Skill_Key = new String(_key);
		m_Remained_Timer = _Timer_ticks;
		
		Send_Start_Message_To_Player();
	}

	// 쿨타임 메소드
	public void Set_Timer(long _time)
	{
		m_Remained_Timer = _time;
	}
	public long Get_Timer()
	{
		return m_Remained_Timer;
	}
	/**
	 * @apiNote 쿨타임을 업데이트한다
	 * @param _time 업데이트 된 틱 (1초 = 20틱)
	 * @return 소멸되어야 하는 경우 true를 반환
	 */
	public boolean Update_Timer(long _time)
	{
		m_Remained_Timer -= _time;
		if(m_Remained_Timer <= 0)
		{
			// 쿨타임이 다 된 경우
			Send_End_Message_To_Player();
			return true;
		}
		return false;
	}

	// 메시지 메소드
	/**
	 * @apiNote 도중인 경우에 메시지를 보낸다
	 */
	public void Send_Playing_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_COOLDOWN:
			m_Player.sendMessage(ChatColor.RED + m_Skill_Name + "은(는) 아직 사용할 수 없습니다."
					+ ChatColor.YELLOW + " (남은 시간: " + (m_Remained_Timer / 20) + "초)");
			break;
		}
	}
	/**
	 * @apiNote 타이머가 시작됐을 때 메시지를 보낸다
	 */
	public void Send_Start_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_READY:
			m_Player.sendMessage(ChatColor.AQUA + "[ " + m_Skill_Name + " 준비 ]");
			break;
		case SKILL_USE:
			m_Player.sendMessage(ChatColor.RED + "[ " + m_Skill_Name + " 발동 ]");
			break;
		}
	}
	/**
	 * @apiNote 타이머가 뒤졌을 때 메시지를 보낸다
	 */
	public void Send_End_Message_To_Player()
	{
		switch(m_Type)
		{
		case SKILL_READY:
			m_Player.sendMessage(ChatColor.GRAY + "[ " + m_Skill_Name + " 준비 해제됨 ]");
			break;
		case SKILL_USE:
			m_Player.sendMessage(ChatColor.GRAY + "[ " + m_Skill_Name + " 종료 ]");
			break;
		case SKILL_COOLDOWN:
			m_Player.sendMessage(ChatColor.YELLOW + m_Skill_Name + "은(는) 이제 사용 가능합니다.");
			break;
		}
	}
	
	// 비교 메소드
	/**
	 * @param _data 비교할 대상
	 * @return 서로 같다면 true, 아니면 false
	 */
	public boolean is_Similar(Timer_Data _data)
	{
		return m_Player == _data.m_Player && m_Skill_Key.equals(_data.m_Skill_Key);
	}
	/**
	 * @param _player 비교할 플레이어
	 * @param _key 비교할 스킬 키 네임
	 * @return 서로 같다면 true, 아니면 false
	 */
	public boolean is_Similar(Player _player, String _key)
	{
		return m_Player == _player && m_Skill_Key.equals(_key);
	}
}
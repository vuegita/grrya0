package com.inso.framework.socketio.model;

import java.math.BigDecimal;
import java.util.Map;

import com.beust.jcommander.internal.Maps;

/**
 * 指令只能是由服务端发送, 
 * @author Administrator
 *
 */
public class CMDProtocol extends MyProtocol {
	
	/**
	 * 指令公共参数模板
	 * @param toUserid
	 * @param nickname
	 * @return
	 */
	private static CMDProtocol createCMDBodyTemplate(String fromUserid, String targetid, IEventType chatType)
	{
		CMDProtocol body = new CMDProtocol();
		body.setFromUserid(fromUserid);
		body.setTargetid(targetid);
//		body.setTypeMsg(chatType);
//		body.setTypeChat(chatType);
		body.setTime(System.currentTimeMillis());
		return body;
	}
	
	/**
	 * 创建指令content部分消息体
	 * @param fromUserid   发送此指令的人
	 * @param cmdType
	 * @return
	 */
	public static Map<String, Object> createDataMap(CMDType cmdType)
	{
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("cmd", cmdType.getKey());
		return dataMap;
	}
	
	/**
	 * 创建红包消息体
	 * @param fromUserid   发送人
	 * @param toUserid		接收人
	 * @param nickname		发送人昵称
	 * @param money			总金额
	 * @param size				红包个数
	 * @return
	 */
	public static CMDProtocol createRedPacketCMDBody(String fromUserid, String targetid, IEventType chatType, BigDecimal money, int size)
	{
		Map<String, Object> dataMap = createDataMap(CMDTypeImpl.C_RED_PACKET);
		dataMap.put("size", size);
		CMDProtocol body = createCMDBodyTemplate(fromUserid, targetid, chatType);
		body.setData(dataMap);
		return body;
	}
	 
	/**
	 * 创建指令
	 * @param fromUserid
	 * @param toUserid
	 * @param fromNickname
	 * @param cmdType
	 * @return
	 */
	public static CMDProtocol createCMDBody(String fromUserid, String toUserid, IEventType chatType, CMDType cmdType)
	{
		if(cmdType == null)
		{
			throw new RuntimeException("该指令不存在!");
		}
		Map<String, Object> dataMap = createDataMap(cmdType);
		CMDProtocol body = createCMDBodyTemplate(fromUserid, toUserid, chatType);
		body.setData(dataMap);
		return body;
	}
	
	/**
	 * 协议接口
	 * @author Administrator
	 *
	 */
	public interface CMDType {
		public String getName();
		public String getKey();
	}
	
	/**
	 * 指令类型：以【c】开头-表示公共相关
	 * 指令类型：以【f】开头-表示单聊相关
	 * 指令类型：以【g】开头-表示群组相关
	 */
	public static enum CMDTypeImpl implements CMDType {
		//==================公共部分==================
		C_REVOKE_MSG("c_revoke_msg", "撤回"),
		C_RED_PACKET("c_red_packet", "红包"),
		
		//==================单聊相关==================
		F_ADD_FRIEND("f_add_friend", "添加好友"),
		F_DEL_FRIEND("f_del_friend", "删除好友"),
		F_BLACK_FRIEND("f_black_friend", "拉黑好友"),
		
		//==================群组相关==================
		G_HOLDER_MODIFY_INFO("g_holder_modify_info", "群主修改群信息通知"),
		G_HOLDER_INVITE_FRIEND("g_holder_invite_friend", "群主邀请好友加群通知"),
		G_HOLDER_DISBAND("g_holder_disband", "解散群组"),
		
		G_MEMBER_INVITE_FRIEND("g_member_invite_friend", "群成员邀请好友加群通知"),
		G_MEMBER_BEINVITE("g_member_beinvite", "被邀请人加群通知"),
		
		;
			
		private String name;
		private String key;
		private CMDTypeImpl(String key, String name)
		{
			this.key = key;
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getKey()
		{
			return key;
		}
	}
	
	public static void main(String[] args)
	{
//		CMDProtocol cmd = createRedPacketCMDBody("a", "b", MessageEvent.SINGLE, BigDecimal.valueOf(10), 10);
//		System.out.println(FastJsonHelper.jsonEncode(cmd));
	}
	
}

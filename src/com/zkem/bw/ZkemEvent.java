package com.zkem.bw;

import com.jacob.com.Dispatch;

/**
 * 中控事件处理类
 * @author 陈捷
 *
 */
public class ZkemEvent {
	private Dispatch dispatch;
	
	public ZkemEvent(Dispatch dispatch){
		this.dispatch=dispatch;
	}
	
	public void OnConnected(){
		System.out.println("事件-->连接设备成功!");
	}
	
	public void OnDisConnected(){
		System.out.println("事件-->断开连接!");
	}
}

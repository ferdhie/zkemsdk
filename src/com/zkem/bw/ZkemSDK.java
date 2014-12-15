package com.zkem.bw;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * 中控考勤B&W系列SDK
 * @author 陈捷
 *
 */
public class ZkemSDK {
	
	//初始化中控插件
	private static ActiveXComponent zkem=null;
	
	static{
		try{
			zkem=new ActiveXComponent("zkemkeeper.ZKEM");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * USB连接考勤机
	 * @param inMachineNumber 机器号(输入参数)
	 * @return 连接成功返回true,连接失败返回false
	 */
	public static boolean Connect_USB(int inMachineNumber){
		boolean status= zkem.invoke("Connect_USB",new Variant(inMachineNumber)).getBoolean();
		return status;
	}
	
	/**
	 * 断开连接
	 */
	public static void Disconnect(){
		zkem.invoke("Disconnect");
	}
	
	/**
	 * 读取所有的用户信息到PC内存中，包括用户编号、密码、卡号等，指纹模板除外。
	 * 该函数执行完成后，可调用GetAllUserID取出用户信息
	 * @param inMachineNumber 机器号(输入参数)
	 * @return 读取成功返回true，读取失败返回false
	 */
	public static boolean ReadAllUserID(int inMachineNumber){
		boolean status=zkem.invoke("ReadAllUserID",new Variant(inMachineNumber)).getBoolean();
		return status;
	}
}

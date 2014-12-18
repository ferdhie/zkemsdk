package com.test;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.zkem.tft.ZkemSDK;

public class test1 {

	/**
	 * 测试zkemsdk包
	 * @param args
	 */
	public static void main(String[] args) {
		int machineNumber=1;
		ZkemSDK zkemsdk=new ZkemSDK();
//		List<Map<String,Object>> listUser=zkemsdk.GetAllUserInfo(machineNumber);
		boolean status=zkemsdk.Connect_USB(machineNumber);
		if(status==false){
			System.out.println("连接USB失败");
		}else{
			System.out.println("连接成功!");
			List<Map<String,Object>> listUser=zkemsdk.SSR_GetAllUserInfo(machineNumber);
			
			if(listUser!=null){
				for(Map<String,Object> user:listUser){
					System.out.println(
							"用户名:"+user.get("name")
							+"   用户编号:"+user.get("enrollnumber")
							+"   用户权限:"+user.get("privilege"));
				}
			}else{
				System.out.println("获取用户信息失败!");
			}
		}
		
		zkemsdk.Disconnect();
	}


	@Test
	public void testSDKVersion(){
		ZkemSDK zkem=new ZkemSDK();
		System.out.println(zkem.GetSDKVersion());
	}
}
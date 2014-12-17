package com.zkem.bw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charset.CharsetUtils;
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
			zkem=new ActiveXComponent("zkemkeeper.ZKEM.1");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * USB连接考勤机
	 * @param inMachineNumber 机器号(输入参数)
	 * @return 连接成功返回true,连接失败返回false
	 */
	public boolean Connect_USB(int inMachineNumber){
		boolean status= zkem.invoke("Connect_USB",new Variant(inMachineNumber)).getBoolean();
		return status;
	}
	
	/**
	 * 断开连接
	 */
	public void Disconnect(){
		zkem.invoke("Disconnect");
	}
	
	/**
	 * 读取所有的用户信息到PC内存中，包括用户编号、密码、卡号等，指纹模板除外。
	 * 该函数执行完成后，可调用GetAllUserID取出用户信息
	 * @param inMachineNumber 机器号(输入参数)
	 * @return 读取成功返回true，读取失败返回false
	 */
	public boolean ReadAllUserID(int inMachineNumber){
		boolean status=zkem.invoke("ReadAllUserID",new Variant(inMachineNumber)).getBoolean();
		return status;
	}
	
	public List<Map<String,Object>> GetAllUserInfo(int inMachineNumber){
		List<Map<String,Object>> listUser=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadAllUserID(inMachineNumber);
		
		if(status==false){
			return null;
		}
		
		Variant machineNumber=new Variant(1,true);
		Variant enrollNumber=new Variant("",true);
		Variant name=new Variant("",true);
		Variant password=new Variant("",true);
		Variant privilege=new Variant(0,true);
		Variant enable=new Variant(false,true);
		
		while(status){
			status=zkem.invoke(
					"SSR_GetAllUserInfo",
					machineNumber,
					enrollNumber,
					name,
					password,
					privilege,
					enable).getBoolean();
			
			//如果没有用户编号则跳过
			String strEnrollnumber=enrollNumber.getStringRef();
			if(strEnrollnumber==null || strEnrollnumber.trim().length()==0)
				continue;
			
			//名字乱码处理
			String strName=null;
			
			
			if(name.getStringRef().getBytes().length == 9 || name.getStringRef().getBytes().length == 8)
			{
				strName = CharsetUtils.Convert(name.getStringRef(), "UTF-8").substring(0,3);
			}else if(name.getStringRef().getBytes().length == 7 || name.getStringRef().getBytes().length == 6)
			{
				strName = CharsetUtils.Convert(name.getStringRef(), "UTF-8").substring(0,2);
			}else if(name.getStringRef().getBytes().length == 11 || name.getStringRef().getBytes().length == 10)
			{
				strName = CharsetUtils.Convert(name.getStringRef(), "UTF-8").substring(0,4);
			}
			
			
//			strName=CharsetUtils.Convert(name.getStringRef(), "UTF-8");
			
			//如果没有名字则跳过
			if(strName==null || strName.trim().length()==0)
				continue;
			
			Map<String,Object> userMap=new HashMap<String,Object>();
			userMap.put("machinenumber", machineNumber.getIntRef());
			userMap.put("enrollnumber", enrollNumber.getStringRef());
			userMap.put("name", strName);
			userMap.put("password", password.getStringRef());
			userMap.put("privilege", privilege.getIntRef());
			userMap.put("enable", enable.getBooleanRef());
			
			listUser.add(userMap);
		}
		
		return listUser;
	}
}

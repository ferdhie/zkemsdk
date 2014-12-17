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
			zkem=new ActiveXComponent("zkemkeeper.ZKEM");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/****************************5.1连接机器相关函数*********************************/
	
	
	/**
	 * 通过IP地址连接机器，和机器建立一个网络连接
	 * 函数原型:VARIANT_BOOL Connect_Net([in] BSTR IPAdd,[in] long Port1)
	 * 
	 * @param ipAddress 机器的IP地址
	 * @param port 连接机器时使用的端口号，默认为4370
	 * @return 连接成功返回true，连接失败返回false
	 */
	public boolean Connect_Net(String ipAddress,int port){
		return zkem.invoke("Connect_Net",new Variant(ipAddress),new Variant(port)).getBoolean();
	}
	
	
	/**
	 * 通过串口连接机器，即RS232或RS485
	 * 函数原型:VARIANT_BOOL Connect_Com([in] long ComPort,[in] long MachineNumber,[in] long BaudRate)
	 * @param comPort 需要连接机器的PC串口号
	 * @param machineNumber 机器号
	 * @param baudRate 波特率
	 * @return 连接成功返回true，连接失败返回false
	 */
	public boolean Connect_Com(int comPort,int machineNumber,int baudRate){
		return zkem.invoke("Connect_Com",new Variant(comPort),new Variant(machineNumber),new Variant(baudRate)).getBoolean();
	}
	
	
	/**
	 * USB连接考勤机
	 * 函数原型:VARIANT_BOOL Connect_USB([in] long MachineNumber)
	 * 
	 * @param inMachineNumber 机器号(输入参数)
	 * @return 连接成功返回true,连接失败返回false
	 */
	public boolean Connect_USB(int machineNumber){
		return zkem.invoke("Connect_USB",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 断开连接的机器，释放相关资源
	 */
	public void Disconnect(){
		zkem.invoke("Disconnect");
	}
	
	
	
	/****************************5.2数据管理相关函数*********************************/
	
	/**
	 * 读取考勤记录到PC的内部缓冲区，同ReadAllGLogData
	 * 函数原型:VARIANT_BOOL ReadGeneralLogData([in] long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号 
	 * @return 缓存成功返回true，缓存失败返回false
	 */
	public boolean ReadGeneralLogData(int machineNumber){
		return zkem.invoke("ReadGeneralLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 读取考勤记录到PC内部缓冲区，同ReadAllGLogData
	 * 函数原型:VARIANT_BOOL ReadAllGLogData([in] long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 缓存成功返回true，缓存失败返回false
	 */
	public boolean ReadAllGLogData(int machineNumber){
		return zkem.invoke("ReadAllGLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取考勤记录，使用该函数前，可是哟个ReadAllGLogData或ReadGeneralLogData将
	 * 考勤记录从机器读到PC内部缓冲区中。该函数每执行一次，指向考勤记录的指针向下一条记录
	 * 函数原型:VARIANT_BOOL SSR_GetGeneralLogData([in] LONG dwMachineNumber,[out] BSTR* dwEnrollNumber,
	 * [out] LONG* dwVerifyMode,[out] LONG* dwInOutMode,[out] LONG* dwYear,[out] LONG* dwMonth,
	 * [out] LONG* dwDay,[out] LONG* dwHour,[out] LONG* dwMinute,[out] LONG* dwSecond,[out]LONG* dwWorkcode)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:指向BSTR型变量的指针，值接收该考勤记录的用户ID号，最大可支持24位
	 * dwVerifyMode:指向long型变量的指针，其值接收记录的验证方式。0为密码验证，1为指纹验证，2为卡验证
	 * dwInOutMode:指向long型变量的指针，其值接收记录的考勤状态。0:CheckIn,1:CheckOut,2:BreakOut,3:BreakIn,4:OTIn,5:OTOut
	 * dwYear/dwMonth/dwDay/dwHour/dwMinute/dwSecond:其值分别接受考勤记录的日期和时间
	 * dwWorkcode:指向long型变量的指针，其值接收记录的Workcode值
	 * 读取成功返回true，读取失败返回false
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回考勤记录列表
	 */
	public List<Map<String,Object>> SSR_GetGeneralLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		boolean status=this.ReadAllGLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant enrollNumber=new Variant("",true);
			Variant verifyMode=new Variant(0,true);
			Variant inoutMode=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			Variant second=new Variant(0,true);
			Variant workCode=new Variant(0,true);
			
			status=zkem.invoke("SSR_GetGeneralLogData",
					new Variant(machineNumber),
					enrollNumber,
					verifyMode,
					inoutMode,
					year,month,day,hour,minute,second,
					workCode).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("enrollnumber", enrollNumber.getIntRef());
				mapLog.put("verifymode", verifyMode.getIntRef());
				mapLog.put("inoutmode", inoutMode.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());
				mapLog.put("second", second.getIntRef());
				mapLog.put("workcode", workCode.getIntRef());
				
				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	/**
	 * 清除机器内所有考勤记录
	 * 函数原型:VARIANT_BOOL ClearGLog([in] dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearGLog(int machineNumber){
		return zkem.invoke("ClearGLog").getBoolean();
	}
	
	
	
	/********************5.22 操作记录相关函数************************/
	
	/**
	 * 读取操作记录到PC的内部缓冲区，同ReadAllSLogData
	 * 函数原型:VARIANT_BOOL ReadSuperLogData([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 读取缓存成功返回true，读取缓存失败返回false
	 */
	public boolean ReadSuperLogData(int machineNumber){
		return zkem.invoke("ReadSuperLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 读取操作记录到PC的内部缓冲区，同ReadSuperLogData
	 * 函数原型:VARIANT_BOOL ReadAllSLogData([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器编号
	 * @return 读取缓存成功返回true，读取缓存失败返回false
	 */
	public boolean ReadAllSLogData(int machineNumber){
		return zkem.invoke("ReadAllSLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录，同GetSuperLogData2,不同
	 * 的是GetSuperLogData2能获取到精确到秒的操作记录时间
	 * 函数原型:VARIANT_BOOL GetSuperLogData([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetSuperLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadSuperLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			
			status=zkem.invoke(
					"GetSuperLogData",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());

				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录.该函数和GetSuperLogData一样
	 * 
	 * 函数原型:VARIANT_BOOL GetAllSLogData([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetAllSLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadAllSLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			
			status=zkem.invoke(
					"GetAllSLogData",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());

				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	/**
	 * 清除机器内所有操作记录
	 * 函数原型:VARIANT_BOOL ClearSLog([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearSLog(int machineNumber){
		return zkem.invoke("ClearSLog").getBoolean();
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录，同GetSuperLogData2,不同
	 * 函数原型:VARIANT_BOOL GetSuperLogData2([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute,[out]long* dwSecs)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetSuperLogData2(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadSuperLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			Variant second=new Variant(0,true);
			
			status=zkem.invoke(
					"GetSuperLogData2",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());
				mapLog.put("second", second.getIntRef());
				
				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	
	/************************5.2.3用户信息相关函数**************************/
	
	
	/**
	 * 读取所有用户信息到PC内存中，包括用户编号，密码，姓名，卡号等(指纹模板除外)。该函数执行完成
	 * 之后，可叼哟个GetAllUserID获取用户信息
	 * 函数原型:VARIANT_BOOL ReadAllUserID([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return
	 */
	public boolean ReadAllUserID(int machineNumber){
		return zkem.invoke("ReadAllUserID",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 设置用户是否可用
	 * 函数原型:VARIANT_BOOL SSR_EnableUser([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in] VARIANT_BOOL bFlag)
	 * 
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param flag 用户启用标志，true为启用，false为禁用
	 * @return 成功设置用户返回true，失败返回false
	 */
	public boolean SSR_EnableUser(int machineNumber,String enrollNumber,boolean flag){
		return zkem.invoke("SSR_EnableUser",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(flag)).getBoolean();
	}
	
	
	/**
	 * 上传用户验证方式或组验证方式，只有多种验证方式的机器可支持该函数
	 * 函数原型:VARIANT_BOOL SetUserInfoEx([in]LONG dwMachineNumber,[in]LONG dwEnrollNumber,
	 * [in]LONG VerifyStyle,[in] BYTE* Reserved)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * VerifyStyle:验证方式
	 * Reserved:保留参数，暂无意义
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param verifyStyle 验证方式,0表示组验证方式，其余参考文档
	 * @return 设置成返回true，设置失败返回false
	 */
	public boolean SetUserInfoEx(int machineNumber,int enrollNumber,int verifyStyle){
		return zkem.invoke("SetUserInfoEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(verifyStyle),
				new Variant(null)).getBoolean();
	}
	
	
	/**
	 * 获取用户验证方式，只有具有多种验证方式的机器可支持该函数
	 * 函数原型:VARIANT_BOOL GetUserInfoEx([in]LONG dwMachineNumber,[in]LONG dwEnrollNumber,
	 * [out]LONG* VerifyStyle,[out] BYTE* Reserved)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @return 读取成功返回该用户的验证方式Map<String,Object>
	 */
	public Integer GetUserInfoEx(int machineNumber,int enrollNumber){
		Integer verify=null;
		Variant verifyStyle=new Variant(0,true);
		boolean status=zkem.invoke("GetUserInfoEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				verifyStyle,new Variant(null)).getBoolean();
		
		if(status==true){
			verify=verifyStyle.getIntRef();
		}
		
		return verify;
	}
	
	
	/**
	 * 删除指定用户设置的多种验证方式，只有多种验证方式的机器可以支持该函数
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @return 删除用户验证成功返回true，删除用户验证失败返回false
	 */
	public boolean DeleteUserInfoEx(int machineNumber,int enrollNumber){
		return zkem.invoke("DeleteUserInfoEx",new Variant(machineNumber),new Variant(enrollNumber)).getBoolean();
	}
	
	
	/**
	 * 获取所有用户信息 ，在执行该函数之前，可用ReadAllUserID读取所有用户信息到PC内存，SSR_GetAllUserInfo每
	 * 执行一次，指向用户信息指针移动到下一条记录，当读完所有的用户信息后，函数返回false
	 * 函数原型:VARIANT_BOOL SSR_GetAllUserInfo([in]LONG dwMachineNumber,[out]BSTR* dwEnrollNumber,
	 * [out] BSTR* Name,[out]BSTR* Password,[out] LONG* Privilege,[out] VARIANT_BOOL* Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限，3管理员，0普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @param inMachineNumber 机器编号
	 * @return List<Map<String,Object>>用户信息,读取失败返回null
	 */
	public List<Map<String,Object>> SSR_GetAllUserInfo(int inMachineNumber){
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
			
//			
//			//如果没有名字则跳过
//			if(strName==null || strName.trim().length()==0)
//				continue;
			
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
	
	
	/**
	 * 获取指定用户的信息
	 * 函数原型:VARIANT_BOOL SSR_GetUserInfo([in] LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [out]BSTR* Name,[out]BSTR* Password,[out]LONG* Privilege,[out]VARIANT_BOOL* Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限,3管理员，0普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @param machineNumebr 机器号
	 * @param enrollNumber 用户号 
	 * @return
	 */
	public Map<String,Object> SSR_GetUserInfo(int machineNumber,String enrollNumber){
		Variant name=new Variant("",true);
		Variant password=new Variant("",true);
		Variant privilege=new Variant(0,true);
		Variant enable=new Variant(0,true);
		
		boolean status=zkem.invoke("SSR_GetUserInfo",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				name,password,privilege,enable).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUser=new HashMap<String,Object>();
		mapUser.put("name", name.getStringRef());
		mapUser.put("password", password.getStringRef());
		mapUser.put("privilege", privilege.getIntRef());
		mapUser.put("enable", enable.getIntRef());
		
		return mapUser;
	}
	
	
	/**
	 * 设置指定用户的用户信息，若机内没有该用户，则会创建该用户
	 * 函数原型:VARIANT_BOOL SSR_SetUserInfo([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]BSTR Name,[in]BSTR Password,[in]LONG Privilege,[in]VARIANT_BOOL Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限，3为管理员，0为普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SSR_SetUserInfo(int machineNumber,String enrollNumber,String name,
										String password,int privilege,boolean enable){
		return zkem.invoke("SSR_SetUserInfo",
							new Variant(machineNumber),
							new Variant(enrollNumber),
							new Variant(name),
							new Variant(password),
							new Variant(privilege),
							new Variant(enable)).getBoolean();
	}
	
	
	
	/*********************5.2.4登记数据(同时包括用户信息和指纹)**************************/
	
	
	/**
	 * 删除登记数据
	 * 函数原型:VARIANT_BOOL SSR_DeleteEnrollData([in]LONG dwMachineNumber,
	 * 						[in]BSTR dwEnrollNumber,[in]LONG dwBackupNumber)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param backupNumber 指纹索引，一般范围为0-9，同时会查询该用户是否还有其他指纹和密码，如果没有，则删除该用户。
		当前为10代表删除的是密码，同时会查询该用户是否有指纹数据，如果没有，则删除该用户。为11是代表删除该用户所有指纹数据，
		当前为12代表删除该用户(包括所有指纹和卡号、密码数据)
	 * 
	 * @return 删除成功返回true，删除失败返回false
	 */ 
	public boolean SSR_DeleteEnrollData(int machineNumber,String enrollNumber,int backupNumber){
		return zkem.invoke("SSR_DeleteEnrollData",new Variant(machineNumber),
							new Variant(enrollNumber),new Variant(backupNumber)).getBoolean();
	}
	
	
	/**
	 * 删除登记数据，和SSR_DeleteEnrollData不同的是删除所有指纹数据可用参数13实现，该函数具有更高效率
	 * 函数原型:VARIANT_BOOL SSR_DeleteEnrollDataExt([in]LONG machineNumber,[in]BSTR enrollNumber,[in]LONG backupNumber)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param backupNumber 指纹索引，一般范围为0-9，同时会查询该用户是否还有其他指纹和密码，如果没有，则删除该用户。
		当前为10代表删除的是密码，同时会查询该用户是否有指纹数据，如果没有，则删除该用户。为11和13是代表删除该用户所有指纹数据，
		当前为12代表删除该用户(包括所有指纹和卡号、密码数据)
	 * @return 删除成功返回true，删除失败返回false
	 */
	public boolean SSR_DeleteEnrollDataExt(int machineNumber,String enrollNumber,int backupNumber){
		return zkem.invoke("SSR_DeleteEnrollDataExt",new Variant(machineNumber),
				new Variant(enrollNumber),new Variant(backupNumber)).getBoolean();
	}
	
	
	
	/*************************5.2.5指纹模板相关函数****************************/
	
	
	/**
	 * 读取机器内所有指纹模板到PC内存，该函数一次性将所有指纹读到内存
	 * @param machineNumber 机器号
	 * @return 读取成功返回true，读取失败返回false
	 */
	public boolean ReadAllTemplate(int machineNumber){
		return zkem.invoke("ReadAllTemplate",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式获取用户指纹模板，和SSR_GetUserTmpStr不同的仅是模板格式不同而已
	 * 函数原型:VARIANT_BOOL SSR_GetUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[out]BYTE* TmpData,[out]LONG* TmpLength)
	 * TmpData:该参数返回指纹模板数据
	 * TmpLength:该参数返回指纹模板数据长度
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引，一般范围为0-9
	 * 
	 * @return Map<String,Object>的用户指纹模板,读取失败返回null
	 */
	public Map<String,Object> SSR_GetUserTmp(int machineNumber,int enrollNumber,int fingerIndex){
		Variant tmpdata=new Variant("",true);
		Variant tmplength=new Variant(0,true);
		boolean status=zkem.invoke("SSR_GetUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> userTmp=new HashMap<String,Object>();
		userTmp.put("tmpdata", tmpdata.getByteRef());
		userTmp.put("tmplength", tmplength.getIntRef());
		
		return userTmp;
	}
	
	
	/**
	 * 以字符串方式获取用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_GetUserTmpStr([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[out]BYTE* TmpData,[out]LONG* TmpLength)
	 * TmpData:该参数返回指纹模板数据
	 * TmpLength:该参数返回指纹模板数据长度
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引，一般范围为0-9
	 * 
	 * @return Map<String,Object>的用户指纹模板,读取失败返回null
	 */
	public Map<String,Object> SSR_GetUserTmpStr(int machineNumber,int enrollNumber,int fingerIndex){
		Variant tmpdata=new Variant("",true);
		Variant tmplength=new Variant(0,true);
		boolean status=zkem.invoke("SSR_GetUserTmpStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> userTmp=new HashMap<String,Object>();
		userTmp.put("tmpdata", tmpdata.getStringRef());
		userTmp.put("tmplength", tmplength.getIntRef());
		
		return userTmp;
	}
	
	
	/**
	 * 以二进制方式上传用户指纹模板，和SSR_SetUserTmpStr不同的是指纹模板格式不同而已
	 * 函数原型:VARIANT_BOOL SSR_SetUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[in]BYTE* TmpData)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 上传成功返回true，上传失败返回false
	 */
	public boolean SSR_SetUserTmp(int machineNumber,String enrollNumber,int fingerIndex,byte tmpData){
		return zkem.invoke("SSR_SetUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 以字符串方式上传用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_SetUserTmpStr([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[in]BSTR TmpData)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 上传成功返回true，上传失败返回false
	 */
	public boolean SSR_SetUserTmpStr(int machineNumber,String enrollNumber,int fingerIndex,String tmpData){
		return zkem.invoke("SSR_SetUserTmpStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 删除用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_DelUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,[in]LONG dwFingerIndex)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @return 删除成功返回true，删除失败返回false
	 * 
	 */
	public boolean SSR_DelUserTmp(int machineNumber,String enrollNumber,int fingerIndex){
		return zkem.invoke("SSR_DelUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex)).getBoolean();
	}
	
	
	/**
	 * 上传用户指纹模板，为SSR_SetUserTmp的加强版
	 * 
	 * @param machineNumber 机器号
	 * @param isDeleted 删除标准，即上传时已存在该用户的指定索引号的指纹是否覆盖原指纹，1为覆盖，0为不覆盖
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SSR_SetUserTmpExt(int machineNumber,int isDeleted,String enrollNumber,int fingerIndex,byte tmpData){
		return zkem.invoke("SSR_SetUserTmpExt",
				new Variant(machineNumber),
				new Variant(isDeleted),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 删除指定用户的指纹模板，和DelUserTmp的区别在于前者可以支持24位用户号
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @return 删除成功返回true，删除失败返回false
	 */
	public boolean SSR_DelUserTmpExt(int machineNumber,String enrollNumber,int fingerIndex){
		return zkem.invoke("SSR_DelUserTmpExt",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式上传用户普通指纹模板或者胁迫指纹模板，和SetUserTmpExStr不同的仅是指纹模板格式不同而已
	 * 注意:机器上必须已存在该用户或者将用户信息同时上传，相同用户的相同索引号模板如果已经登记，则覆盖。
	 * 注:要求机器固件支持胁迫指纹功能
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 
	 * @return false
	 * 
	 * @deprecated 该函数的接口文档有问题
	 */
	public boolean SetUserTmpEx(int machineNumber,String enrollNumber,int fingerIndex,byte tmpData){
		/********该函数的接口文档有问题*******/
		return false;
	}
	
	
	/**
	 * 以字符串形式上传用户普通指纹模板或者胁迫指纹模板，和SetUserTmpEx不同的仅是指纹模板格式不同而已
	 * 
	 * @param machineNumber 机器号
	 * @param erollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示为胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetUserTmpExStr(int machineNumber,String enrollNumber,int fingerIndex,int flag,String tmpData){
		return zkem.invoke("SetUserTmpExStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(flag),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式下载用户普通指纹模板或者胁迫指纹模板，和GetUserTmpExStr不同的仅是指纹模板格式不同而已
	 * 注：要求机器固件支持胁迫指纹功能(固件内部版本号Ver6.60及以上)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或者是否为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @param tmpLength 指纹模板长度
	 * @return Map<String,Object>格式的用户指纹模板数据
	 */
	public Map<String,Object> GetUserTmpEx(int machineNumber,String enrollNumber,
								int fingerIndex,int flag,byte tmpData,int tmpLength){
		
		Variant v_flag=new Variant(0,true);
		Variant tmpdata=new Variant(0,true);
		Variant tmplength=new Variant(0,true);
		
		boolean status=zkem.invoke("GetUserTmpEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				v_flag,
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUsertmp=new HashMap<String,Object>();
		mapUsertmp.put("flag", v_flag.getIntRef());
		mapUsertmp.put("tmpdata", tmpdata.getByteRef());
		mapUsertmp.put("tmplength", tmplength.getIntRef());
		
		return mapUsertmp;
	}
	
	
	/**
	 * 以字符串方式下载用户普通指纹模板或者胁迫指纹模板
	 * 注：要求机器固件支持胁迫指纹功能(固件内部版本号Ver6.60及以上)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或者是否为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @param tmpLength 指纹模板长度
	 * @return Map<String,Object>格式的用户指纹模板数据
	 */
	public Map<String,Object> GetUserTmpExStr(int machineNumber,String enrollNumber,
								int fingerIndex,int flag,String tmpData,int tmpLength){
		
		Variant v_flag=new Variant(0,true);
		Variant tmpdata=new Variant(0,true);
		Variant tmplength=new Variant(0,true);
		
		boolean status=zkem.invoke("GetUserTmpExStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				v_flag,
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUsertmp=new HashMap<String,Object>();
		mapUsertmp.put("flag", v_flag.getIntRef());
		mapUsertmp.put("tmpdata", tmpdata.getStringRef());
		mapUsertmp.put("tmplength", tmplength.getIntRef());
		
		return mapUsertmp;
	}
	
	
	/**
	 * 根据节假日编号获取机器上的节假日设置
	 * 函数原型:VARIANT_BOOL SSR_GetHoliday([in]LONG dwMachineNumber,[in]LONG HolidayID,
	 * [out]LONG* BeginMonth,[out]LONG* BeginDay,[out]LONG* EndMonth,[out]LONG EndDay,[out]LONG TimeZoneID)
	 * 
	 * dwMachineNumber:机器号
	 * HolidayID:节假日编号
	 * BeginMonth/BeginDay/EndMonth/EndDay:该参数接收节假日的开始日期结束日期
	 * TimeZoneID:该参数接受节假日的时间段编号
	 * 
	 * @param machineNumber 机器号
	 * @param holidayID 节假日编号
	 * @return 
	 */
	public boolean SSR_GetHoliday(int machineNumber,int holidayID){
		
	}
}

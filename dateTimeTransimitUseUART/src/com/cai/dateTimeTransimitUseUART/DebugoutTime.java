package com.cai.dateTimeTransimitUseUART;

import java.util.Arrays;

/*
 * ����ʱ���ʽ�Ľ���
 * debug
 * 
 */
public class DebugoutTime 
{
	public static void main(String[] args) 
	{
		String currentTime=SystemDateTimeGet.getCurrentDateTime();
		byte[] timeString=SystemDateTimeGet.dateTimeBytesGet(currentTime);
		System.out.println(Arrays.toString(timeString));
//		for(int j=0;j<timeString.length;j++)
//		{
//			int temp=SystemDateTimeGet.bytetoUnsigendInt(timeString[j]);
//			System.out.println(temp);
//		}
		System.out.println(SystemDateTimeGet.getCurrentDateTime());
	}
}

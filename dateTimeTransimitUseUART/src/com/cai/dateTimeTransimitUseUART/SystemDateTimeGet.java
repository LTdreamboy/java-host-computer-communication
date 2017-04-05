package com.cai.dateTimeTransimitUseUART;
import java.util.Calendar;
import java.util.Date;
/*
 * ��ȡϵͳ��ǰʱ��
 */
public class SystemDateTimeGet
{
	//����� ʱ���У���
	static int  timeCheckSum=0;

	public static String getCurrentDateTime()
	{
		//����ģʽ
		 Calendar calendar=Calendar.getInstance();
		 int year = calendar.get(Calendar.YEAR);//��ȡ���  
         int month=calendar.get(Calendar.MONTH);//��ȡ�·�   
         int day=calendar.get(Calendar.DATE);//��ȡ����  
         int minute=calendar.get(Calendar.MINUTE);//��   
         int hour=calendar.get(Calendar.HOUR);//Сʱ   
         int second=calendar.get(Calendar.SECOND);//��  
         if(hour>=12)
         {
        	 hour=hour+12;
         }
         String curerentDateTime= year + " " + (month + 1 )+ " " + day + " "+ hour + " " + minute + " " + second + " ";
         timeCheckSum=year+(month+1)+day+(hour+12)+minute+second;
         return curerentDateTime;  
	}
	/*
	 * ������ʱ���ַ������и�����byte[]����
	 */
	public static byte[] dateTimeBytesGet(String currenDateTime)
	{
		//�Ե�ǰʱ��������и�ʽ�ж�
		//�Ը�ʽ�����ж�
		int rawDataSize=6;
		byte[] dateTimeBytes=new byte[rawDataSize+1];
		String[] currentDateTimeSplit=currenDateTime.split(" ");
		if(currentDateTimeSplit.length==rawDataSize)
		{
			//ʱ�����ݸ�ʽ��ȷ
			//eg 2016 12 23 22 18 26
			//ʹ��byte[]���д洢ʱ��Ҫ -128~+127
		    //�������ʹ������byte�洢
			for(int dataIndex=0;dataIndex<rawDataSize;dataIndex++)
			{
				int dateTemp=Integer.parseInt(currentDateTimeSplit[dataIndex]);
				if(dataIndex==0)
				{
					byte H8bits=(byte)((dateTemp)>>8);
					byte L8bits=(byte)((dateTemp)&0xff);
					dateTimeBytes[dataIndex]= H8bits;
					dateTimeBytes[dataIndex+1]= L8bits;
				}
				dateTimeBytes[dataIndex+1]=(byte)dateTemp;
			}
		}else
		{
			System.out.println("��ǰʱ���ȡ�����쳣����");
			System.exit(-1);
			dateTimeBytes=null;
		}
		return dateTimeBytes;
	}
	/*
	 * ��ʱ���ʽ���н�������ԭԭ����ʱ���ʽ
	 * �����ݽ��л�ԭ
	 * ������debugʹ��
	 */
	public static String dateTimeBytesfromTostring(byte[] currentDateTime)
	{
		String string="";
		if(currentDateTime.length==7)
		{
		  string=((currentDateTime[0]<<8)+bytetoUnsigendInt(currentDateTime[1]))+" "+currentDateTime[2]+" "+
		  currentDateTime[3]+" "+currentDateTime[4]+" "+currentDateTime[5]+" "+
		  currentDateTime[6];
		}

		return string;
	}
	
	/*
	 * ��byteת��Ϊ�ַ���
	 * ���з���byteת��Ϊ�޷�������
	 * debugʹ��
	 */
	public  static int bytetoUnsigendInt(byte aByte)
	{   
		
		String s=String.valueOf(aByte);
		System.out.println(s);
		//System.out.println(s);
		int bytetoUnsigendInt=0;
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)!='0')
			{
				bytetoUnsigendInt+=1<<(7-i);
			}
		}
		return bytetoUnsigendInt;
	}
	/*
	* �������װ��֡
	* ÿһ������֡�����¼����������
	* 1)���ݰ�ͷ�� head 0X2F
	* 2)���ݰ����� CMD  0X5A
	* 3)���ݸ���     length of data 7
	* 4)У���         H8/L8 byte of  check sum(���ֽ���ǰ ���ֽ��ں�)
	* 5)���ݽ�β��־ tail OX30
	* 6)�ɲ����߳̽��л�ȡ��ǰʱ��
	*/
	public static byte[] makeCurrentDateTimefromStringtoFramePackage(byte[] dateTimeBytes)
	{
		//��ʱ��byte[]ǰ�����һЩpackageУ����Ϣ
		int dataLength=13;
		byte[] terimalTimePackage=new byte[dataLength];
		//װ����Ϣ
		//ʱ�����ݰ�֮ǰ����Ϣ
		terimalTimePackage[0]=0x2F;
		terimalTimePackage[1]=0X5A;
		terimalTimePackage[2]=7;
		//����У���
		//ת��Ϊ�޷��Ž���У��
		for(int dataIndex=0;dataIndex<dateTimeBytes.length;dataIndex++)
		{
			terimalTimePackage[dataIndex+3]=dateTimeBytes[dataIndex];
		}
		//��У��ͷ�Ϊ�ߵ��ֽ�
		byte sumH8bits=(byte)((timeCheckSum)>>8);
		byte sumL8bits=(byte)((timeCheckSum)&0xff);
		terimalTimePackage[10]=sumH8bits;//���ֽ���ǰ
		terimalTimePackage[11]=sumL8bits;//���ֽ��ں�
		//���ݰ���β
		terimalTimePackage[12]=0X30;
		return terimalTimePackage;
	}
}


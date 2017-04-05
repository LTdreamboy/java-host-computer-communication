package com.cai.dateTimeTransimitUseUART;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import javax.sql.rowset.serial.SerialException;
/*
 * ��Ҫ��ʵ�ֶ�ϵͳʱ������ݰ���װͨ��windows UART���͸����������ʱ��У׼
 * ��������Ҫ��ʵ����λ������ʵ�ֶ�ʱ�����ݰ������ݵĸ�ʽ�Ķ��������
 * ���ݶ����ʱ��packageg��ʽ�ڵ�����Ͻ��н���
 * Version 1.0
 * Author��caizq
 * date time:2016/12/23
 * location��tju lab349
 * ��ʵ������
 * step 1:����UART ͨ�����ݰ�
 * step 2:����ģʽ��ȡϵͳʱ��(��λ�ȡ������)
 * step 3:�����̣߳����߳���Ҫ����ȡʱ���ʱ�����ݰ����з���������
 * step 4:�򵥵Ĳ��Դ���(����ʹ���̼߳�ش���buffer���ݲ���ʾ,���Խ�������ӡ��־)
 * step 5:ʵ�ֽ���ķ�װ
 * step 6:���UART���ճ���
 * step 7:��һЩ���ν��м���ʾ�Լ������ݽ��д洢
 * step 8:mysql���ݽ��д洢 JDBC
 * step 9:��һ��ʵ�ֽ���ķ�װ
 * step 10:����Ҫ��Ϣ�Ŀ��ӻ�
 */
public class dateTimeTransimitUseUARTMain
{
	public static void main(String[] args) 
	{
		//��mian��
		byte[] dataFrame={0x00,0x5A,0x64,0x56,0x43,0x6F,0x78};
		ArrayList<String> arraylist=UARTParameterSetup.uartPortUseAblefind();
		int useAbleLen=arraylist.size();
		if(useAbleLen==0)
		{
			System.out.println("û���ҵ����õĴ��ڶ˿ڣ���check�豸��");
		}
		else
		{   
			System.out.println("�Ѳ�ѯ���ü�����������¶˿ڿ���ʹ�ã�");
			for(int index=0;index<arraylist.size();index++)
			{
				System.out.println("��COM�˿�����:"+arraylist.get(index));
				//���Դ������õ���ط���
			} 
			//ȡ����һ��COM�˿ڽ��в���
			SerialPort serialPort=UARTParameterSetup.portParameterOpen(arraylist.get(0), 57600);
		    //�˳����� ��������Ҫ��� ��Ϊtransimitһֱ��Ҫ��֤����״̬
			//System.exit(0);
			DataTransimit.uartSendDatatoSerialPort(serialPort, dataFrame);
			String currentDateTime=SystemDateTimeGet.getCurrentDateTime();
			System.out.println(currentDateTime);
			byte[] bytes=SystemDateTimeGet.dateTimeBytesGet(currentDateTime);
			//System.out.println(Arrays.toString(bytes));
			String str=SystemDateTimeGet.dateTimeBytesfromTostring(bytes);
			System.out.println(str);
			//System.out.println(SystemDateTimeGet.bytetoUnsigendInt((byte) -32));
			byte[] terimalTimeByte=SystemDateTimeGet.makeCurrentDateTimefromStringtoFramePackage(bytes);
			System.out.println(Arrays.toString(terimalTimeByte));
			DataTransimit.uartSendDatatoSerialPort(serialPort, terimalTimeByte);
			//�رմ���
			UARTParameterSetup.closePort(serialPort);
		}		
	}
}
/*����һ���������ݰ����ಢ��һЩ�򵥵Ĳ���
 * ��Ҫ�Ǵ���ͨ�ŵ�һЩ�쳣����Լ���ʾ
 * ��ͨ�Ŵ��ڵ�һЩ��������������
 * ͨ�����ⲿ���õ�jar�����ڹ�����lib�ļ���
 * ����ļ����������ǶԴ��ڵļ�������ã�����Ҫ�����޸� ����һ����Ĳ������ʹ��static�෽��
 * ���ü̳�Ȩ�ޣ���ϣ������չ��̳У����������η�Ϊ��final
 */
class UARTParameterSetup
{
	/*�෽�� ���ɸı� �����ܼ̳�
	 * ɨ���ȡ���õĴ���
	 * �����ô��������list��������list
	 */
	public static final ArrayList<String> uartPortUseAblefind()
	{
		//��ȡ��ǰ���п��ô��� 
		//��CommPortIdentifier���ṩ����
		Enumeration<CommPortIdentifier> portList=CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> portNameList=new ArrayList();
		//��Ӳ�����ArrayList
		while(portList.hasMoreElements())
		{
			String portName=portList.nextElement().getName();
			portNameList.add(portName);	
		}
		return portNameList;
	}
	/*
	 * ���ڳ�������
	 * 1)�򿪴���
	 * 2)���ò����� ���ݵ�����������������Ϊ57600 ...
	 * 3)�ж϶˿��豸�Ƿ�Ϊ�����豸
	 * 4)�˿��Ƿ�ռ��
	 * 5)��������������check�Ժ󷵻�һ���������ö���new UARTParameterSetup()
	 * 6)return:����һ��SerialPortһ��ʵ���������ж���com���Ǵ�������в�������
	 *   �������򷵻�SerialPort����Ϊnull
	 */
	public static final SerialPort portParameterOpen(String portName,int baudrate)
	{
		SerialPort serialPort=null;
		try 
		{  //ͨ���˿���ʶ�𴮿�
		   CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		   //�򿪶˿ڲ����ö˿����� serialPort�ͳ�ʱʱ�� 2000ms
		   CommPort commPort=portIdentifier.open(portName,1000);
		   //��һ���ж�comm�˿��Ƿ��Ǵ��� instanceof
		   if(commPort instanceof SerialPort)
		   {
			   System.out.println("��COM�˿��Ǵ��ڣ�");
			   //��һ��ǿ������ת��
			   serialPort=(SerialPort)commPort;
			   //����baudrate �˴���Ҫע��:������ֻ��������int�� ����57600�㹻
			   //8λ����λ
			   //1λֹͣλ
			   //����żУ��
			   serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			   //����������� log
			   System.out.println("���ڲ�����������ɣ�������Ϊ"+baudrate+",����λ8bits,ֹͣλ1λ,����żУ��");
		   }
		   //���Ǵ���
		   else
		   {
			   System.out.println("��com�˿ڲ��Ǵ���,�����豸!");
			   //��com�˿�����Ϊnull Ĭ����null����Ҫ����
		   }
			  
		} 
		catch (NoSuchPortException e) 
		{
			e.printStackTrace();
		} 
		catch (PortInUseException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedCommOperationException e)
        {
			e.printStackTrace();
		}
		
		return serialPort;		
	}
    /*
     * �رմ���
     * ���ڹر��Լ�����COM�˿ڷǴ��ڵı�־�Ƿ���һ��SerialPort��null
     * �رմ��ں�ʹ��null��������
     */
    public static void closePort(SerialPort serialPort)
    {
    	if(serialPort!=null)
    	{
    		serialPort.close();
    		serialPort=null;
    		System.out.println("�����ѹرգ�");
    	}
    }
     
}

/*
 * �������ݷ����Լ����ݴ�����Ϊһ����
 * ��������Ҫʵ�ֶ����ݰ��Ĵ������µ����
 */
class DataTransimit
{
	
	/*
	 * ��λ���������ͨ�����ڷ�������
	 * ���ڶ��� seriesPort 
	 * ����֡:dataPackage
	 * ���͵ı�־:����δ���ͳɹ��׳�һ���쳣
	 */
	public static void uartSendDatatoSerialPort(SerialPort serialPort,byte[] dataPackage)
	{
		OutputStream out=null;
		try
		{
			out=serialPort.getOutputStream();
			out.write(dataPackage);
			out.flush();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}finally
		{
			//�ر������
			if(out!=null)
			{
				try 
				{
					out.close();
					out=null;
					System.out.println("�����ѷ������!");
				} catch (IOException e) 
				{
					e.printStackTrace();
				}	
			}
		}			
     }
	/*
	 * ��λ����������
	 * ���ڶ���seriesPort
	 * ��������buffer
	 * ����һ��byte����
	 */
	public  static  byte[] uartReceiveDatafromSingleChipMachine(SerialPort serialPort)
	{
		byte[] receiveDataPackage=null;
		InputStream in=null;
		try 
		{
			in=serialPort.getInputStream();
			//��ȡdata buffer���ݳ���
			int bufferLength=in.available();
			while(bufferLength!=0)
			{
				receiveDataPackage=new byte[bufferLength];
				in.read(receiveDataPackage);
				bufferLength=in.available();
				
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return receiveDataPackage;
	}		
    /*
     * ������
     * ���ݽ�����ͨ��ʱ�����߳����
     * ��Ҫ�����߳̽��в���
     */
    public static void listener(SerialPort port,SerialPortEventListener listener)
    {
    	//������Ӽ�����
    	try 
    	{
			port.addEventListener(listener);
		} catch (TooManyListenersException e)
		{
			e.printStackTrace();
		}
    	//���õ�ǰ��Ч
    }
}
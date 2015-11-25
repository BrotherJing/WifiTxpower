package com.tqd.utils;

public class Test {

	public static void main(String[] args) {
		  IPSeeker ip=new IPSeeker("qqwry.dat","/");  
	         //≤‚ ‘IP 58.20.43.13  
	System.out.println(ip.getIPLocation("125.88.193.249").getCountry()+":"+ip.getIPLocation("58.20.43.13").getArea());  
	
	}
}

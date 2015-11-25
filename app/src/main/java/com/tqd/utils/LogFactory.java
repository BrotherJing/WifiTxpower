package com.tqd.utils;

import java.util.logging.Level;

 

public class LogFactory {
	 
	   
	  
	    public static void log(String info,   Throwable ex) {  
	       android.util.Log.i(info,ex.getMessage());
	    }  
	      
	 
}

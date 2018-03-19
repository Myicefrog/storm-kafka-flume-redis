package net.zengzhiying;

import net.zengzhiying.JNIDemo;
/**
 * Hello world!
 *
 */
public class App 
{
    	public static void main( String[] args )
    	{
		JNIDemo j=new JNIDemo();
        	String user="javastr";
                String ent=j.testString(user);
                System.out.println(ent);
        	System.out.println( "Hello World!" );
    }
}

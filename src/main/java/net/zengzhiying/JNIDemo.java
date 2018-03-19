package net.zengzhiying;

public class JNIDemo {
        static{
                //System.loadLibrary ("test");
                System.load ("/home/luguang/demo/storm-1.1/storm-kafka-flume-redis/src/main/java/net/zengzhiying/libtest.so");
        }
        public native String testString(String str);
        /*
        public static void main(String[] args) {
		JNIDemo j=new JNIDemo();
		String user="javastr";
		String ent=j.testString(user);
		System.out.println(ent);
		
                // TODO Auto-generated method stub
        }
        */
}

package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class HelloWorldClientServer {

	public static void main(String[] args) {
		
		System.setSecurityManager(new RMISecurityManager());
		if(args.length > 0){
			
			ConntectToServer(args[0]);
			
		}else{
			
			ListenForClient("127.0.0.1:1337");
		}
	}
	
	public static void ConntectToServer(String addr){
		String url = "rmi://" + addr + "/HelloWorld";
		try {
			HelloWorld helloWorld = (HelloWorld)Naming.lookup(url);
			
			helloWorld.SayHello();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void ListenForClient(String localAddr){
		
		HelloWorld helloWorld = null;
		try {
			helloWorld = new HelloWorldImpl();
			Naming.rebind("rmi://" + localAddr + "/HelloWorld", helloWorld );
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
}

package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class HelloWorldClientServer {

	public static void main(String[] args) {
		
		System.setProperty("java.security.policy","file:///home/.../<filename>.policy");
		if(args.length > 0){
			
			ConntectToServer(args[0]);
			
		}else{
			
			ListenForClient("127.0.0.1");
		}
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ConntectToServer(String addr){
		System.out.println("helldakjsdhaksjalsd");
		String url = "rmi://" + addr + "/HelloWorld";
		System.out.println(url);
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
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

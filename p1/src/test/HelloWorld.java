package test;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

public interface HelloWorld extends Remote{
	
	
	public void SayHello() throws RemoteException;
	
}



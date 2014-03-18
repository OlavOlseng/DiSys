package test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloWorldImpl extends UnicastRemoteObject implements HelloWorld {

	protected HelloWorldImpl() throws RemoteException {
		super();

	}

	@Override
	public void SayHello() throws RemoteException {
		System.out.println("Hello, World!");
		
	}
	
}

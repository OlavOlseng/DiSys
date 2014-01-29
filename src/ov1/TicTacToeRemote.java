package ov1;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.event.ListSelectionEvent;

public interface TicTacToeRemote extends Remote {

	public void setStatusMessage(String status) throws RemoteException;
	public void valueChanged(ListSelectionEvent e) throws RemoteException;
}

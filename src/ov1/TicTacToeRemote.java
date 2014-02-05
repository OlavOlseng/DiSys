package ov1;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.event.ListSelectionEvent;

/*This is the RMI interface that will be used to communicate between the two players.
This implementation does not have a centralized model, instead the changes in to local model are also
done on the remote object through these methods.
*/
public interface TicTacToeRemote extends Remote {
	
	public void setStatusMessage(String status) throws RemoteException;
	public void valueChanged(ListSelectionEvent e) throws RemoteException;
	public void clientConnected(TicTacToeRemote registryUrl) throws RemoteException;
	public void setCell(int x, int y) throws RemoteException;
	
}

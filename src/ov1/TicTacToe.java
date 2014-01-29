package ov1;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import sun.misc.Resource;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * A Tic Tac Toe application.
 * Currently this is a stand-alone application where
 * players take alternating turns using the same computer.
 * <p/>
 * The task is to transform it to a networking application using RMI.
 */
public class TicTacToe extends UnicastRemoteObject implements ListSelectionListener, TicTacToeRemote
{

	JFrame frame;
	private static final int BOARD_SIZE = 15;
	private final BoardModel boardModel;
	private final JTable board;
	private final JLabel statusLabel = new JLabel();
	private final char playerMarks[] = {'X', 'O'};
	private TicTacToeRemote remotePlayer;
	private int currentPlayer = 0; // Player to set the next mark.
	private int thisPlayer = 0;
	private boolean isWon = false;
	
	public static void main(String args[])
	{
		TicTacToe ttt = null;
		try {
			ttt = new TicTacToe();
			if (args.length == 0)
			{
				ttt.thisPlayer = 0;
				ttt.statusLabel.setText("Waiting for other player...");
				//This client had no arguments, and is therefore waiting for a remote client.
				ttt.waitForConnection();
				
			}
			else
			{
				ttt.thisPlayer = 1;
				ttt.statusLabel.setText(String.format("Connecting to host at %s...", args[0]));
				//This client had arguments and is there initiating a connection with someone else.
				ttt.connectToHost(args[0]);
				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			ttt.statusLabel.setText("FATAL ERROR....");
			System.err.println("Fatal Error...");
			System.exit(1337);
		}
	}

	public TicTacToe() throws RemoteException
	{
		super();
		JFrame frame = new JFrame("TDT4190: Tic Tac Toe");
		boardModel = new BoardModel(BOARD_SIZE);
		board = new JTable(boardModel);
		board.setFont(board.getFont().deriveFont(25.0f));
		board.setRowHeight(30);
		board.setCellSelectionEnabled(true);
		for (int i = 0; i < board.getColumnCount(); i++)
			board.getColumnModel().getColumn(i).setPreferredWidth(30);
		board.setGridColor(Color.BLACK);
		board.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultTableCellRenderer dtcl = new DefaultTableCellRenderer();
		dtcl.setHorizontalAlignment(SwingConstants.CENTER);
		board.setDefaultRenderer(Object.class, dtcl);
		board.getSelectionModel().addListSelectionListener(this);
		board.getColumnModel().getSelectionModel().addListSelectionListener(this);

		statusLabel.setPreferredSize(new Dimension(statusLabel.getPreferredSize().width, 40));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(board, BorderLayout.CENTER);
		contentPane.add(statusLabel, BorderLayout.SOUTH);
		frame.pack();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		int centerX = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - frame.getSize().width) / 2;
		int centerY = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - frame.getSize().height) / 2;
		frame.setLocation(centerX, centerY);
		frame.setVisible(true);
	}

	
	public void clientConnected(TicTacToeRemote remote)
	{
		System.out.println(remote);
		remotePlayer = remote;
		this.statusLabel.setText("Other player connected...");
	}
	
	/**
	 * This binds a name for itself in the rmi registry on port 3320, so other people can find it.
	 */
	void waitForConnection() {
		String url = "rmi://127.0.0.1:3320/TicTacToeHost";
		try {
			Naming.rebind(url, this);
			
		} catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * This methods tries to register a remote object on the given ip address.
	 * After it registers itself in the rmi registry on port 3320.
	 * It then registers itself on the clientside by invoking a method with itself as a parameter.
	 * @param address IP address and port should be set to 3320.
	 */
	void connectToHost(String address) 
	{
		String url = "rmi://" + address +"/TicTacToeHost";
		try {
			remotePlayer = (TicTacToeRemote) Naming.lookup(url);
			remotePlayer.clientConnected(this);
			Naming.rebind("rmi://" + address +"/TicTacToeClient", this);
			statusLabel.setText("Connection success...");
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
	
	
	/***
	 * A remote method invoked on the other players board. 
	 * It updates the board to the correct state. It is called automagically when the local board is changed.
	 * It also sets the currentPlayer variable correctly.
	 */
	public void setCell(int x, int y)
	{
		isWon = boardModel.setCell(x, y, playerMarks[currentPlayer]);
		if (!isWon)
			currentPlayer = 1 - currentPlayer;
	}
	
	/***
	 * This method invokes itself recursively on the remote client. 
	 * Using this method to set the status text will result in the same message being displayed on the remote client.
	 */
	public void setStatusMessage(String status)
	{
		if(this.statusLabel.getText().equals(status))
			return;
		statusLabel.setText(status);
		try {
			remotePlayer.setStatusMessage(status);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This has to be modified. Currently the application is stand-alone so
	 * both players have to use the same computer.
	 * <p/>
	 * When completed, marks from the first player originates from a ListSelectionEvent
	 * and is then sent to the second player. And marks from the second player is received
	 * and added to the board of the first player.
	 * CHECK
	 */
	public void valueChanged(ListSelectionEvent e)
	{
		if (isWon) return;
		if (e.getValueIsAdjusting())
			return;
		int x = board.getSelectedColumn();
		int y = board.getSelectedRow();
		if (x == -1 || y == -1 || !boardModel.isEmpty(x, y))
			return;
		if (currentPlayer != thisPlayer)
			return;
		
		// The next turn is by the other player.
	
		try {
			remotePlayer.setCell(x, y);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		
		isWon = (boardModel.setCell(x, y, playerMarks[currentPlayer]));
		if(isWon)
		{
			setStatusMessage("Player " + playerMarks[currentPlayer] + " won!");
		}
		else
		{
			currentPlayer = 1 - currentPlayer;
			setStatusMessage("Current player: " + playerMarks[currentPlayer]);;
		}
	}
}
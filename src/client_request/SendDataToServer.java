package client_request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Transferer.TransferManager;
import server_manager.ClientHandler;
import server_manager.LinKlipboard;
import server_manager.LinKlipboardGroup;
import server_manager.LinKlipboardServer;

@WebServlet("/SendDataToServer")
public class SendDataToServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SendDataToServer() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ������ ����
		String groupName = request.getParameter("groupName");
		//String tmp = request.getParameter("type");

		String ipAddr = request.getRemoteAddr();

		LinKlipboardGroup targetGroup = LinKlipboardServer.getGroup(groupName); // �׷�
																				// ��ü
																				// ������
		if(targetGroup == null) {
			System.out.println("���̿���");
		}
		System.out.println("test:" + ipAddr);
		ClientHandler client = targetGroup.searchClient(ipAddr); // �׷쿡�� Ŭ���̾�Ʈ
																	// Ư��
		TransferManager receiver = new TransferManager(targetGroup, client);
		receiver.createReceiveThread(); // ���� ������ ����

		PrintWriter out = response.getWriter();
		sendRespond(receiver, out); // ���� ���
	}

	/** �������� ������ ���� �� ���� ���� ��� */
	public void sendRespond(TransferManager receiver, PrintWriter out) {
		//Timer timer = new Timer(5); // 5�� Ÿ�̸�
		
		while (!receiver.isConnected()) {
//			if (!timer.isAlive()) {
//				out.println(LinKlipboard.ERROR_SOCKET_CONNECTION); // ����: ���� ��� ����
//				return;
//			}
		}
		out.println(LinKlipboard.READY_TO_TRANSFER); // ������ ���� �غ� ��
	}

	/** Ŭ���̾�Ʈ�� ������ ���� ���� ����Ͽ� ���� �ð� ����Ѵ�. */
	class Timer extends Thread {
		private int time;

		public Timer(int time) {
			this.time = time;
			this.start();
		}

		@Override
		public void run() {
			for (int i = 0; i < time; i++) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return;
		}
	}
}
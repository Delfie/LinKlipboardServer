package client_request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server_manager.ClientHandler;
import server_manager.LinKlipboard;
import server_manager.LinKlipboardGroup;
import server_manager.LinKlipboardServer;

@WebServlet("/JoinGroup")
public class RequestJoinGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RequestJoinGroup() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Hrer is joinGroup servlet URL").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 정보 받음s
		String groupName = request.getParameter("groupName");
		String password = request.getParameter("password");

		String respondMsg = null;
		PrintWriter out = response.getWriter();

		// 1. 그룹 찾기
		if (!LinKlipboardServer.isExistGroup(groupName)) {
			respondMsg = Integer.toString(LinKlipboard.ERROR_NO_MATCHED_GROUPNAME); // 오류: 그룹이름 매핑 불가
		}
		else {
			LinKlipboardGroup group = LinKlipboardServer.getGroup(groupName); // 그룹 객체 가져옴
			// 2. 인원 체크
			if (group.isFull()) {
				respondMsg = Integer.toString(LinKlipboard.ERROR_FULL_CLIENT); // 오류: 정원 초과
			}
			else {
				// 3. 패스워드 체크
				if (!group.isPasswordCorrected(password)) { // 그룹의 패스워드 일치 확인
					respondMsg = Integer.toString(LinKlipboard.ERROR_PASSWORD_INCORRECT); // 오류: 패스워드 불일치
				}
				else {
					ClientHandler newClient = new ClientHandler(request, groupName); // 클라이언트 생성
					LinKlipboardServer.waitClient(newClient); // 대기열에 클라이언트 추가

					respondMsg = LinKlipboard.ACCESS_PERMIT + LinKlipboard.SEPARATOR; // 허가 코드
					respondMsg += "nickname" + LinKlipboard.SEPARATOR + group.createDefaultNickname() + LinKlipboard.SEPARATOR; // 닉네임
					respondMsg += "portNum" + LinKlipboard.SEPARATOR + newClient.getRemotePort(); // 포트번호
				}
			}
		}
		out.println(respondMsg); // 응답
	}

}
package server_manager;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import surpport.Directory;
import surpport.Logger;

public class LinKlipboardServer {

	private static Hashtable<String, LinKlipboardGroup> groups = new Hashtable<String, LinKlipboardGroup>(); // 임시 그룹 모임 1
	private static Hashtable<String, ClientHandler> watingClients = new Hashtable<String, ClientHandler>();
	public static Logger logger = new Logger();

	public LinKlipboardServer() {
		System.out.println(" - Boot Server");
		Directory.createEmptyDirectory(LinKlipboard.FILE_DIR);
		System.out.println(" - Create directory to store files ");
	}

	/** 새로운 그룹을 생성 
	 * @param newGroup 새로 생성할 그룹 */
	public synchronized static void createGroup(LinKlipboardGroup newGroup) {
		if (!isFull()) {
			groups.put(newGroup.getName(), newGroup);
		}
	}

	/** @return 그룹 수가 MAX_GROUP 이하이면 true, 아니면 false */
	public static boolean isFull() {
		if (groups.size() < LinKlipboard.MAX_GROUP) { // 최대 그룹 수 체크
			return false;
		}
		return true;
	}

	/** 기존 그룹에 클라이언트 입장 
	 * @param groupName 클라이언트가 입장을 희망하는 그룹의 이름 
	 * @param 클라이언트의 핸들러 */
	public synchronized static void joinGroup(String groupName, ClientHandler newClient) {
		/* 중복처리 및 보안 처리는 서블릿에서 */
		groups.get(groupName).joinGroup(newClient);
	}

	/** 신설 하려는 그룹의 이름이 기존의 것과 중복되는지 확인 
	 * @param groupName 새로 신설하려 하는 그룹의 이름 
	 * @return 중복된 이름이 있으면 true 아니면 false */
	public static boolean isExistGroup(String groupName) {
		return groups.containsKey(groupName);
	}

	/** @param groupName 반환 받을 그룹이름 
	 * @return groupName과 일치하는 LinKlipboardGroup 객체 반환*/
	public static LinKlipboardGroup getGroup(String groupName) {
		return groups.get(groupName);
	}

	/** @return 서버에 존재하는 그룹의 수 */
	public static int getGroupCnt() {
		return groups.size();
	}

	/** @param 서버에 접속한 클라이언트의 ip 주소 
	 * @return 서버 내에 접속한 클라이언트와 중복된 ip 주소가 존재하면 true 아니면 false */
	public static boolean checkDuplicatedIpAddr(String ip) {

		if (groups.isEmpty())
			return false;

		Set<String> keys = groups.keySet();
		Iterator<String> groupName = keys.iterator();
		while (groupName.hasNext()) {
			String name = groupName.next();
			if (groups.get(name).isDuplicatedIpAddr(ip)) {
				return true;
			}
		}
		return false;
	}

	/**@param*/
	public static LinKlipboardGroup removeGroup(String group) {
		if (!group.isEmpty()) {
			return groups.remove(group);
		}
		return null;
	}
	
	public static void waitClient(ClientHandler client) {
		watingClients.put(client.getRemoteAddr(), client);
	}
	
	public static ClientHandler enterClient(String ip) {
		return watingClients.remove(ip);
	}
	
}

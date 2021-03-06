package datamanage;

import java.io.Serializable;
import java.util.Vector;

import contents.Contents;
import server_manager.LinKlipboardGroup;

public class ClientInitData implements Serializable {
	Vector<Contents> history;
	Vector<String> clients;

	public ClientInitData(LinKlipboardGroup group) {

		Vector<Contents> initContents = group.getHistory();
		synchronized (initContents) {
			this.history = new Vector<Contents>(initContents);
		}

		Vector<String> initClients = group.getClients();
		synchronized (initClients) {
			this.clients = new Vector<String>(initClients);
		}
	}

	public Vector<Contents> getHistory() {
		return this.history;
	}

	public Vector<String> getClients() {
		return this.clients;
	}
}
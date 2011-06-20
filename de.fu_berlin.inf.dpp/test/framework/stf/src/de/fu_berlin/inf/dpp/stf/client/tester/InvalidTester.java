package de.fu_berlin.inf.dpp.stf.client.tester;

import java.rmi.RemoteException;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.stf.server.rmi.remotebot.IRemoteWorkbenchBot;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.ISuperBot;

/**
 * DummyTester is responsible to check if a tester is already initialized.
 */
class InvalidTester implements AbstractTester {

    RuntimeException exception;
    String password;
    JID jid;

    public InvalidTester(JID jid, String password, RuntimeException exception) {
        this.jid = jid;
        this.password = password;
        this.exception = exception;
    }

    /**
     * @Return the name segment of {@link JID}.
     */

    public String getName() {
        return jid.getName();
    }

    /**
     * @Return the JID without resource qualifier.
     */

    public String getBaseJid() {
        return jid.getBase();
    }

    /**
     * @Return the resource qualified {@link JID}.
     */

    public String getRqJid() {
        return jid.toString();
    }

    public String getDomain() {
        return jid.getDomain();
    }

    public IRemoteWorkbenchBot remoteBot() {
        throw exception;
    }

    public ISuperBot superBot() throws RemoteException {
        throw exception;
    }

    public JID getJID() {
        return jid;
    }

    public String getPassword() {
        return password;
    }
}
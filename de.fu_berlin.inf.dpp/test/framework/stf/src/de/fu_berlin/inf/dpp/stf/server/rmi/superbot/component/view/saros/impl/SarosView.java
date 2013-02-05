package de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.view.saros.impl;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withRegex;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Roster;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.stf.server.StfRemoteObject;
import de.fu_berlin.inf.dpp.stf.server.bot.SarosSWTBotPreferences;
import de.fu_berlin.inf.dpp.stf.server.rmi.controlbot.impl.ControlBotImpl;
import de.fu_berlin.inf.dpp.stf.server.rmi.remotebot.impl.RemoteWorkbenchBot;
import de.fu_berlin.inf.dpp.stf.server.rmi.remotebot.widget.IRemoteBotView;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.ISuperBot;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.IContextMenusInBuddiesArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.IContextMenusInSessionArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.impl.ContextMenusInBuddiesArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.impl.ContextMenusInSessionArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.view.saros.IChatroom;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.view.saros.ISarosView;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.impl.SuperBot;
import de.fu_berlin.inf.dpp.stf.server.util.WidgetUtil;

/**
 * This implementation of {@link ISarosView}
 * 
 * @author lchen
 */
public final class SarosView extends StfRemoteObject implements ISarosView {

    private static final Logger log = Logger.getLogger(SarosView.class);

    private static final SarosView INSTANCE = new SarosView();

    private SWTBotView view;
    private SWTBotTree tree;

    public static SarosView getInstance() {
        return INSTANCE;
    }

    public ISarosView setView(SWTBotView view) {
        setViewWithTree(view);
        return this;
    }

    @Override
    public void connectWith(JID jid, String password) throws RemoteException {

        ControlBotImpl.getInstance().getAccountManipulator()
            .addAccount(jid.getName(), password, jid.getDomain());

        ControlBotImpl.getInstance().getAccountManipulator()
            .activateAccount(jid.getName(), jid.getDomain());

        if (isConnected()) {
            clickToolbarButtonWithTooltip(TB_DISCONNECT);
            waitUntilIsDisconnected();
        }

        clickToolbarButtonWithTooltip(TB_CONNECT);
        waitUntilIsConnected();
    }

    @Override
    public void connectWithActiveAccount() throws RemoteException {
        if (isDisconnected()) {
            if (getXmppAccountStore().isEmpty())
                throw new RuntimeException(
                    "unable to connect with the active account, it does not exists");

            clickToolbarButtonWithTooltip(TB_CONNECT);
            waitUntilIsConnected();
        }
    }

    @Override
    public void disconnect() throws RemoteException {
        if (isConnected()) {
            clickToolbarButtonWithTooltip(TB_DISCONNECT);
            waitUntilIsDisconnected();
        }
    }

    /*
     * FIXME: there are some problems by clicking the toolbarDropDownButton.
     */
    @SuppressWarnings("unused")
    private void selectConnectAccount(String baseJID) throws RemoteException {
        SWTBotToolbarDropDownButton b = view.toolbarDropDownButton(TB_CONNECT);
        @SuppressWarnings("static-access")
        Matcher<MenuItem> withRegex = WidgetMatcherFactory.withRegex(Pattern
            .quote(baseJID) + ".*");
        b.menuItem(withRegex).click();
        try {
            b.pressShortcut(KeyStroke.getInstance("ESC"));
        } catch (ParseException e) {
            log.debug("", e);
        }
    }

    @Override
    public void addNewBuddy(JID jid) throws RemoteException {
        if (!hasBuddy(jid)) {
            clickToolbarButtonWithTooltip(TB_ADD_A_NEW_BUDDY);
            SuperBot.getInstance().confirmShellAddBuddy(jid);
        }
        // wait for update of the saros session tree
        new SWTBot().sleep(500);
    }

    @Override
    public void shareYourScreenWithSelectedBuddy(JID jidOfPeer)
        throws RemoteException {
        selectParticipant(jidOfPeer, "you cannot share a screen with youself");
        clickToolbarButtonWithTooltip(TB_SHARE_SCREEN_WITH_BUDDY);
    }

    public void stopSessionWithBuddy(JID jidOfPeer) throws RemoteException {
        selectParticipant(jidOfPeer,
            "you cannot stop a screen session with youself");
        clickToolbarButtonWithTooltip(TB_STOP_SESSION_WITH_BUDDY);
    }

    @Override
    public void sendFileToSelectedBuddy(JID jidOfPeer) throws RemoteException {
        selectParticipant(jidOfPeer, "you cannot send a file to youself");
        clickToolbarButtonWithTooltip(TB_SEND_A_FILE_TO_SELECTED_BUDDY);
    }

    @Override
    public void startAVoIPSessionWithSelectedBuddy(JID jidOfPeer)
        throws RemoteException {
        selectParticipant(jidOfPeer,
            "you cannot start a VoIP session with youself");
        clickToolbarButtonWithTooltip(TB_START_VOIP_SESSION);
        if (RemoteWorkbenchBot.getInstance().shell(SHELL_ERROR_IN_SAROS_PLUGIN)
            .isActive()) {
            RemoteWorkbenchBot.getInstance().shell(SHELL_ERROR_IN_SAROS_PLUGIN)
                .confirm(OK);
        }
    }

    @Override
    public void leaveSession() throws RemoteException {
        if (isInSession()) {
            if (!isHost()) {
                clickToolbarButtonWithTooltip(TB_LEAVE_SESSION);
                SWTBotShell shell = new SWTBot()
                    .shell(SHELL_CONFIRM_LEAVING_SESSION);
                shell.activate();
                shell.bot().button(YES).click();
            } else {
                boolean isLastInSession = tree.getTreeItem(NODE_SESSION)
                    .getNodes().size() == 1;
                clickToolbarButtonWithTooltip(TB_STOP_SESSION);
                if (!isLastInSession) {
                    SWTBotShell shell = new SWTBot()
                        .shell(SHELL_CONFIRM_CLOSING_SESSION);
                    shell.activate();
                    shell.bot().button(YES).click();
                }
            }
            waitUntilIsNotInSession();
        }
    }

    /**
     * Note: {@link StfRemoteObject#TB_INCONSISTENCY_DETECTED} is not complete
     * toolbarName, so we need to use
     * {@link IRemoteBotView#toolbarButtonWithRegex(String)} to perform this
     * action.
     */
    @Override
    public void resolveInconsistency() throws RemoteException {
        WidgetUtil.getToolbarButtonWithRegex(view,
            Pattern.quote(TB_INCONSISTENCY_DETECTED) + ".*").click();

        SWTBot bot = new SWTBot();
        bot.sleep(1000);
        bot.waitWhile(Conditions.shellIsActive(SHELL_PROGRESS_INFORMATION),
            SarosSWTBotPreferences.SAROS_LONG_TIMEOUT);
    }

    /**********************************************
     * 
     * Content of Saros View
     * 
     **********************************************/

    @Override
    public IContextMenusInBuddiesArea selectBuddies() throws RemoteException {
        initBuddiesContextMenuWrapper(Pattern.quote((NODE_BUDDIES)));
        return ContextMenusInBuddiesArea.getInstance();
    }

    @Override
    public IContextMenusInBuddiesArea selectBuddy(JID buddyJID)
        throws RemoteException {
        if (getNickname(buddyJID) == null) {
            throw new RuntimeException("no buddy exists with the JID: "
                + buddyJID.getBase());
        }
        initBuddiesContextMenuWrapper(Pattern.quote(NODE_BUDDIES),
            Pattern.quote(getNickname(buddyJID)) + ".*");
        return ContextMenusInBuddiesArea.getInstance();
    }

    @Override
    public IContextMenusInSessionArea selectSession() throws RemoteException {
        if (!isInSession())
            throw new RuntimeException("you are not in a session");
        initSessionContextMenuWrapper(Pattern.quote((NODE_SESSION)));
        return ContextMenusInSessionArea.getInstance();
    }

    @Override
    public IContextMenusInSessionArea selectNoSessionRunning()
        throws RemoteException {
        if (isInSession())
            throw new RuntimeException("you are in a session");
        initSessionContextMenuWrapper(Pattern.quote((NODE_NO_SESSION_RUNNING)));
        return ContextMenusInSessionArea.getInstance();
    }

    @Override
    public IContextMenusInSessionArea selectParticipant(final JID participantJID)
        throws RemoteException {
        if (!isInSession())
            throw new IllegalStateException("you are not in a session");
        String participantLabel = getParticipantLabel(participantJID);
        initSessionContextMenuWrapper(Pattern.quote(NODE_SESSION), ".*"
            + Pattern.quote(participantLabel) + ".*");
        ContextMenusInSessionArea.getInstance().setParticipantJID(
            participantJID);
        return ContextMenusInSessionArea.getInstance();
    }

    /**********************************************
     * 
     * state
     * 
     **********************************************/

    @Override
    public boolean isConnected() {
        return isToolbarButtonEnabled(TB_DISCONNECT);
    }

    public boolean isDisconnected() {
        return isToolbarButtonEnabled(TB_CONNECT);
    }

    @Override
    public String getNickname(JID buddyJID) throws RemoteException {
        Roster roster = getSarosNet().getRoster();

        if (roster == null)
            throw new IllegalStateException("not connected to a xmpp server");

        if (roster.getEntry(buddyJID.getBase()) == null)
            return null;
        if (roster.getEntry(buddyJID.getBase()).getName() == null)
            return buddyJID.getBase();
        else
            return roster.getEntry(buddyJID.getBase()).getName();
    }

    @Override
    public boolean hasNickName(JID buddyJID) throws RemoteException {
        try {
            if (getNickname(buddyJID) == null)
                return false;
            if (!getNickname(buddyJID).equals(buddyJID.getBase()))
                return true;
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<String> getAllBuddies() throws RemoteException {
        SWTBotTreeItem items = tree.getTreeItem(NODE_BUDDIES);
        List<String> buddies = new ArrayList<String>();

        for (SWTBotTreeItem item : items.getItems())
            buddies.add(item.getText());

        return buddies;
    }

    @Override
    public boolean hasBuddy(JID buddyJID) throws RemoteException {
        try {
            String nickName = getNickname(buddyJID);
            if (nickName == null)
                return false;

            nickName = Pattern.quote(nickName) + ".*";
            for (String label : tree.getTreeItem(NODE_BUDDIES).getNodes())
                if (label.matches(nickName))
                    return true;

            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsParticipant(JID participantJID) throws RemoteException {
        try {
            String participantLabel = getParticipantLabel(participantJID);

            participantLabel = ".*" + Pattern.quote(participantLabel) + ".*";
            for (String label : tree.getTreeItem(NODE_SESSION).getNodes())
                if (label.matches(participantLabel))
                    return true;

            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getParticipantLabel(JID participantJID)
        throws RemoteException {
        String contactLabel;

        if (SuperBot.getInstance().views().sarosView()
            .hasNickName(participantJID)) {

            contactLabel = SuperBot.getInstance().views().sarosView()
                .getNickname(participantJID);
        } else {
            contactLabel = participantJID.getName();
        }
        return contactLabel;
    }

    @Override
    public boolean isInSession() {
        try {
            for (SWTBotToolbarButton button : view.getToolbarButtons()) {
                if ((button.getToolTipText().equals(TB_STOP_SESSION) || button
                    .getToolTipText().equals(TB_LEAVE_SESSION))
                    && button.isEnabled())
                    return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isHost() throws RemoteException {
        try {
            if (!isInSession())
                return false;

            List<String> participants = tree.getTreeItem(NODE_SESSION)
                .getNodes();

            if (participants.size() == 0)
                return false;

            for (String participant : participants) {
                if (participant.contains(HOST_INDICATION)) {
                    if (participant.contains(getParticipantLabel(SuperBot
                        .getInstance().getJID()))) {
                        return true;
                    }
                }
            }
            return false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isFollowing() throws RemoteException {
        try {
            JID followedBuddy = getFollowedBuddy();
            if (followedBuddy == null)
                return false;

            return selectParticipant(followedBuddy).isFollowing();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<String> getAllParticipants() throws RemoteException {
        return tree.getTreeItem(NODE_SESSION).getNodes();

    }

    @Override
    public JID getJID() {
        return SuperBot.getInstance().getJID();
    }

    @Override
    public JID getFollowedBuddy() {
        if (getEditorManager().getFollowedUser() != null)
            return getEditorManager().getFollowedUser().getJID();
        else
            return null;
    }

    @Override
    public IChatroom selectChatroom(String name) throws RemoteException {
        return selectChatroomWithRegex(Pattern.quote(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public IChatroom selectChatroomWithRegex(String regex)
        throws RemoteException {
        Chatroom.getInstance().setChatTab(
            new SWTBotCTabItem((CTabItem) view.bot().widget(
                allOf(widgetOfType(CTabItem.class), withRegex(regex)),
                view.getWidget())));

        return Chatroom.getInstance();
    }

    @Override
    public void closeChatroom(String name) throws RemoteException {
        closeChatroomWithRegex(Pattern.quote(name));

    }

    @SuppressWarnings("unchecked")
    @Override
    public void closeChatroomWithRegex(String regex) throws RemoteException {
        List<? extends Widget> chatTabs = view.bot().widgets(
            allOf(widgetOfType(CTabItem.class), withRegex(regex)),
            view.getWidget());

        for (Widget chatTab : chatTabs)
            new SWTBotCTabItem((CTabItem) chatTab).close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasOpenChatrooms() throws RemoteException {
        return !view
            .bot()
            .getFinder()
            .findControls(view.getWidget(),
                allOf(widgetOfType(CTabItem.class), withRegex(".*")), true)
            .isEmpty();
    }

    /*
     * waits until
     */

    @Override
    public void waitUntilIsConnected() throws RemoteException {
        new SWTBot().waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return isConnected();
            }

            @Override
            public String getFailureMessage() {
                return "unable to connect to server";
            }
        }, SarosSWTBotPreferences.SAROS_DEFAULT_TIMEOUT);
    }

    @Override
    public void waitUntilIsDisconnected() throws RemoteException {
        new SWTBot().waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return isDisconnected();
            }

            @Override
            public String getFailureMessage() {
                return "unable to disconnect from server";
            }
        }, SarosSWTBotPreferences.SAROS_DEFAULT_TIMEOUT);
    }

    @Override
    public void waitUntilIsInSession() throws RemoteException {
        new SWTBot().waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return isInSession();
            }

            @Override
            public String getFailureMessage() {
                return "joining the session failed";
            }
        });
    }

    @Override
    public void waitUntilIsInviteeInSession(ISuperBot sarosBot)
        throws RemoteException {
        sarosBot.views().sarosView().waitUntilIsInSession();
    }

    @Override
    public void waitUntilIsNotInSession() throws RemoteException {
        new SWTBot().waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return !isInSession();
            }

            @Override
            public String getFailureMessage() {
                return "leaving the session failed";
            }
        });
    }

    @Override
    public void waitUntilIsInviteeNotInSession(ISuperBot sarosBot)
        throws RemoteException {
        sarosBot.views().sarosView().waitUntilIsNotInSession();
    }

    @Override
    public void waitUntilAllPeersLeaveSession(
        final List<JID> jidsOfAllParticipants) throws RemoteException {

        /*
         * see STFController which manipulates the behavior of how the
         * HostAloneInSession dialog is displayed
         */

        // new SWTBot().waitUntil(new DefaultCondition() {
        // @Override
        // public boolean test() throws Exception {
        // for (JID jid : jidsOfAllParticipants) {
        // if (existsParticipant(jid))
        // return false;
        // }
        // return true;
        // }
        //
        // @Override
        // public String getFailureMessage() {
        // return "there are still users in the session";
        // }
        // });

        waitUntilIsNotInSession();
    }

    @Override
    public void waitUntilIsInconsistencyDetected() throws RemoteException {
        RemoteWorkbenchBot.getInstance().waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return WidgetUtil.getToolbarButtonWithRegex(view,
                    Pattern.quote(TB_INCONSISTENCY_DETECTED) + ".*")
                    .isEnabled();
            }

            @Override
            public String getFailureMessage() {
                return "the toolbar button " + TB_INCONSISTENCY_DETECTED
                    + " is not enabled";
            }
        });
    }

    /**************************************************************
     * 
     * inner functions
     * 
     **************************************************************/

    private boolean isToolbarButtonEnabled(String tooltip) {

        try {
            for (SWTBotToolbarButton button : view.getToolbarButtons())
                if (button.getToolTipText().equals(tooltip)
                    && button.isEnabled())
                    return true;

            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private void clickToolbarButtonWithTooltip(String tooltipText) {
        WidgetUtil.getToolbarButtonWithRegex(view,
            Pattern.quote(tooltipText) + ".*").click();
    }

    private void selectParticipant(JID jidOfSelectedUser, String message)
        throws RemoteException {
        if (SuperBot.getInstance().getJID().equals(jidOfSelectedUser)) {
            throw new RuntimeException(message);
        }
        selectParticipant(jidOfSelectedUser);
    }

    private void setViewWithTree(SWTBotView view) {
        this.view = view;
        this.tree = view.bot().tree();
    }

    private void initSessionContextMenuWrapper(String... treeItemNodes) {
        ContextMenusInSessionArea.getInstance().setTree(tree);
        ContextMenusInSessionArea.getInstance().setTreeItemNodes(treeItemNodes);
        ContextMenusInSessionArea.getInstance().setSarosView(this);
    }

    private void initBuddiesContextMenuWrapper(String... treeItemNodes) {
        ContextMenusInBuddiesArea.getInstance().setTree(tree);
        ContextMenusInBuddiesArea.getInstance().setTreeItemNodes(treeItemNodes);
        ContextMenusInBuddiesArea.getInstance().setSarosView(this);
    }
}

package net.bither.viewsystem.froms;

import net.bither.bitherj.core.Peer;
import net.bither.bitherj.core.PeerManager;
import net.bither.bitherj.utils.Utils;
import net.bither.fonts.AwesomeIcon;
import net.bither.implbitherj.PeerNotificationCenter;
import net.bither.languages.MessageKey;
import net.bither.model.PeerTableModel;
import net.bither.utils.PeerUtil;
import net.bither.viewsystem.base.Panels;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PeersPanel extends WizardPanel implements PeerNotificationCenter.IPeerListener {
    private List<Peer> peerList = new ArrayList<Peer>();
    private PeerTableModel peerTableModel;

    public PeersPanel() {
        super(MessageKey.PEERS, AwesomeIcon.FA_USERS, true);
        if (!PeerManager.instance().isConnected()) {
            PeerUtil.startPeer();
        }
    }

    @Override
    public void initialiseContent(JPanel panel) {
        panel.setLayout(new MigLayout(
                Panels.migXYLayout(),
                "10[]10", // Column constraints
                "10[]10" // Row constraints
        ));

        peerTableModel = new PeerTableModel(peerList);
        JTable table = new JTable(peerTableModel);

        panel.add(table, "push,align center,grow");
        refreshPeer();

    }

    private void refreshPeer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (peerList) {
                    List<Peer> temp = PeerManager.instance().getConnectedPeers();
                    if (temp != null && temp.size() > 0) {
                        peerList.addAll(temp);
                        Collections.sort(peerList, new Comparator<Peer>() {
                            @Override
                            public int compare(Peer lhs, Peer rhs) {
                                if (lhs.getClientVersion() == 0 && rhs.getClientVersion() > 0) {
                                    return 1;
                                }
                                if (rhs.getClientVersion() == 0 && lhs.getClientVersion() > 0) {
                                    return -1;
                                }
                                return -1 * Long.valueOf(Utils.parseLongFromAddress(
                                        lhs.getPeerAddress())).compareTo(
                                        Long.valueOf(Utils.parseLongFromAddress(rhs.getPeerAddress())));

                            }
                        });
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                peerTableModel.fireTableDataChanged();
                            }
                        });

                    }
                }

            }
        }).start();
    }

    @Override
    public void sendConnectedChangeBroadcast(String connectedChangeBroadcast, boolean isConnected) {
        refreshPeer();
    }

    @Override
    public void sendBroadcastProgressState(double value) {

    }

    @Override
    public void sendBroadcastPeerState(int numPeers) {
        refreshPeer();

    }

    @Override
    public void removeProgressState() {

    }
}

package net.bither.viewsystem.froms;

import net.bither.BitherUI;
import net.bither.bitherj.core.Address;
import net.bither.bitherj.core.AddressManager;
import net.bither.bitherj.core.BitherjSettings;
import net.bither.bitherj.core.Tx;
import net.bither.bitherj.utils.UnitUtil;
import net.bither.bitherj.utils.Utils;
import net.bither.factory.MonitorAddress;
import net.bither.fonts.AwesomeDecorator;
import net.bither.fonts.AwesomeIcon;
import net.bither.implbitherj.TxNotificationCenter;
import net.bither.languages.Languages;
import net.bither.model.Ticker;
import net.bither.preference.UserPreference;
import net.bither.utils.BitherTimer;
import net.bither.utils.LocaliserUtils;
import net.bither.utils.MarketUtil;
import net.bither.utils.WalletUtils;
import net.bither.viewsystem.base.Buttons;
import net.bither.viewsystem.base.Labels;
import net.bither.viewsystem.base.Panels;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuBar implements TxNotificationCenter.ITxListener {
    private JButton btnCreateAddress;
    private JButton btnWatchOnly;
    private JPanel panelButton;
    private JButton btnChangePassword;
    private JButton btnExportKey;
    private JButton btnImport;
    private JButton btnCheck;
    private JButton btnAbout;

    private JButton btnMore;
    private JLabel labBitcoinUnit;
    private JLabel labBitcoinAmt;
    private JLabel labelDevier;
    private JLabel labMoney;
    private JLabel labMarket;

    private JPanel panelInfo;
    private JPanel panelMain;


    public MenuBar() {
        TxNotificationCenter.addTxListener(MenuBar.this);
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        panelMain.setOpaque(false);
        panelButton = getPanelButton();
        panelMain.add(panelButton, BorderLayout.EAST);

        if (UserPreference.getInstance().getAppMode() == BitherjSettings.AppMode.HOT) {
            panelInfo = getHotPanelInfo();
            panelMain.add(panelInfo, BorderLayout.WEST);
            updateTickerInfo();
            BitherTimer bitherTimer = new BitherTimer(MenuBar.this);
            bitherTimer.startTimer();
        } else {
            panelInfo=getPanelColdInfo();
            panelMain.add(panelInfo, BorderLayout.WEST);
        }

    }

    private JPanel getPanelButton() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setHgap(5);
        JPanel jPanel = new JPanel(flowLayout);

        // Force transparency
        jPanel.setOpaque(false);

        // Ensure LTR and RTL is detected by the layout
        jPanel.applyComponentOrientation(Languages.currentComponentOrientation());


        btnCreateAddress = Buttons.newAddButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddAddressPanel addAddressPanel = new AddAddressPanel();
                addAddressPanel.showPanel();

            }
        });
        jPanel.add(btnCreateAddress);


        if (UserPreference.getInstance().getAppMode() == BitherjSettings.AppMode.HOT) {
            btnWatchOnly = Buttons.newEyeButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MonitorAddress monitorAddress = new MonitorAddress();
                    monitorAddress.monitorAddress();

                }
            });
            jPanel.add(btnWatchOnly);
            if (WalletUtils.isWatchOnlyLimit()) {
                btnWatchOnly.setEnabled(false);
            }

        }

        btnChangePassword = Buttons.newShowChangePasswordButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ChangePasswordPanel wizardForm = new ChangePasswordPanel();
                //  wizardForm.setOkAction(changePasswordForm.getOKAction());
                wizardForm.showPanel();
            }
        });


        jPanel.add(btnChangePassword);
        btnExportKey = Buttons.newExportButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExportPrivateKeyPanel privateKeyPanel = new ExportPrivateKeyPanel();
                privateKeyPanel.showPanel();

            }
        });
        jPanel.add(btnExportKey);

        btnImport = Buttons.newImportButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImportPrivateKeyPanel privateKeyPanel = new ImportPrivateKeyPanel();

                privateKeyPanel.showPanel();
            }
        });

        jPanel.add(btnImport);
        btnCheck = Buttons.newCheckPrivateKeyButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CheckPrivateKeyPanel checkPrivateKeyPanel = new CheckPrivateKeyPanel();
                checkPrivateKeyPanel.showPanel();
            }
        });
        jPanel.add(btnCheck);

        if (UserPreference.getInstance().getAppMode() == BitherjSettings.AppMode.HOT) {
            btnMore = Buttons.newMoreButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MorePanel morePanel = new MorePanel();
                    morePanel.showPanel();

                }
            });
            jPanel.add(btnMore);
        }
        btnAbout = Buttons.newAboutButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutPanel aboutPanel = new AboutPanel();
                aboutPanel.showPanel();
            }
        });
        jPanel.add(btnAbout);
        return jPanel;

    }

    private JPanel getPanelColdInfo() {
        JPanel panel = Panels.newPanel(new MigLayout(
                Panels.migXYLayout(),
                "10[]", // Column constraints
                "[]" // Row constraints
        ));
        JLabel label= Labels.newValueLabel(LocaliserUtils.getString("cold.wallet"));
        label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 22));
        panel.add(label);


        return panel;
    }

    private JPanel getHotPanelInfo() {

        labBitcoinUnit = new JLabel();
        labBitcoinAmt = new JLabel();
        labelDevier = new JLabel(" ~ ");
        labMoney = new JLabel();
        labMarket = new JLabel();
        Font font = labBitcoinUnit.getFont().deriveFont(Font.BOLD, (float) BitherUI.NORMAL_PLUS_ICON_SIZE);
        labBitcoinUnit.setFont(font);
        font = labBitcoinAmt.getFont().deriveFont(Font.PLAIN, (float) BitherUI.NORMAL_ICON_SIZE);
        labBitcoinAmt.setFont(font);
        labelDevier.setFont(font);
        labMoney.setFont(font);
        labMarket.setFont(font);

        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel panelHotInfo = new JPanel(gridBagLayout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 10, 0, 0);


        panelHotInfo.add(labBitcoinUnit, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 5, 0, 0);


        panelHotInfo.add(labBitcoinAmt, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 5, 0, 0);

        panelHotInfo.add(labelDevier, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 5, 0, 0);

        panelHotInfo.add(labMoney, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 5, 0, 0);

        panelHotInfo.add(labMarket, constraints);
        panelHotInfo.setOpaque(false);


        panelHotInfo.addComponentListener(new ComponentAdapter() {
        });
        panelHotInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                // Bither.getCoreController().displayView(ViewEnum.PREFERENCES_VIEW);
                ExchangePreferencePanel exchangePreferencePanel = new ExchangePreferencePanel();
                exchangePreferencePanel.showPanel();


            }
        });
        return panelHotInfo;


    }


    private void updateTickerInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                long finalEstimatedBalance = 0;
                for (Address address : AddressManager.getInstance().getAllAddresses()) {
                    finalEstimatedBalance = finalEstimatedBalance + address.getBalance();
                }
                final long total = finalEstimatedBalance;
                final String exchange = MarketUtil.getMarketName(UserPreference.getInstance().getDefaultMarket());
                final String currency = UserPreference.getInstance().getDefaultCurrency().getName();
                final Ticker ticker = MarketUtil.getTickerOfDefaultMarket();


                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateHeaderOnSwingThread("", total, currency, ticker, exchange);
                    }
                });

            }


        }).start();


    }

    private void updateHeaderOnSwingThread(String bitcoin, long amt, String unit, Ticker ticker, String exchangeName) {

        AwesomeDecorator.applyIcon(AwesomeIcon.BITCOIN, labBitcoinUnit, false, labBitcoinUnit.getFont().getSize());
        labBitcoinUnit.setIconTextGap(-2);
        labBitcoinAmt.setText(UnitUtil.formatValue(amt, UnitUtil.BitcoinUnit.BTC));
        double money = 0;
        if (ticker != null) {
            money = ticker.getDefaultExchangePrice() * amt / Math.pow(10, 8);
        }
        labMoney.setText(UserPreference.getInstance().getDefaultCurrency().getSymbol() + " " + Utils.formatDoubleToMoneyString(money));

        labMarket.setText("(" + exchangeName + ")");
    }


    public void updateTicker() {
        updateTickerInfo();


    }


    public JPanel getPanelMain() {
        return panelMain;
    }

    @Override
    public void notificatTx(Address address, Tx tx, Tx.TxNotificationType txNotificationType, long deltaBalance) {
        updateTickerInfo();
    }
}

package com.firebirdcss.tool.image_converter;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/08/2023
 *
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private Border btnBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    
    private JFrame thisWindow = this;
    private Container pane = this.getContentPane();
    private SpringLayout mainLayout = new SpringLayout();
    
    private JLabel lblDisp = new JLabel("Device Display:");
    private Screen disp = new Screen(128, 64);
    
    private JPanel dispCtrls = new JPanel();
    private JLabel lblWidth = new JLabel("Width:");
    private JSpinner width = new JSpinner(new SpinnerNumberModel(128, 5, 300, 1));
    private JLabel lblHeight = new JLabel("Height:");
    private JSpinner height = new JSpinner(new SpinnerNumberModel(64, 5, 300, 1));
    private JButton btnDispUpdate = new JButton("Update");
    private SpringLayout layDispCtrls = new SpringLayout();
    
    private JButton btnAddAsset = new JButton("Add Image Asset");
    
    private String headers[] = {"Asset ID", "Width", "Height", "XPos", "YPos"};
    private String data[][] = {{"Sample_ID", "16", "16", "0", "0"}};
    private JTable assetTable = new JTable(data, headers);
    private JTableHeader header = assetTable.getTableHeader();
    private JScrollPane assetsPane = new JScrollPane(assetTable);
    
    private JButton btnExportAll = new JButton("Export All");
    
    private JPanel pnlSelAsset = new JPanel();
    private JLabel lblAssetId = new JLabel("Asset ID:");
    private JTextField txtAssetId = new JTextField();
    private JLabel lblXPos = new JLabel("X-Pos:");
    private JTextField txtXPos = new JTextField();
    private JLabel lblYPos = new JLabel("Y-Pos:");
    private JTextField txtYPos = new JTextField();
    private JButton btnUpdatePos = new JButton("Update Position");
    private JLabel lblImageSize = new JLabel("Image Size:");
    private JTextField txtImageWidth = new JTextField();
    private JLabel lblBy = new JLabel(" x ");
    private JTextField txtImageHeight = new JTextField();
    private JButton btnImgResize = new JButton("Resize");
    private JSeparator sep = new JSeparator();
    private JButton btnImgExport = new JButton("Export");
    private JButton btnImgRemove = new JButton("Remove");
    private SpringLayout selAssetLayout = new SpringLayout();
    
    public MainWindow() {
        this.setTitle("Devices Image Converter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        doInitializeControls();
        doInitializeLayouts();
        doAddControlsToContainers();
        doAddActionListeners();
        
        thisWindow.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
        thisWindow.pack();
    }
    
    private void doAddActionListeners() {
        btnDispUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disp.setSize((Integer) width.getValue(), (Integer) height.getValue());
                disp.repaint();
                SwingUtilities.updateComponentTreeUI(thisWindow);
                thisWindow.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
                thisWindow.pack();
            }
        });
    }
    
    private void doAddControlsToContainers() {
        /* Display Size: */
        dispCtrls.add(lblWidth);
        dispCtrls.add(width);
        dispCtrls.add(lblHeight);
        dispCtrls.add(height);
        dispCtrls.add(btnDispUpdate);
        
        /* Selected Asset: */
        pnlSelAsset.add(lblAssetId);
        pnlSelAsset.add(txtAssetId);
        pnlSelAsset.add(lblXPos);
        pnlSelAsset.add(txtXPos);
        pnlSelAsset.add(lblYPos);
        pnlSelAsset.add(txtYPos);
        pnlSelAsset.add(btnUpdatePos);
        pnlSelAsset.add(lblImageSize);
        pnlSelAsset.add(txtImageWidth);
        pnlSelAsset.add(lblBy);
        pnlSelAsset.add(txtImageHeight);
        pnlSelAsset.add(btnImgResize);
        pnlSelAsset.add(sep);
        pnlSelAsset.add(btnImgExport);
        pnlSelAsset.add(btnImgRemove);
        
        /* Main Window */
        pane.add(lblDisp);
        pane.add(disp);
        pane.add(dispCtrls);
        pane.add(btnAddAsset);
        pane.add(assetsPane);
        pane.add(btnExportAll);
        pane.add(pnlSelAsset);
    }
    
    private void doInitializeLayouts() {
        /* Display Size: */
        layDispCtrls.putConstraint(SpringLayout.NORTH, lblWidth, 10, SpringLayout.NORTH, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.WEST, lblWidth, 6, SpringLayout.WEST, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.NORTH, width, 6, SpringLayout.NORTH, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.WEST, width, 3, SpringLayout.EAST, lblWidth);
        layDispCtrls.putConstraint(SpringLayout.NORTH, lblHeight, 10, SpringLayout.NORTH, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.WEST, lblHeight, 12, SpringLayout.EAST, width);
        layDispCtrls.putConstraint(SpringLayout.NORTH, height, 6, SpringLayout.NORTH, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.WEST, height, 3, SpringLayout.EAST, lblHeight);
        layDispCtrls.putConstraint(SpringLayout.NORTH, btnDispUpdate, 6, SpringLayout.NORTH, dispCtrls);
        layDispCtrls.putConstraint(SpringLayout.EAST, btnDispUpdate, -6, SpringLayout.EAST, dispCtrls);
        
        /* Image Assets: */
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblAssetId, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblAssetId, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtAssetId, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtAssetId, 2, SpringLayout.EAST, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblXPos, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblXPos, 12, SpringLayout.EAST, txtAssetId);
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtXPos, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtXPos, 2, SpringLayout.EAST, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblYPos, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblYPos, 6, SpringLayout.EAST, txtXPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtYPos, 6, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtYPos, 2, SpringLayout.EAST, lblYPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnUpdatePos, 1, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.EAST, btnUpdatePos, -6, SpringLayout.EAST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblImageSize, 20, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblImageSize, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtImageWidth, 20, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtImageWidth, 12, SpringLayout.EAST, lblImageSize);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblBy, 20, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblBy, 3, SpringLayout.EAST, txtImageWidth);
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtImageHeight, 20, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtImageHeight, 3, SpringLayout.EAST, lblBy);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgResize, 15, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgResize, 12, SpringLayout.EAST, txtImageHeight);
        selAssetLayout.putConstraint(SpringLayout.NORTH, sep, 6, SpringLayout.SOUTH, lblImageSize);
        selAssetLayout.putConstraint(SpringLayout.WEST, sep, 2, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.EAST, sep, -2, SpringLayout.EAST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgExport, 6, SpringLayout.SOUTH, sep);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgExport, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgRemove, 6, SpringLayout.SOUTH, sep);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgRemove, 6, SpringLayout.EAST, btnImgExport);
        
        /* Main Window */
        mainLayout.putConstraint(SpringLayout.NORTH, lblDisp, 6, SpringLayout.NORTH, pane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lblDisp, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, disp, 3, SpringLayout.SOUTH, lblDisp);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, disp, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, dispCtrls, 12, SpringLayout.SOUTH, disp);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dispCtrls, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, btnAddAsset, 12, SpringLayout.SOUTH, dispCtrls);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnAddAsset, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, assetsPane, 12, SpringLayout.SOUTH, btnAddAsset);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetsPane, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, btnExportAll, 12, SpringLayout.SOUTH, assetsPane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnExportAll, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, pnlSelAsset, 12, SpringLayout.SOUTH, btnExportAll);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pnlSelAsset, 0, SpringLayout.HORIZONTAL_CENTER, pane);
    }
    
    private void doInitializeControls() {
        /* Main Window */
        pane.setLayout(mainLayout);
        
        /* Display Size: */
        dispCtrls.setBorder(BorderFactory.createTitledBorder("Display Settings:"));
        dispCtrls.setPreferredSize(new Dimension(400, 70));
        dispCtrls.setLayout(layDispCtrls);
        
        /* BUTTON: Add Image Asset */
        btnAddAsset.setPreferredSize(new Dimension(400, 25));
        btnAddAsset.setBorder(btnBorder);
        
        /* Image Assets: */
        header.setForeground(Color.WHITE);
        assetsPane.setPreferredSize(new Dimension(700, 200));
        assetsPane.setBorder(BorderFactory.createTitledBorder("Image Assets:"));
        assetsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        assetsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        /* BUTTON: Export All */
        btnExportAll.setPreferredSize(new Dimension(700, 25));
        btnExportAll.setBorder(btnBorder);
        
        /* Selected Asset: */
        pnlSelAsset.setPreferredSize(new Dimension(700, 145));
        pnlSelAsset.setBorder(BorderFactory.createTitledBorder("Selected Asset:"));
        pnlSelAsset.setLayout(selAssetLayout);
        txtAssetId.setPreferredSize(new Dimension(200, 20));
        txtAssetId.setEditable(false);
        txtXPos.setPreferredSize(new Dimension(50,20));
        txtYPos.setPreferredSize(new Dimension(50,20));
        txtImageWidth.setPreferredSize(new Dimension(50, 20));
        txtImageWidth.setEditable(false);
        txtImageHeight.setPreferredSize(new Dimension(50, 20));
        txtImageHeight.setEditable(false);
        sep.setOrientation(SwingConstants.HORIZONTAL);
        sep.setBackground(Color.LIGHT_GRAY);
    }
    
    private int getContentHeight() {
        int result = 
            lblDisp.getPreferredSize().height +
            disp.getPreferredSize().height +
            dispCtrls.getPreferredSize().height +
            btnAddAsset.getPreferredSize().height + 
            assetsPane.getPreferredSize().height +
            btnExportAll.getPreferredSize().height + 
            pnlSelAsset.getPreferredSize().height 
        ;
        
        return result + 150;
    }
    
    private int getContentWidth() {
        int result = assetsPane.getPreferredSize().width;
        
        return result + 100;
    }
}

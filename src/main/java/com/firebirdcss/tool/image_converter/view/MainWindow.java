package com.firebirdcss.tool.image_converter.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;

import com.firebirdcss.tool.image_converter.AssetManager;
import com.firebirdcss.tool.image_converter.ImageAsset;
import com.firebirdcss.tool.image_converter.utils.Utils;
import com.firebirdcss.tool.image_converter.view.components.Screen;

/**
 * This class is the code for the Main Window of the application.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/08/2023
 *
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private AssetManager assetManager = new AssetManager();
    
    private Border btnBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    
    private JFrame thisWindow = this;
    private Container pane = this.getContentPane();
    private SpringLayout mainLayout = new SpringLayout();
    
    /* Device Display: */
    private JLabel lblDisp = new JLabel("Device Display:");
    private Screen disp = new Screen(128, 64);
    
    /* JPANEL: Display Settings: */
    private JPanel dispCtrls = new JPanel();
    private JLabel lblWidth = new JLabel("Width:");
    private JSpinner width = new JSpinner(new SpinnerNumberModel(128, 5, 300, 1));
    private JLabel lblHeight = new JLabel("Height:");
    private JSpinner height = new JSpinner(new SpinnerNumberModel(64, 5, 300, 1));
    private JButton btnDispUpdate = new JButton("Update");
    private SpringLayout layDispCtrls = new SpringLayout();
    
    /* BUTTTON: Add Image Asset */
    private JButton btnAddAsset = new JButton("Add Image Asset");
    
    /* JSCROLLPANE: Image Assets: */
    private DefaultTableModel assetTableModel = new DefaultTableModel(assetManager.getData(), assetManager.getHeaders());
    private JTable assetTable = new JTable(assetTableModel);
    private JTableHeader header = assetTable.getTableHeader();
    private JScrollPane assetsPane = new JScrollPane(assetTable);
    
    /* BUTTON: Export All */
    private JButton btnExportAll = new JButton("Export All");
    
    /* JPANEL: Selected Asset: */
    private JPanel pnlSelAsset = new JPanel();
    private JLabel lblAssetId = new JLabel("Asset ID:");
    private JTextField txtAssetId = new JTextField();
    private JLabel lblXPos = new JLabel("X-Pos:");
    private JSpinner spnXPos = new JSpinner();
    private JLabel lblYPos = new JLabel("Y-Pos:");
    private JSpinner spnYPos = new JSpinner();
    private JButton btnUpdatePos = new JButton("Update Position");
    private JLabel lblImageSize = new JLabel("Image Size:");
    private JSpinner spnImageWidth = new JSpinner();
    private JLabel lblBy = new JLabel(" x ");
    private JSpinner spnImageHeight = new JSpinner();
    private JButton btnImgResize = new JButton("Resize");
    private JLabel lblResizeNote = new JLabel("Note: To keep aspect-ratio set one dimension as desired and the other to '-1'.");
    private JSeparator sep = new JSeparator();
    private JButton btnImgExport = new JButton("Export");
    private JButton btnImgRemove = new JButton("Remove");
    private SpringLayout selAssetLayout = new SpringLayout();
    
    
    
    /**
     * CONSTRUCTOR: The class constructor. This is where this object gets
     * initialized. 
     *
     */
    public MainWindow() {
        this.setTitle("Devices Image Converter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        doInitializeControls();
        doInitializeLayouts();
        doAddControlsToContainers();
        doAddActionListeners();
        
        thisWindow.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
        thisWindow.setResizable(false);
        thisWindow.pack();
    }
    
    /**
     * PRIVATE METHOD: This is where all of the ActionListeners are created and
     * assigned for use by various parts of the window's interface.
     */
    private void doAddActionListeners() {
        /* BUTTON: btnDispUpdate (Update) */
        btnDispUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disp.setSize((Integer) width.getValue(), (Integer) height.getValue());
                new Thread() {
                    @Override
                    public void run() {
                        disp.repaint();
                        SwingUtilities.updateComponentTreeUI(thisWindow);
                        thisWindow.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
                        thisWindow.pack();
                    }
                }.start();
            }
        });
        
        /* BUTTON: btnAddAsset (Add Image Asset) */
        btnAddAsset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File imageFile = Utils.getChosenFile(thisWindow);
                    if (imageFile != null) { // File was chosen...
                        ImageAsset asset = new ImageAsset(imageFile);
                        disp.registerItem(asset);
                        new Thread() {
                            @Override
                            public void run() {
                                disp.repaint();
                                assetManager.add(asset);
                                refreshImageAssets(true);
                                resetSelectedAssetObjects();
                            }
                        }.start();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(thisWindow, "Chosen image file resulted in an Exception:\n\t" + ex.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        /* TABLE: assetTable Selection Listener */
        assetTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selRow = assetTable.getSelectedRow();
                    if (selRow != -1) {
                        String imageId = (String) assetTable.getValueAt(selRow, 0);
                        ImageAsset asset = assetManager.get(imageId);
                        if (asset != null) {
                            enableSelectedAssetObjects();
                            txtAssetId.setText(asset.getId());
                            spnXPos.setValue(asset.getX());
                            spnYPos.setValue(asset.getY());
                            spnImageWidth.setValue(asset.getScaledImage().getWidth());
                            spnImageHeight.setValue(asset.getScaledImage().getHeight());
                        }
                    }
                }
            }
        });
        
        /* BUTTON: btnUpdatePos (Update Position) */
        btnUpdatePos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) {
                    disp.moveItem(txtAssetId.getText(), ((Integer)spnXPos.getValue()).intValue(), ((Integer)spnYPos.getValue()).intValue());
                    new Thread() {
                        @Override
                        public void run() {
                            disp.repaint();
                            refreshImageAssets(false);
                        }
                    }.start();
                }
            }
        });
        
        /* BUTTON: btnImgResize (Resize) */
        btnImgResize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) { // Asset ID is not blank...
                    ImageAsset asset = assetManager.get(txtAssetId.getText());
                    if (asset != null) {
                        asset.getScaledImage(
                            ((Integer) spnImageWidth.getValue()).intValue(), 
                            ((Integer) spnImageHeight.getValue()).intValue()
                        );
                        disp.registerItem(asset);
                        new Thread() {
                            @Override
                            public void run() {
                                disp.repaint();
                                refreshImageAssets(false);
                            }
                        }.start();
                    }
                }
            }
        });
        
        /* BUTTON: btnImgRemove (Remove) */
        btnImgRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) {
                    assetManager.remove(txtAssetId.getText());
                    disp.unregisterItem(txtAssetId.getText());
                    new Thread() {
                        @Override
                        public void run() {
                            disp.repaint();
                            refreshImageAssets(true);
                            resetSelectedAssetObjects();
                        }
                    }.start();
                }
            }
        });
        
        /* BUTTON: btnImgExport (Export) */
        btnImgExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) {
                    ImageAsset asset = assetManager.get(txtAssetId.getText());
                    List<ImageAsset> aList = new ArrayList<>();
                    aList.add(asset);
                    ExportWindow epw = new ExportWindow(aList, thisWindow);
                    epw.setVisible(true);
                }
            }
        });
        
        /* BUTTON: btnExportAll (Export All) */
        btnExportAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (assetManager.size() > 0) {
                    ExportWindow epw = new ExportWindow(assetManager.getAssets(), thisWindow);
                    epw.setVisible(true);
                }
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
        pnlSelAsset.add(spnXPos);
        pnlSelAsset.add(lblYPos);
        pnlSelAsset.add(spnYPos);
        pnlSelAsset.add(btnUpdatePos);
        pnlSelAsset.add(lblImageSize);
        pnlSelAsset.add(spnImageWidth);
        pnlSelAsset.add(lblBy);
        pnlSelAsset.add(spnImageHeight);
        pnlSelAsset.add(btnImgResize);
        pnlSelAsset.add(lblResizeNote);
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
        selAssetLayout.putConstraint(SpringLayout.NORTH, txtAssetId, 1, SpringLayout.NORTH, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.WEST, txtAssetId, 2, SpringLayout.EAST, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.EAST, txtAssetId, -6, SpringLayout.EAST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblXPos, 6, SpringLayout.SOUTH, txtAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblXPos, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, spnXPos, 6, SpringLayout.SOUTH, txtAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, spnXPos, 2, SpringLayout.EAST, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblYPos, 6, SpringLayout.SOUTH, txtAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblYPos, 6, SpringLayout.EAST, spnXPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, spnYPos, 6, SpringLayout.SOUTH, txtAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, spnYPos, 2, SpringLayout.EAST, lblYPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnUpdatePos, 6, SpringLayout.SOUTH, lblAssetId);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnUpdatePos, 12, SpringLayout.EAST, spnYPos);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblImageSize, 15, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblImageSize, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, spnImageWidth, 15, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, spnImageWidth, 12, SpringLayout.EAST, lblImageSize);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblBy, 16, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblBy, 3, SpringLayout.EAST, spnImageWidth);
        selAssetLayout.putConstraint(SpringLayout.NORTH, spnImageHeight, 15, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, spnImageHeight, 3, SpringLayout.EAST, lblBy);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgResize, 12, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgResize, 12, SpringLayout.EAST, spnImageHeight);
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblResizeNote, 20, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblResizeNote, 3, SpringLayout.EAST, btnImgResize);
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
        assetTable.setRowSelectionAllowed(true);
        assetTable.setDefaultEditor(Object.class, new TableCellEditor() {
            @Override
            public Object getCellEditorValue() {return null;}
            @Override
            public boolean isCellEditable(EventObject anEvent) {return false;}
            @Override
            public boolean shouldSelectCell(EventObject anEvent) {return false;}
            @Override
            public boolean stopCellEditing() {return false;}
            @Override
            public void cancelCellEditing() {}
            @Override
            public void addCellEditorListener(CellEditorListener l) {}
            @Override
            public void removeCellEditorListener(CellEditorListener l) {}
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {return null;}
        });
        
        /* BUTTON: Export All */
        btnExportAll.setPreferredSize(new Dimension(700, 25));
        btnExportAll.setBorder(btnBorder);
        
        /* Selected Asset: */
        pnlSelAsset.setPreferredSize(new Dimension(700, 160));
        pnlSelAsset.setBorder(BorderFactory.createTitledBorder("Selected Asset:"));
        pnlSelAsset.setLayout(selAssetLayout);
        txtAssetId.setEditable(false);
        spnXPos.setPreferredSize(new Dimension(50,20));
        spnYPos.setPreferredSize(new Dimension(50,20));
        spnImageWidth.setPreferredSize(new Dimension(50, 20));
        spnImageHeight.setPreferredSize(new Dimension(50, 20));
        lblResizeNote.setForeground(Color.RED);
        lblResizeNote.setFont(new Font(lblResizeNote.getFont().getName(), Font.PLAIN, 9));
        sep.setOrientation(SwingConstants.HORIZONTAL);
        sep.setBackground(Color.LIGHT_GRAY);
        
        resetSelectedAssetObjects();
    }
    
    /* ==============================================================
     *                     PRIVATE Methods Below 
     * ==============================================================*/
    
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
    
    private void resetSelectedAssetObjects() {
        txtAssetId.setText("");
        spnXPos.setValue(0);
        spnYPos.setValue(0);
        spnImageWidth.setValue(0);
        spnImageHeight.setValue(0);
        
        spnXPos.setEnabled(false);
        spnYPos.setEnabled(false);
        spnImageWidth.setEnabled(false);
        spnImageHeight.setEnabled(false);
        
        btnUpdatePos.setEnabled(false);
        btnImgResize.setEnabled(false);
        btnImgRemove.setEnabled(false);
        btnImgExport.setEnabled(false);
    }
    
    private void enableSelectedAssetObjects() {
        spnXPos.setEnabled(true);
        spnYPos.setEnabled(true);
        spnImageWidth.setEnabled(true);
        spnImageHeight.setEnabled(true);
        btnUpdatePos.setEnabled(true);
        btnImgResize.setEnabled(true);
        btnImgRemove.setEnabled(true);
        btnImgExport.setEnabled(true);
    }
    
    private void refreshImageAssets(boolean clearSelection) {
        int rSel = assetTable.getSelectedRow();
        DefaultTableModel tModel = (DefaultTableModel) assetTable.getModel();
        tModel.setDataVector(assetManager.getData(), assetManager.getHeaders());
        tModel.fireTableDataChanged();
        if (clearSelection) {
            assetTable.clearSelection();
        } else if (rSel != -1) {
            assetTable.setRowSelectionInterval(rSel, rSel);
        }
    }
}

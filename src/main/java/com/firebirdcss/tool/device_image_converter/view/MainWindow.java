package com.firebirdcss.tool.device_image_converter.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
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

import com.firebirdcss.tool.device_image_converter.data.AssetManager;
import com.firebirdcss.tool.device_image_converter.data.pojo.ImageAsset;
import com.firebirdcss.tool.device_image_converter.listeners.ButtonOnEnterKeyListener;
import com.firebirdcss.tool.device_image_converter.listeners.SelectOnFocusListener;
import com.firebirdcss.tool.device_image_converter.listeners.ThreadedActionListener;
import com.firebirdcss.tool.device_image_converter.utils.Utils;
import com.firebirdcss.tool.device_image_converter.view.components.Screen;

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
    private JLabel lblDeviceDisp = new JLabel("Device Display:");
    private Screen disp = new Screen(128, 64);
    
    /* JPANEL: Display Settings: */
    private JPanel pnlDispSettings = new JPanel();
    private SpringLayout layDispCtrls = new SpringLayout();
    private JLabel lblDispWidth = new JLabel("Width:");
    private JSpinner spnDispWidth = new JSpinner(new SpinnerNumberModel(128, 5, 300, 1));
    private JLabel lblDispHeight = new JLabel("Height:");
    private JSpinner spnDispHeight = new JSpinner(new SpinnerNumberModel(64, 5, 300, 1));
    private JButton btnDispUpdate = new JButton("Update");
    private JButton btnChgScrColor = new JButton("BG Color");
    private JCheckBox chkDispBinary = new JCheckBox("Show Binary");
    
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
    private JLabel lblResizeNote = new JLabel("<html>Note: To keep aspect-ratio set one dimension as desired and the other to '-1'.<br>Also, a zero dimension means to keep that dimension original size.</html>");
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
        /* SPINNER Width - width */
        SelectOnFocusListener.addListenerTo(spnDispWidth);
        ButtonOnEnterKeyListener.addListenerTo(spnDispWidth, btnDispUpdate);
        
        /* SPINNER Height - height */
        SelectOnFocusListener.addListenerTo(spnDispHeight);
        ButtonOnEnterKeyListener.addListenerTo(spnDispHeight, btnDispUpdate);
        
        /* BUTTON: btnDispUpdate (Update) */
        SelectOnFocusListener.addListenerTo(btnDispUpdate);
        btnDispUpdate.addFocusListener(new SelectOnFocusListener(btnDispUpdate));
        btnDispUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        disp.setSize(((Integer) spnDispWidth.getValue()).intValue(), ((Integer) spnDispHeight.getValue()).intValue());
                        disp.repaint();
                        SwingUtilities.updateComponentTreeUI(thisWindow);
                        thisWindow.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
                        thisWindow.pack();
                    }
                });
            }
        });
        
        chkDispBinary.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    disp.showBinary(true);
                } else {
                    disp.showBinary(false);
                }
            }
        });
        
        /* BUTTON: btnChgScrColor (BG Color)  */
        btnChgScrColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(thisWindow, "Pick Color", disp.getBackground());
                disp.setBackground(newColor);
            }
        });
        
        /* BUTTON: btnAddAsset (Add Image Asset) */
        SelectOnFocusListener.addListenerTo(btnAddAsset);
        btnAddAsset.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
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
        SelectOnFocusListener.addListenerTo(assetTable);
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
        
        /* BUTTON: btnExportAll (Export All) */
        SelectOnFocusListener.addListenerTo(btnExportAll);
        btnExportAll.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
                if (assetManager.size() > 0) {
                    ExportWindow epw = new ExportWindow(assetManager.getAssets(), thisWindow);
                    epw.setVisible(true);
                }
            }
        });
        
        /* TEXT Asset ID - txtAssetId */
        SelectOnFocusListener.addListenerTo(txtAssetId);
        
        /* SPINNER X-Pos - spnXPos */
        SelectOnFocusListener.addListenerTo(spnXPos);
        ButtonOnEnterKeyListener.addListenerTo(spnXPos, btnUpdatePos);
        
        /* SPINNER Y-Pos - spnYPos */
        SelectOnFocusListener.addListenerTo(spnYPos);
        ButtonOnEnterKeyListener.addListenerTo(spnYPos, btnUpdatePos);
        
        /* BUTTON: btnUpdatePos (Update Position) */
        SelectOnFocusListener.addListenerTo(btnUpdatePos);
        btnUpdatePos.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
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
        
        /* SPINNER Image Width - spnImageWidth */
        SelectOnFocusListener.addListenerTo(spnImageWidth);
        ButtonOnEnterKeyListener.addListenerTo(spnImageWidth, btnImgResize);
        
        /* SPINNER Image Height - spnImageHeight */
        SelectOnFocusListener.addListenerTo(spnImageHeight);
        ButtonOnEnterKeyListener.addListenerTo(spnImageHeight, btnImgResize);
        
        /* BUTTON: btnImgResize (Resize) */
        SelectOnFocusListener.addListenerTo(btnImgResize);
        btnImgResize.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) { // Asset ID is not blank...
                    ImageAsset asset = assetManager.get(txtAssetId.getText());
                    if (asset != null) {
                        asset.getScaledImage(
                            ((Integer) spnImageWidth.getValue()).intValue(), 
                            ((Integer) spnImageHeight.getValue()).intValue()
                        );
                        disp.registerItem(asset);
                        disp.repaint();
                        refreshImageAssets(false);
                    }
                }
            }
        });
        
        /* BUTTON: btnImgExport (Export) */
        SelectOnFocusListener.addListenerTo(btnImgExport);
        btnImgExport.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) {
                    ImageAsset asset = assetManager.get(txtAssetId.getText());
                    List<ImageAsset> aList = new ArrayList<>();
                    aList.add(asset);
                    ExportWindow epw = new ExportWindow(aList, thisWindow);
                    epw.setVisible(true);
                }
            }
        });
        
        /* BUTTON: btnImgRemove (Remove) */
        SelectOnFocusListener.addListenerTo(btnImgRemove);
        btnImgRemove.addActionListener(new ThreadedActionListener() {
            @Override
            public void actionOccurred(ActionEvent e) {
                if (!txtAssetId.getText().isBlank()) {
                    assetManager.remove(txtAssetId.getText());
                    disp.unregisterItem(txtAssetId.getText());
                    disp.repaint();
                    refreshImageAssets(true);
                    resetSelectedAssetObjects();
                }
            }
        });
    }
    
    /**
     * PRIVATE: This is where the various controls are added to their 
     * appropriate container.
     */
    private void doAddControlsToContainers() {
        /* Display Size: */
        pnlDispSettings.add(lblDispWidth);
        pnlDispSettings.add(spnDispWidth);
        pnlDispSettings.add(lblDispHeight);
        pnlDispSettings.add(spnDispHeight);
        pnlDispSettings.add(btnDispUpdate);
        pnlDispSettings.add(btnChgScrColor);
        pnlDispSettings.add(chkDispBinary);
        
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
        pane.add(lblDeviceDisp);
        pane.add(disp);
        pane.add(pnlDispSettings);
        pane.add(btnAddAsset);
        pane.add(assetsPane);
        pane.add(btnExportAll);
        pane.add(pnlSelAsset);
    }
    
    /**
     * PRIVATE: This is where all of the layouts used in this Window are defined.
     */
    private void doInitializeLayouts() {
        /* Display Size: */
        layDispCtrls.putConstraint(SpringLayout.NORTH, lblDispWidth, 10, SpringLayout.NORTH, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.WEST, lblDispWidth, 6, SpringLayout.WEST, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.NORTH, spnDispWidth, 6, SpringLayout.NORTH, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.WEST, spnDispWidth, 3, SpringLayout.EAST, lblDispWidth);
        layDispCtrls.putConstraint(SpringLayout.NORTH, lblDispHeight, 10, SpringLayout.NORTH, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.WEST, lblDispHeight, 12, SpringLayout.EAST, spnDispWidth);
        layDispCtrls.putConstraint(SpringLayout.NORTH, spnDispHeight, 6, SpringLayout.NORTH, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.WEST, spnDispHeight, 3, SpringLayout.EAST, lblDispHeight);
        layDispCtrls.putConstraint(SpringLayout.NORTH, btnDispUpdate, 6, SpringLayout.NORTH, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.EAST, btnDispUpdate, -6, SpringLayout.EAST, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.NORTH, btnChgScrColor, 6, SpringLayout.SOUTH, lblDispWidth);
        layDispCtrls.putConstraint(SpringLayout.WEST, btnChgScrColor, 6, SpringLayout.WEST, pnlDispSettings);
        layDispCtrls.putConstraint(SpringLayout.NORTH, chkDispBinary, 6, SpringLayout.SOUTH, lblDispWidth);
        layDispCtrls.putConstraint(SpringLayout.WEST, chkDispBinary, 6, SpringLayout.EAST, btnChgScrColor);
        
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
        selAssetLayout.putConstraint(SpringLayout.NORTH, lblResizeNote, 15, SpringLayout.SOUTH, lblXPos);
        selAssetLayout.putConstraint(SpringLayout.WEST, lblResizeNote, 3, SpringLayout.EAST, btnImgResize);
        selAssetLayout.putConstraint(SpringLayout.NORTH, sep, 6, SpringLayout.SOUTH, lblImageSize);
        selAssetLayout.putConstraint(SpringLayout.WEST, sep, 2, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.EAST, sep, -2, SpringLayout.EAST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgExport, 6, SpringLayout.SOUTH, sep);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgExport, 6, SpringLayout.WEST, pnlSelAsset);
        selAssetLayout.putConstraint(SpringLayout.NORTH, btnImgRemove, 6, SpringLayout.SOUTH, sep);
        selAssetLayout.putConstraint(SpringLayout.WEST, btnImgRemove, 6, SpringLayout.EAST, btnImgExport);
        
        /* Main Window */
        mainLayout.putConstraint(SpringLayout.NORTH, lblDeviceDisp, 6, SpringLayout.NORTH, pane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lblDeviceDisp, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, disp, 3, SpringLayout.SOUTH, lblDeviceDisp);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, disp, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, pnlDispSettings, 12, SpringLayout.SOUTH, disp);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pnlDispSettings, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, btnAddAsset, 12, SpringLayout.SOUTH, pnlDispSettings);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnAddAsset, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, assetsPane, 12, SpringLayout.SOUTH, btnAddAsset);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetsPane, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, pnlSelAsset, 12, SpringLayout.SOUTH, assetsPane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pnlSelAsset, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, btnExportAll, 12, SpringLayout.SOUTH, pnlSelAsset);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnExportAll, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        
    }
    
    private void doInitializeControls() {
        /* Main Window */
        pane.setLayout(mainLayout);
        
        disp.setBackground(Color.WHITE);
        
        /* Display Size: */
        pnlDispSettings.setBorder(BorderFactory.createTitledBorder("Display Settings:"));
        pnlDispSettings.setPreferredSize(new Dimension(400, 85));
        pnlDispSettings.setLayout(layDispCtrls);
        
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
            lblDeviceDisp.getPreferredSize().height +
            disp.getPreferredSize().height +
            pnlDispSettings.getPreferredSize().height +
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
        
        assetTable.getColumnModel().getColumn(0).setPreferredWidth(400);
        assetTable.getColumnModel().getColumn(1).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(2).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(4).setPreferredWidth(20);
        
        if (clearSelection) {
            assetTable.clearSelection();
        } else if (rSel != -1) {
            assetTable.setRowSelectionInterval(rSel, rSel);
        }
    }
}

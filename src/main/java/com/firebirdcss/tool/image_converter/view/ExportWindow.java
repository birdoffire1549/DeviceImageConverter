package com.firebirdcss.tool.image_converter.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.firebirdcss.tool.image_converter.data.AssetManager;
import com.firebirdcss.tool.image_converter.data.Settings;
import com.firebirdcss.tool.image_converter.data.enums.ImageType;
import com.firebirdcss.tool.image_converter.data.pojo.ImageAsset;
import com.firebirdcss.tool.image_converter.data.pojo.ImageExportInfo;
import com.firebirdcss.tool.image_converter.utils.ImageUtils;
import com.firebirdcss.tool.image_converter.utils.Utils;

public class ExportWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    final AssetManager am = new AssetManager(); 
    
    private JFrame thisWindow = this;
    private Container pane = this.getContentPane();
    private SpringLayout mainLayout = new SpringLayout();
    
    /* SECTION - What: */
    private DefaultTableModel assetTableModel = new DefaultTableModel(am.getData(), am.getHeaders());
    private JTable assetTable = new JTable(assetTableModel);
    private JScrollPane assetsPane = new JScrollPane(assetTable);
    
    /* SECTION - As: */
    private JPanel pnlExportAs = new JPanel();
    private SpringLayout layExportAs = new SpringLayout();
    private ButtonGroup grpAsOptions = new ButtonGroup();
    private JRadioButton rbCTypeArray = new JRadioButton("C-Type Binary Array");
    private JRadioButton rbScaledImage = new JRadioButton("Scaled Image");
    private JRadioButton rbBinData = new JRadioButton("Binary Data");
    private JSeparator sepExportAs = new JSeparator();
    private JPanel pnlAddInfoCType = new JPanel();
    private SpringLayout layAddInfoCType = new SpringLayout();
    private JLabel lblConversionThreshold = new JLabel("Black & White conversion threshold:");
    private JSpinner spnThreshold = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
    private JLabel lblPadValue = new JLabel("Binary Padding Value:");
    private JSpinner spnPadValue = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
    private JLabel lblBinForBlack = new JLabel("Select binary color representation:");
    private ButtonGroup grpBinForBlack = new ButtonGroup();
    private JRadioButton rbOneForBlack = new JRadioButton("0 - White; 1 - Black");
    private JRadioButton rbZeroForBlack = new JRadioButton("0 - Black; 1 - White");
    private JPanel pnlRowTerm = new JPanel();
    private SpringLayout layRowTerm = new SpringLayout();
    private ButtonGroup grpRowTerm = new ButtonGroup();
    private JRadioButton rbRowTermNL = new JRadioButton("NL (\\n)");
    private JRadioButton rbRowTermRetNL = new JRadioButton("Ret & NL (\\r\\n)");
    private JRadioButton rbRowTermNone = new JRadioButton("None");
    private JPanel pnlAddInfoScaledImage = new JPanel();
    private SpringLayout layAddInfoScaledImage = new SpringLayout();
    private JLabel lblSelectExportType = new JLabel("Select image export type:");
    private JComboBox<String> cboImageType = new JComboBox<>(ImageType.getSystemSupportedTypeStrings());
    
    /* SECTION - Where: */
    private JPanel pnlExportWhere = new JPanel();
    private SpringLayout layExportWhere = new SpringLayout();
    private ButtonGroup grpWhereOptions = new ButtonGroup();
    private JRadioButton rbClipboard = new JRadioButton("Clipboard");
    private JRadioButton rbFile = new JRadioButton("File");
    
    /* BUTTONS */
    private JButton btnExport = new JButton("Export");
    private JButton btnCancel = new JButton("Cancel");
    
    private final JFrame parent;
    
    /**
     * CONSTRUCTOR: Class constructor. Here is where the object gets
     * initialized. 
     *
     */
    public ExportWindow(List<ImageAsset> assetsToExport, JFrame parent) {
        this.parent = parent;
        this.setLocation(parent.getLocation());
        this.parent.setEnabled(false);
        this.parent.setVisible(false);
        
        for (ImageAsset a : assetsToExport) {
            am.add(a);
        }
        
        doInitializeElements();
        doAddComponentsToContainers();
        doInitializeLayouts();
        doAddListeners();
        
        refreshImageAssets();
    }
    
    private void doAddListeners() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
                parent.setVisible(true);
                parent.setLocation(thisWindow.getLocation());
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisWindow.dispatchEvent(new WindowEvent(thisWindow, WindowEvent.WINDOW_CLOSING));
            }
        });
        
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean exportComplete = false;
                if (rbCTypeArray.isSelected()) { // <------------- rbCTypeArray Selected
                    exportComplete = exportCTypeArray();
                } else if (rbScaledImage.isSelected()) { // <------- rbScaledImage Selected
                    exportComplete = exportScaledImage();
                } else if (rbBinData.isSelected()) { // <--------- rbBinData Selected
                    exportComplete = exportBinData();
                }
                
                // Close the window...
                if (exportComplete) {
                    persistSettings();
                    thisWindow.dispatchEvent(new WindowEvent(thisWindow, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
        
        rbCTypeArray.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbOneForBlack.isSelected();
                pnlAddInfoCType.setVisible(true);
                pnlRowTerm.setVisible(true);
                rbRowTermNL.setSelected(true);
                rbRowTermNone.setVisible(false);
                pnlAddInfoScaledImage.setVisible(false);
            }
        });
        
        rbScaledImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pnlAddInfoCType.setVisible(false);
                pnlRowTerm.setVisible(false);
                if (rbFile.isSelected()) {
                    pnlAddInfoScaledImage.setVisible(true);
                } else {
                    pnlAddInfoScaledImage.setVisible(false);
                }
            }
        });
        
        rbBinData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbOneForBlack.isSelected();
                pnlAddInfoCType.setVisible(true);
                pnlRowTerm.setVisible(true);
                rbRowTermNone.setVisible(true);
                pnlAddInfoScaledImage.setVisible(false);
            }
        });
        
        rbClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbScaledImage.isSelected()) {
                    pnlAddInfoScaledImage.setVisible(false);
                }
            }
        });
        
        rbFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbScaledImage.isSelected()) {
                    pnlAddInfoScaledImage.setVisible(true);
                }
            }
        });
    }
    
    private boolean exportScaledImage() {
        if (rbClipboard.isSelected()) {
            List<ImageAsset> assetList = am.getAssets();
            for (int i = 0; i < assetList.size(); i ++) {
                Utils.persistDataToClipboard(assetList.get(i).getScaledImage());
                if (i < assetList.size() - 1) { // Not last image...
                    int choice = JOptionPane.showConfirmDialog(
                        thisWindow, 
                        "Image was send to Clipboard.\nPlease paste it off somewhere before clicking 'Ok'.\nWhen you are ready for the next image click 'Ok' and it will be placed on the clipboard,\nor 'Cancel' to stop.", 
                        "Image sent to Clipboard", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (choice == JOptionPane.CANCEL_OPTION) {
                        JOptionPane.showMessageDialog(thisWindow, "Export operation was canceled.", "Export canceled", JOptionPane.WARNING_MESSAGE);
                        
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(thisWindow, "Image was sent to Clipboard.", "Image sent to Clipboard", JOptionPane.INFORMATION_MESSAGE);
                    
                    return true;
                }
            }
        } else if (rbFile.isSelected()) {
            List<ImageExportInfo> data = new ArrayList<>();
            for (ImageAsset a : am.getAssets()) {
                String strExportType = (String) cboImageType.getSelectedItem();
                ImageType exportType = ImageType.getEnumByValue(strExportType);
                data.add(new ImageExportInfo(a, exportType));
            }
            try {
                if (Utils.persistDataToFiles(data, thisWindow)) {
                    JOptionPane.showMessageDialog(thisWindow, "Image data was saved to file(s).", "Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    return true;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(thisWindow, "Error saving Image data:\n\t" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    /**
     * EXPORT BINARY DATA: This method handles transforming the data for exporting
     * and then exports the data as needed.
     * 
     * @return Return a true if export successful otherwise false as <code>boolean</code>
     */
    private boolean exportBinData() {
        Map<String/*ImageId*/, String/*Data*/> binImages = new HashMap<>();
        String lineTerm = "";
        if (rbRowTermNL.isSelected()) {
            lineTerm = "\n";
        } else if (rbRowTermRetNL.isSelected()) {
            lineTerm = "\r\n";
        }
        
        // Transform the data for export...
        for (ImageAsset a : am.getAssets()) { // Iterate and transform assets...
            byte[][] binImage = ImageUtils.convertImageToBinary(
                a.getScaledImage(), 
                ((Integer) spnThreshold.getValue()).intValue(), 
                rbOneForBlack.isSelected() ? 1 : 0, 
                true, 
                ((Integer) spnPadValue.getValue()).intValue()
            );
            // Build binary image into String representation...
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < binImage.length; y++) { // Iterate the binary image rows...
                for (int x = 0; x < binImage[y].length; x++) { // Iterate binary image columns...
                    sb.append(String.valueOf(binImage[y][x]));
                }
                if (y != binImage.length - 1) { // Row is not the last row...
                    sb.append(lineTerm);
                }
            }
            
            binImages.put(a.getId(), sb.toString());
        }
        
        if (rbClipboard.isSelected()) { // <---- Export to Clipboard...
            StringBuilder sb = new StringBuilder();
            for (String itm : binImages.values()) { // Put all data together for export...
                sb.append(itm);
                sb.append(lineTerm);
                sb.append(lineTerm);
                sb.append(lineTerm);
            }
            // Send to clipboard and notify user...
            Utils.persistDataToClipboard(sb.toString());
            JOptionPane.showMessageDialog(thisWindow, "Binary Image Data was send to Clipboard.");
            
            return true;
        } else if (rbFile.isSelected()) { // <---- Export to file(s)...
            Map<String/*FileName*/, String/*Data*/> data = new HashMap<>();
            for (Entry<String, String> e : binImages.entrySet()) { // Modify data for sending...
                data.put(Utils.toSafeCVarName(e.getKey()) + ".txt", e.getValue());
            }
            // Send to file or files and notify the user...
            try {
                if (Utils.persistDataToFiles(data, thisWindow)) { 
                    JOptionPane.showMessageDialog(thisWindow, "Data was saved to file(s).", "Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    return true;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(thisWindow, "Error saving data:\n\t" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    private boolean exportCTypeArray() {
        StringBuilder sb = new StringBuilder();
        String lineTerm = rbRowTermRetNL.isSelected() ? "\r\n" : "\n";
        
        for (ImageAsset a : am.getAssets()) { // Handle each asset...
            byte[][] binImage = ImageUtils.convertImageToBinary(
                a.getScaledImage(), 
                ((Integer) spnThreshold.getValue()).intValue(), 
                rbOneForBlack.isSelected() ? 1 : 0, 
                true, 
                ((Integer) spnPadValue.getValue()).intValue()
            );
            String array = ImageUtils.toCTypeArray(binImage, a.getId(), lineTerm);
            sb.append(array);
            sb.append(lineTerm);
            sb.append(lineTerm);
        }
        
        if (rbClipboard.isSelected()) {
            // Send data to clipboard...
            Utils.persistDataToClipboard(sb.toString());
            
            // Let user know...
            JOptionPane.showMessageDialog(thisWindow, "C-Type Array Code was send to Clipboard.");
            
            return true;
        } else if (rbFile.isSelected()) {
            try {
                if (Utils.persistDataToFile(sb.toString(), thisWindow)) { // Data was saved...
                    JOptionPane.showMessageDialog(thisWindow, "Array data was saved to file.", "Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    return true;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(thisWindow, "Error saving data:\n\t" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    private void doInitializeElements() {
        pane.setLayout(mainLayout);
        
        /* SECTION - Export What: */
        assetTable.getTableHeader().setForeground(Color.white);
        assetsPane.setPreferredSize(new Dimension(700, 200));
        assetsPane.setBorder(BorderFactory.createTitledBorder("Assets to Export:"));
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
        
        /* SECTION - Export As: */
        pnlExportAs.setLayout(layExportAs);
        pnlExportAs.setBorder(BorderFactory.createTitledBorder("Export As:"));
        pnlExportAs.setPreferredSize(new Dimension(700, 150));
        grpAsOptions.add(rbCTypeArray);
        grpAsOptions.add(rbScaledImage);
        grpAsOptions.add(rbBinData);
        if (Settings.lastSelectedExportAs == -1) {
            rbCTypeArray.setSelected(true);
            Settings.lastSelectedExportAs = 0;
        } else {
            switch(Settings.lastSelectedExportAs) {
                case 0:
                    rbCTypeArray.setSelected(true);
                    break;
                case 1:
                    rbScaledImage.setSelected(true);
                    break;
                case 2:
                    rbBinData.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        if (Settings.lastBWConversionThresh != -1) {
            spnThreshold.setValue(Settings.lastBWConversionThresh);
        } else {
            spnThreshold.setValue(50);
            Settings.lastBWConversionThresh = 50;
        }
        if (Settings.lastBinPadVal != -1) {
            spnPadValue.setValue(Settings.lastBinPadVal);
        } else {
            spnPadValue.setValue(0);
            Settings.lastBinPadVal = 0;
        }
        sepExportAs.setOrientation(SwingConstants.VERTICAL);
        sepExportAs.setForeground(Color.LIGHT_GRAY);
        pnlAddInfoCType.setLayout(layAddInfoCType);
        pnlAddInfoCType.setBorder(BorderFactory.createTitledBorder("Additional Information:"));
        pnlAddInfoCType.setEnabled(true);
        
        if (Settings.lastSelectedBinaryColorRep == -1) {
            rbOneForBlack.setSelected(true);
            Settings.lastSelectedBinaryColorRep = 0;
        } else {
            switch(Settings.lastSelectedBinaryColorRep) {
                case 0:
                    rbOneForBlack.setSelected(true);
                    break;
                case 1:
                    rbZeroForBlack.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        pnlRowTerm.setLayout(layRowTerm);
        pnlRowTerm.setBorder(BorderFactory.createTitledBorder("Row Terminator:"));
        pnlRowTerm.setVisible(true);
        rbRowTermNone.setVisible(false);
        
        if (Settings.lastSelectedRowTerm == -1) {
            rbRowTermNL.setSelected(true);
            Settings.lastSelectedRowTerm = 0;
        } else {
            switch(Settings.lastSelectedRowTerm) {
                case 0:
                    rbRowTermNL.setSelected(true);
                    break;
                case 1:
                    rbRowTermRetNL.setSelected(true);
                    break;
                case 2:
                    rbRowTermNone.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        
        pnlAddInfoScaledImage.setLayout(layAddInfoScaledImage);
        pnlAddInfoScaledImage.setBorder(BorderFactory.createTitledBorder("Additional Information:"));
        pnlAddInfoScaledImage.setVisible(false);
        if (Settings.lastSelectedImageExportType.isEmpty()) {
            cboImageType.setSelectedItem(ImageType.JPG.toString());
            Settings.lastSelectedImageExportType = ImageType.JPG.toString();
        } else {
            cboImageType.setSelectedItem(Settings.lastSelectedImageExportType);
        }
        
        /* SECTION - Export Where: */
        pnlExportWhere.setLayout(layExportWhere);
        pnlExportWhere.setBorder(BorderFactory.createTitledBorder("Export Where:"));
        pnlExportWhere.setPreferredSize(new Dimension(700, 90));
        
        if (Settings.lastSelectedExportWhere == -1) {
            rbClipboard.setSelected(true);
            Settings.lastSelectedExportWhere = 0;
        } else {
            switch(Settings.lastSelectedExportWhere) {
                case 0:
                    rbClipboard.setSelected(true);
                    break;
                case 1:
                    rbFile.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        rbFile.setEnabled(true);
        
        if (rbFile.isSelected() && rbScaledImage.isSelected()) {
            pnlAddInfoScaledImage.setVisible(true);
            pnlAddInfoCType.setVisible(false);
        } else if (rbCTypeArray.isSelected() || rbBinData.isSelected()) {
            pnlAddInfoCType.setVisible(true);
            pnlAddInfoScaledImage.setVisible(false);
        }
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Export Image Assets");
        this.setPreferredSize(new Dimension(getContentWidth(), getContentHeight()));
        this.setResizable(false);
        this.pack();
    }
    
    private void doAddComponentsToContainers() {
        /* BUTTON GROUPS */
        grpBinForBlack.add(rbOneForBlack);
        grpBinForBlack.add(rbZeroForBlack);
        grpRowTerm.add(rbRowTermNL);
        grpRowTerm.add(rbRowTermRetNL);
        grpRowTerm.add(rbRowTermNone);
        grpWhereOptions.add(rbClipboard);
        grpWhereOptions.add(rbFile);
        
        /* SECTION - Export As: */
        pnlExportAs.add(rbCTypeArray);
        pnlExportAs.add(rbScaledImage);
        pnlExportAs.add(rbBinData);
        pnlExportAs.add(sepExportAs);
        pnlExportAs.add(pnlAddInfoCType);
        pnlExportAs.add(pnlAddInfoScaledImage);
        
        /* Row Terminator: */
        pnlRowTerm.add(rbRowTermNL);
        pnlRowTerm.add(rbRowTermRetNL);
        pnlRowTerm.add(rbRowTermNone);
        
        /* SECTION - Additional Information: */
        pnlAddInfoCType.add(lblConversionThreshold);
        pnlAddInfoCType.add(spnThreshold);
        pnlAddInfoCType.add(lblPadValue);
        pnlAddInfoCType.add(spnPadValue);
        pnlAddInfoCType.add(lblBinForBlack);
        pnlAddInfoCType.add(rbOneForBlack);
        pnlAddInfoCType.add(rbZeroForBlack);
        pnlAddInfoCType.add(pnlRowTerm);
        
        /* SECTION - Additional Information: (pnlAddInfoScaledImage) */
        pnlAddInfoScaledImage.add(lblSelectExportType);
        pnlAddInfoScaledImage.add(cboImageType);
        
        /* SECTION - Export Where: */
        pnlExportWhere.add(rbClipboard);
        pnlExportWhere.add(rbFile);
        
        /* MAIN SECTION */
        pane.add(assetsPane);
        pane.add(pnlExportAs);
        pane.add(pnlExportWhere);
        pane.add(btnExport);
        pane.add(btnCancel);
    }
    
    private void doInitializeLayouts() {
        /* SECTION - Export As: */
        layExportAs.putConstraint(SpringLayout.NORTH, rbCTypeArray, 6, SpringLayout.NORTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.WEST, rbCTypeArray, 12, SpringLayout.WEST, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.NORTH, rbScaledImage, 6, SpringLayout.SOUTH, rbCTypeArray);
        layExportAs.putConstraint(SpringLayout.WEST, rbScaledImage, 12, SpringLayout.WEST, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.NORTH, rbBinData, 6, SpringLayout.SOUTH, rbScaledImage);
        layExportAs.putConstraint(SpringLayout.WEST, rbBinData, 12, SpringLayout.WEST, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.NORTH, sepExportAs, 3, SpringLayout.NORTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.SOUTH, sepExportAs, -3, SpringLayout.SOUTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.WEST, sepExportAs, 12, SpringLayout.EAST, rbCTypeArray);
        layExportAs.putConstraint(SpringLayout.NORTH, pnlAddInfoCType, 3, SpringLayout.NORTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.SOUTH, pnlAddInfoCType, -3, SpringLayout.SOUTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.WEST, pnlAddInfoCType, 3, SpringLayout.EAST, sepExportAs);
        layExportAs.putConstraint(SpringLayout.EAST, pnlAddInfoCType, -3, SpringLayout.EAST, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.NORTH, pnlAddInfoScaledImage, 3, SpringLayout.NORTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.SOUTH, pnlAddInfoScaledImage, -3, SpringLayout.SOUTH, pnlExportAs);
        layExportAs.putConstraint(SpringLayout.WEST, pnlAddInfoScaledImage, 3, SpringLayout.EAST, sepExportAs);
        layExportAs.putConstraint(SpringLayout.EAST, pnlAddInfoScaledImage, -3, SpringLayout.EAST, pnlExportAs);
        
        /* Row Terminator */
        layRowTerm.putConstraint(SpringLayout.NORTH, rbRowTermNL, 2, SpringLayout.NORTH, pnlRowTerm);
        layRowTerm.putConstraint(SpringLayout.NORTH, rbRowTermRetNL, 2, SpringLayout.SOUTH, rbRowTermNL);
        layRowTerm.putConstraint(SpringLayout.NORTH, rbRowTermNone, 2, SpringLayout.SOUTH, rbRowTermRetNL);
        
        
        /* SECTION - Additional Information: */
        layAddInfoCType.putConstraint(SpringLayout.NORTH, lblConversionThreshold, 6, SpringLayout.NORTH, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.WEST, lblConversionThreshold, 6, SpringLayout.WEST, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, spnThreshold, 3, SpringLayout.NORTH, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.WEST, spnThreshold, 6, SpringLayout.EAST, lblConversionThreshold);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, lblPadValue, 5, SpringLayout.SOUTH, lblConversionThreshold);
        layAddInfoCType.putConstraint(SpringLayout.WEST, lblPadValue, 6, SpringLayout.WEST, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, spnPadValue, 1, SpringLayout.SOUTH, lblConversionThreshold);
        layAddInfoCType.putConstraint(SpringLayout.WEST, spnPadValue, 6, SpringLayout.EAST, lblPadValue);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, lblBinForBlack, 8, SpringLayout.SOUTH, lblPadValue);
        layAddInfoCType.putConstraint(SpringLayout.WEST, lblBinForBlack, 6, SpringLayout.WEST, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, rbOneForBlack, 2, SpringLayout.SOUTH, lblBinForBlack);
        layAddInfoCType.putConstraint(SpringLayout.WEST, rbOneForBlack, 6, SpringLayout.WEST, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, rbZeroForBlack, 2, SpringLayout.SOUTH, lblBinForBlack);
        layAddInfoCType.putConstraint(SpringLayout.WEST, rbZeroForBlack, 12, SpringLayout.EAST, rbOneForBlack);
        layAddInfoCType.putConstraint(SpringLayout.NORTH, pnlRowTerm, -2, SpringLayout.NORTH, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.EAST, pnlRowTerm, 0, SpringLayout.EAST, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.SOUTH, pnlRowTerm, 0, SpringLayout.SOUTH, pnlAddInfoCType);
        layAddInfoCType.putConstraint(SpringLayout.WEST, pnlRowTerm, -145, SpringLayout.EAST, pnlAddInfoCType);
        
        /* SECTION - Additional Information: (pnlAddInfoScaledImage) */
        layAddInfoScaledImage.putConstraint(SpringLayout.NORTH, lblSelectExportType, 20, SpringLayout.NORTH, pnlAddInfoScaledImage);
        layAddInfoScaledImage.putConstraint(SpringLayout.WEST, lblSelectExportType, 30, SpringLayout.WEST, pnlAddInfoScaledImage);
        layAddInfoScaledImage.putConstraint(SpringLayout.NORTH, cboImageType, 6, SpringLayout.SOUTH, lblSelectExportType);
        layAddInfoScaledImage.putConstraint(SpringLayout.WEST, cboImageType, 30, SpringLayout.WEST, pnlAddInfoScaledImage);
        
        /* SECTION - Export Where: */
        layExportWhere.putConstraint(SpringLayout.NORTH, rbClipboard, 6, SpringLayout.NORTH, pnlExportWhere);
        layExportWhere.putConstraint(SpringLayout.WEST, rbClipboard, 12, SpringLayout.NORTH, pnlExportWhere);
        layExportWhere.putConstraint(SpringLayout.NORTH, rbFile, 6, SpringLayout.SOUTH, rbClipboard);
        layExportWhere.putConstraint(SpringLayout.WEST, rbFile, 12, SpringLayout.NORTH, pnlExportWhere);
        
        /* MAIN SECTION */
        mainLayout.putConstraint(SpringLayout.NORTH, assetsPane, 6, SpringLayout.NORTH, pane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetsPane, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, pnlExportAs, 12, SpringLayout.SOUTH, assetsPane);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pnlExportAs, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, pnlExportWhere, 12, SpringLayout.SOUTH, pnlExportAs);
        mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pnlExportWhere, 0, SpringLayout.HORIZONTAL_CENTER, pane);
        mainLayout.putConstraint(SpringLayout.NORTH, btnExport, 12, SpringLayout.SOUTH, pnlExportWhere);
        mainLayout.putConstraint(SpringLayout.WEST, btnExport, 0, SpringLayout.WEST, pnlExportWhere);
        mainLayout.putConstraint(SpringLayout.NORTH, btnCancel, 12, SpringLayout.SOUTH, pnlExportWhere);
        mainLayout.putConstraint(SpringLayout.EAST, btnCancel, 0, SpringLayout.EAST, pnlExportWhere);
    }
    
    private int getContentHeight() {
        int result = 
            assetsPane.getPreferredSize().height +
            pnlExportAs.getPreferredSize().height +
            pnlExportWhere.getPreferredSize().height +
            btnExport.getPreferredSize().height 
        ;
        
        return result + 100;
    }
    
    private int getContentWidth() {
        int result = assetsPane.getPreferredSize().width;
        
        return result + 100;
    }
    
    private void refreshImageAssets() {
        DefaultTableModel tModel = (DefaultTableModel) assetTable.getModel();
        tModel.setDataVector(am.getData(), am.getHeaders());
        tModel.fireTableDataChanged();
        assetTable.clearSelection();
        assetTable.getColumnModel().getColumn(0).setPreferredWidth(400);
        assetTable.getColumnModel().getColumn(1).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(2).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        assetTable.getColumnModel().getColumn(4).setPreferredWidth(20);
    }
    
    private void persistSettings() {
        if (rbCTypeArray.isSelected()) {
            Settings.lastSelectedExportAs = 0;
        } else if (rbScaledImage.isSelected()) {
            Settings.lastSelectedExportAs = 1;
        } else if (rbBinData.isSelected()) {
            Settings.lastSelectedExportAs = 2;
        }
        
        Settings.lastBWConversionThresh = ((Integer) spnThreshold.getValue()).intValue();
        Settings.lastBinPadVal = ((Integer) spnPadValue.getValue()).intValue();
        Settings.lastSelectedImageExportType = cboImageType.getSelectedItem().toString();
        
        if (rbOneForBlack.isSelected()) {
            Settings.lastSelectedBinaryColorRep = 0;
        } else if (rbZeroForBlack.isSelected()) {
            Settings.lastSelectedBinaryColorRep = 1;
        }
        
        if (rbClipboard.isSelected()) {
            Settings.lastSelectedExportWhere = 0;
        } else if (rbFile.isSelected()) {
            Settings.lastSelectedExportWhere = 1;
        }
        
        if (rbRowTermNL.isSelected()) {
            Settings.lastSelectedRowTerm = 0;
        } else if (rbRowTermRetNL.isSelected()) {
            Settings.lastSelectedRowTerm = 1;
        } else if (rbRowTermNone.isSelected()) {
            Settings.lastSelectedRowTerm = 2;
        }
    }
    
    /*
     * =============================================
     * METHODS BELOW ARE FOR CLASS DEBUGGING ONLY!!!
     * =============================================
     */
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                List<ImageAsset> ia = new ArrayList<>();
//                JFrame main = new JFrame();
//                SpringLayout sp = new SpringLayout();
//                main.getContentPane().setLayout(sp);
//                main.setSize(new Dimension(800, 800));
//                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                JButton jb = new JButton("Click Me");
//                jb.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        JOptionPane.showMessageDialog(main, "POPUP!!!");
//                    }
//                });
//                main.getContentPane().add(jb);
//                
//                JButton jb2 = new JButton("New Window");
//                jb2.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        ExportWindow epw = new ExportWindow(ia, main);
//                        epw.setVisible(true);
//                    }
//                });
//                main.getContentPane().add(jb2);
//                
//                sp.putConstraint(SpringLayout.NORTH, jb, 6, SpringLayout.NORTH, main.getContentPane());
//                sp.putConstraint(SpringLayout.WEST, jb, 6, SpringLayout.WEST, main.getContentPane());
//                sp.putConstraint(SpringLayout.NORTH, jb2, 6, SpringLayout.SOUTH, jb);
//                sp.putConstraint(SpringLayout.WEST, jb, 6, SpringLayout.WEST, main.getContentPane());
//                
//                main.setVisible(true);
//            }
//        });
//    }
}

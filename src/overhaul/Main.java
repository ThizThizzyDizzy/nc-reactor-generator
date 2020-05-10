package overhaul;
import common.Version;
import common.Exporter;
import discord.BotInterface;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataListener;
public class Main extends javax.swing.JFrame{
    public static Main instance;
    public static GenerationPlan genPlan = GenerationPlan.DEFAULT;
    public static GenerationModel genModel = GenerationModel.DEFAULT;
    public static String toTime(long timeDiff) {
        String time;
        if(timeDiff<1_000_000){
            time = timeDiff+"ns";
        }else{
            timeDiff/=1_000_000;
            if(timeDiff<1000){
                time = timeDiff+"ms";
            }else{
                timeDiff/=1000;
                if(timeDiff<60){
                    time = timeDiff+"s";
                }else{
                    timeDiff/=60;
                    if(timeDiff<60){
                        time = timeDiff+"m";
                    }else{
                        timeDiff/=60;
                        if(timeDiff<24){
                            time = timeDiff+"h";
                        }else{
                            timeDiff/=24;
                            time = timeDiff+"d";
                        }
                    }
                }
            }
        }
        return time;
    }
    static{
        try{
            Challenger.init();
        }catch(Exception ex){}
    }
    public Main(){
        initComponents();
        boxGenModelItemStateChanged(null);
        boxGenPlanItemStateChanged(null);
        fillConfigPanels(Configuration.DEFAULT);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        panelParts = new javax.swing.JPanel();
        labelParts = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listParts = new javax.swing.JList<>();
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        panelGenModel = new javax.swing.JPanel();
        boxGenModel = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaGenModelDesc = new javax.swing.JTextArea();
        panelGenModelSettings = new javax.swing.JPanel();
        panelGenPlan = new javax.swing.JPanel();
        boxGenPlan = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaGenPlanDesc = new javax.swing.JTextArea();
        panelGenPlanSettings = new javax.swing.JPanel();
        panelPriorities = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listPriorities = new javax.swing.JList<>();
        buttonPriorityDown = new javax.swing.JButton();
        buttonPriorityUp = new javax.swing.JButton();
        panelFuels = new javax.swing.JPanel();
        boxFuel = new javax.swing.JComboBox<>();
        boxFuelType = new javax.swing.JComboBox<>();
        panelSize = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spinnerX = new javax.swing.JSpinner();
        spinnerY = new javax.swing.JSpinner();
        spinnerZ = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        spinnerThreads = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        buttonStart = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        checkBoxSymmetryX = new javax.swing.JCheckBox();
        checkBoxSymmetryY = new javax.swing.JCheckBox();
        checkBoxSymmetryZ = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        panelOutput = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        checkBoxDrawReactors = new javax.swing.JCheckBox();
        checkBoxShowClusters = new javax.swing.JCheckBox();
        buttonExportImage = new javax.swing.JButton();
        buttonExportJSON = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        textAreaImport = new javax.swing.JTextArea();
        panelImportDisplay = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        textAreaImportOutput = new javax.swing.JTextArea();
        buttonImport = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        textAreaImportStats = new javax.swing.JTextArea();
        buttonImportExportImage = new javax.swing.JButton();
        buttonImportExportJSON = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        buttonConfigDefault = new javax.swing.JButton();
        buttonConfigCustom = new javax.swing.JButton();
        panelConfigHeatsinks = new javax.swing.JPanel();
        panelConfigFuels = new javax.swing.JPanel();
        panelConfigSources = new javax.swing.JPanel();
        panelConfigModerators = new javax.swing.JPanel();
        panelConfigReflectors = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Overhaul Reactor Generator "+Version.VERSION);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelParts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelParts.setText("Reactor Parts");

        listParts.setBackground(new java.awt.Color(190, 190, 190));
        listParts.setModel(getReactorPartsModel());
        listParts.setToolTipText("<html>\nThis is the list of parts available<br>\nSelected (white) parts will be used by the generator\nClick to select a single part\nShift-click to select a range\nCtrl-click to toggle a part\n</html>");
        listParts.setSelectedIndices(generateSelectedParts());
        listParts.setSelectionBackground(new java.awt.Color(255, 255, 255));
        listParts.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(listParts);

        javax.swing.GroupLayout panelPartsLayout = new javax.swing.GroupLayout(panelParts);
        panelParts.setLayout(panelPartsLayout);
        panelPartsLayout.setHorizontalGroup(
            panelPartsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPartsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPartsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(labelParts, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPartsLayout.setVerticalGroup(
            panelPartsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPartsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelParts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        boxGenModel.setModel(getGenerationModels());
        boxGenModel.setSelectedIndex(GenerationModel.models.indexOf(genModel));
        boxGenModel.setToolTipText("");
        boxGenModel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxGenModelItemStateChanged(evt);
            }
        });

        textAreaGenModelDesc.setEditable(false);
        textAreaGenModelDesc.setColumns(20);
        textAreaGenModelDesc.setLineWrap(true);
        textAreaGenModelDesc.setRows(5);
        textAreaGenModelDesc.setWrapStyleWord(true);
        jScrollPane2.setViewportView(textAreaGenModelDesc);

        panelGenModelSettings.setLayout(null);

        javax.swing.GroupLayout panelGenModelLayout = new javax.swing.GroupLayout(panelGenModel);
        panelGenModel.setLayout(panelGenModelLayout);
        panelGenModelLayout.setHorizontalGroup(
            panelGenModelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGenModelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGenModelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelGenModelSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(boxGenModel, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGenModelLayout.setVerticalGroup(
            panelGenModelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGenModelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxGenModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGenModelSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        boxGenPlan.setModel(getGenerationPlans());
        boxGenPlan.setSelectedIndex(GenerationPlan.plans.indexOf(genPlan));
        boxGenPlan.setToolTipText("");
        boxGenPlan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxGenPlanItemStateChanged(evt);
            }
        });

        textAreaGenPlanDesc.setEditable(false);
        textAreaGenPlanDesc.setColumns(20);
        textAreaGenPlanDesc.setLineWrap(true);
        textAreaGenPlanDesc.setRows(5);
        textAreaGenPlanDesc.setWrapStyleWord(true);
        jScrollPane3.setViewportView(textAreaGenPlanDesc);

        panelGenPlanSettings.setLayout(null);

        javax.swing.GroupLayout panelGenPlanLayout = new javax.swing.GroupLayout(panelGenPlan);
        panelGenPlan.setLayout(panelGenPlanLayout);
        panelGenPlanLayout.setHorizontalGroup(
            panelGenPlanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGenPlanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGenPlanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelGenPlanSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(boxGenPlan, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGenPlanLayout.setVerticalGroup(
            panelGenPlanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGenPlanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxGenPlan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGenPlanSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Generator Priorities");

        listPriorities.setModel(getPrioritiesModel());
        listPriorities.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(listPriorities);

        buttonPriorityDown.setText("Move Down");
        buttonPriorityDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPriorityDownActionPerformed(evt);
            }
        });

        buttonPriorityUp.setText("Move Up");
        buttonPriorityUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPriorityUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrioritiesLayout = new javax.swing.GroupLayout(panelPriorities);
        panelPriorities.setLayout(panelPrioritiesLayout);
        panelPrioritiesLayout.setHorizontalGroup(
            panelPrioritiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrioritiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrioritiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(buttonPriorityUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonPriorityDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrioritiesLayout.setVerticalGroup(
            panelPrioritiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrioritiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPriorityUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPriorityDown)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        boxFuel.setModel(getFuels());

        boxFuelType.setModel(getFuelModifiers());

        javax.swing.GroupLayout panelFuelsLayout = new javax.swing.GroupLayout(panelFuels);
        panelFuels.setLayout(panelFuelsLayout);
        panelFuelsLayout.setHorizontalGroup(
            panelFuelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFuelsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxFuel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxFuelType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelFuelsLayout.setVerticalGroup(
            panelFuelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFuelsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFuelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxFuel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boxFuelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Reactor Size");

        spinnerX.setModel(new javax.swing.SpinnerNumberModel(3, 1, null, 1));

        spinnerY.setModel(new javax.swing.SpinnerNumberModel(3, 1, null, 1));

        spinnerZ.setModel(new javax.swing.SpinnerNumberModel(3, 1, null, 1));

        javax.swing.GroupLayout panelSizeLayout = new javax.swing.GroupLayout(panelSize);
        panelSize.setLayout(panelSizeLayout);
        panelSizeLayout.setHorizontalGroup(
            panelSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelSizeLayout.createSequentialGroup()
                        .addComponent(spinnerX, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerY, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerZ, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelSizeLayout.setVerticalGroup(
            panelSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSizeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spinnerThreads.setModel(new javax.swing.SpinnerNumberModel(1, 1, 128, 1));

        jLabel3.setText("Threads:");

        buttonStart.setText("GENERATE");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        buttonStop.setText("ABORT");
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spinnerThreads))
                    .addComponent(buttonStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonStart, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonStop, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        jCheckBox1.setText("Replace air with conductors");

        checkBoxSymmetryX.setText("Apply X Symmetry");

        checkBoxSymmetryY.setText("Apply Y Symmetry");

        checkBoxSymmetryZ.setText("Apply Z Symmetry");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGenPlan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGenModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPriorities, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFuels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxSymmetryX, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxSymmetryY, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxSymmetryZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGenPlan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(panelSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelFuels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelPriorities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxSymmetryX)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxSymmetryY)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxSymmetryZ)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelGenModel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabbedPane.addTab("Generation Settings", jPanel2);

        textAreaOutput.setEditable(false);
        textAreaOutput.setColumns(20);
        textAreaOutput.setRows(5);
        jScrollPane5.setViewportView(textAreaOutput);

        panelOutput.setLayout(new java.awt.GridLayout(1, 0));

        checkBoxDrawReactors.setSelected(true);
        checkBoxDrawReactors.setText("Draw Reactors");
        checkBoxDrawReactors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDrawReactorsActionPerformed(evt);
            }
        });

        checkBoxShowClusters.setSelected(true);
        checkBoxShowClusters.setText("Show clusters");

        buttonExportImage.setText("Export Image");
        buttonExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportImageActionPerformed(evt);
            }
        });

        buttonExportJSON.setText("Export to JSON");
        buttonExportJSON.setToolTipText("Exported JSONs are compatible with Hellrage's Reactor Planner");
        buttonExportJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportJSONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxDrawReactors)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxShowClusters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonExportImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonExportJSON)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxDrawReactors)
                    .addComponent(buttonExportImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonExportJSON)
                    .addComponent(checkBoxShowClusters))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tabbedPane.addTab("Generator Output", jPanel3);

        textAreaImport.setColumns(20);
        textAreaImport.setRows(5);
        textAreaImport.setToolTipText("To import a reactor, paste the contents of the design JSON into this box and click the Import button below");
        textAreaImport.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textAreaImportKeyTyped(evt);
            }
        });
        jScrollPane6.setViewportView(textAreaImport);

        panelImportDisplay.setLayout(new java.awt.GridLayout(1, 0));

        textAreaImportOutput.setEditable(false);
        textAreaImportOutput.setColumns(20);
        textAreaImportOutput.setRows(2);
        textAreaImportOutput.setText("Paste a reactor JSON above to begin");
        jScrollPane7.setViewportView(textAreaImportOutput);

        buttonImport.setText("Import Reactor");
        buttonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportActionPerformed(evt);
            }
        });

        textAreaImportStats.setEditable(false);
        textAreaImportStats.setColumns(20);
        textAreaImportStats.setRows(5);
        jScrollPane8.setViewportView(textAreaImportStats);

        buttonImportExportImage.setText("Export Image");
        buttonImportExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportExportImageActionPerformed(evt);
            }
        });

        buttonImportExportJSON.setText("Export to JSON");
        buttonImportExportJSON.setToolTipText("Exported JSONs are compatible with Hellrage's Reactor Planner");
        buttonImportExportJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportExportJSONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addComponent(buttonImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelImportDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonImportExportImage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonImportExportJSON)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelImportDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonImport)
                    .addComponent(buttonImportExportImage)
                    .addComponent(buttonImportExportJSON))
                .addContainerGap())
        );

        tabbedPane.addTab("Import Reactor", jPanel4);

        jPanel9.setLayout(new java.awt.GridLayout(1, 0));

        buttonConfigDefault.setText("Default");
        buttonConfigDefault.setEnabled(false);
        buttonConfigDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonConfigDefaultActionPerformed(evt);
            }
        });
        jPanel9.add(buttonConfigDefault);

        buttonConfigCustom.setText("Custom");
        buttonConfigCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonConfigCustomActionPerformed(evt);
            }
        });
        jPanel9.add(buttonConfigCustom);

        panelConfigHeatsinks.setLayout(new java.awt.GridLayout(0, 2));

        panelConfigFuels.setMaximumSize(new java.awt.Dimension(1000, 32767));
        panelConfigFuels.setLayout(new java.awt.GridLayout(0, 13));

        panelConfigSources.setLayout(new java.awt.GridLayout(0, 2));

        panelConfigModerators.setLayout(new java.awt.GridLayout(0, 3));

        panelConfigReflectors.setLayout(new java.awt.GridLayout(0, 3));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelConfigHeatsinks, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelConfigSources, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelConfigModerators, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelConfigReflectors, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelConfigFuels, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelConfigFuels, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(panelConfigHeatsinks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelConfigSources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelConfigModerators, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelConfigReflectors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tabbedPane.addTab("Configuration", jPanel8);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelParts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane)
                .addGap(0, 0, 0))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelParts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane))
                .addContainerGap())
        );

        jScrollPane9.setViewportView(jPanel6);

        jButton1.setText("Take me back to the simpler Pre-overhaul times!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane9)
                .addGap(0, 0, 0)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void boxGenModelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxGenModelItemStateChanged
        genModel = GenerationModel.models.get(boxGenModel.getSelectedIndex());
        textAreaGenModelDesc.setText(genModel.description);
        panelGenModelSettings.removeAll();
        genModel.fillSettings(panelGenModelSettings);
        repaint();
    }//GEN-LAST:event_boxGenModelItemStateChanged
    private void boxGenPlanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxGenPlanItemStateChanged
        genPlan = GenerationPlan.plans.get(boxGenPlan.getSelectedIndex());
        textAreaGenPlanDesc.setText(genPlan.description);
        panelGenPlanSettings.removeAll();
        genPlan.fillSettings(panelGenPlanSettings);
        repaint();
    }//GEN-LAST:event_boxGenPlanItemStateChanged
    private void buttonPriorityUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPriorityUpActionPerformed
        int index = listPriorities.getSelectedIndex();
        if(index==0)return;
        Priority.priorities.add(index-1, Priority.priorities.remove(index));
        listPriorities.setModel(getPrioritiesModel());
        listPriorities.setSelectedIndex(index-1);
    }//GEN-LAST:event_buttonPriorityUpActionPerformed
    private void buttonPriorityDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPriorityDownActionPerformed
        int index = listPriorities.getSelectedIndex();
        if(index==listPriorities.getModel().getSize()-1)return;
        Priority.priorities.add(index+1, Priority.priorities.remove(index));
        listPriorities.setModel(getPrioritiesModel());
        listPriorities.setSelectedIndex(index+1);
    }//GEN-LAST:event_buttonPriorityDownActionPerformed
    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        start();
    }//GEN-LAST:event_buttonStartActionPerformed
    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
        stop();
    }//GEN-LAST:event_buttonStopActionPerformed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        stop();
        dispose();
        underhaul.Main.main(new String[0]);
    }//GEN-LAST:event_jButton1ActionPerformed
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stop();
    }//GEN-LAST:event_formWindowClosing
    private void textAreaImportKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaImportKeyTyped
        new Thread(() -> {
            try{
                Thread.sleep(10);//The event is called before the character is added to the box
            }catch(InterruptedException ex){}
            Reactor reactor = getImportReactor();
            panelImportDisplay.removeAll();
            panelImportDisplay.add(new ReactorPanel(reactor));
            if(reactor==null)return;
            textAreaImportStats.setText(reactor.getDetails(true, false));
            repaint();
        }).start();
    }//GEN-LAST:event_textAreaImportKeyTyped
    private void buttonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportActionPerformed
        Reactor r = getImportReactor();
        if(r==null)return;
        genPlan.importReactor(getImportReactor(), running);
        textAreaImportOutput.setText("Imported!");
        updateDisplay();
    }//GEN-LAST:event_buttonImportActionPerformed
    private void checkBoxDrawReactorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDrawReactorsActionPerformed
        if(!checkBoxDrawReactors.isSelected()){
            panelOutput.removeAll();
            repaint();
        }
    }//GEN-LAST:event_checkBoxDrawReactorsActionPerformed
    private void buttonExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportImageActionPerformed
        ArrayList<Reactor> reactors = genPlan.getReactors();
        if(reactors.isEmpty())return;
        Exporter.export(new ReactorPanel(reactors.get(0)).getImage());
    }//GEN-LAST:event_buttonExportImageActionPerformed
    private void buttonExportJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportJSONActionPerformed
        ArrayList<Reactor> reactors = genPlan.getReactors();
        if(reactors.isEmpty())return;
        Exporter.export(reactors.get(0).exportJSON());
    }//GEN-LAST:event_buttonExportJSONActionPerformed
    private void buttonImportExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportExportImageActionPerformed
        Exporter.export(new ReactorPanel(getImportReactor()).getImage());
    }//GEN-LAST:event_buttonImportExportImageActionPerformed
    private void buttonImportExportJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportExportJSONActionPerformed
        Exporter.export(getImportReactor().exportJSON());
    }//GEN-LAST:event_buttonImportExportJSONActionPerformed
    private void buttonConfigDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConfigDefaultActionPerformed
        if(Configuration.load(Configuration.DEFAULT)){
            buttonConfigDefault.setEnabled(false);
            buttonConfigCustom.setEnabled(true);
            fillConfigPanels(Configuration.DEFAULT);
        }
    }//GEN-LAST:event_buttonConfigDefaultActionPerformed
    private void buttonConfigCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConfigCustomActionPerformed
        if(Configuration.load(Configuration.CUSTOM)){
            buttonConfigDefault.setEnabled(true);
            buttonConfigCustom.setEnabled(false);
            panelConfigHeatsinks.removeAll();
            panelConfigFuels.removeAll();
            panelConfigModerators.removeAll();
            panelConfigReflectors.removeAll();
            panelConfigSources.removeAll();
            panelConfigHeatsinks.add(new JLabel("Heatsink"));
            panelConfigHeatsinks.add(new JLabel("Cooling"));
            panelConfigModerators.add(new JLabel("Moderator"));
            panelConfigModerators.add(new JLabel("Efficiency"));
            panelConfigModerators.add(new JLabel("Flux Factor"));
            panelConfigReflectors.add(new JLabel("Reflector"));
            panelConfigReflectors.add(new JLabel("Reflectivity"));
            panelConfigReflectors.add(new JLabel("Efficiency"));
            panelConfigSources.add(new JLabel("Source"));
            panelConfigSources.add(new JLabel("Efficiency"));
            for(ReactorPart part : ReactorPart.parts){
                if(part instanceof Heatsink){
                    panelConfigHeatsinks.add(new JLabel(part.toString().replace(" Heatsink", "")));
                    JSpinner spinner = new JSpinner(new SpinnerNumberModel((int)Configuration.CUSTOM.cooling.get((Heatsink)part), 0, Integer.MAX_VALUE, 1));
                    spinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.cooling.put((Heatsink)part, (Integer) spinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigHeatsinks.add(spinner);
                }
                if(part instanceof Moderator){
                    panelConfigModerators.add(new JLabel(part.toString().replace(" Moderator", "")));
                    JSpinner effSpinner = new JSpinner(new SpinnerNumberModel((double)Configuration.CUSTOM.modEff.get((Moderator)part), 0, Double.MAX_VALUE, .01));
                    effSpinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.modEff.put((Moderator)part, (float) effSpinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigModerators.add(effSpinner);
                    JSpinner fluxSpinner = new JSpinner(new SpinnerNumberModel((int)Configuration.CUSTOM.modFlux.get((Moderator)part), 0, Integer.MAX_VALUE, 1));
                    fluxSpinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.modFlux.put((Moderator)part, (int) fluxSpinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigModerators.add(fluxSpinner);
                }
                if(part instanceof Reflector){
                    panelConfigReflectors.add(new JLabel(part.toString().replace(" Reflector", "")));
                    JSpinner refSpinner = new JSpinner(new SpinnerNumberModel((double)Configuration.CUSTOM.refRef.get((Reflector)part), 0, Double.MAX_VALUE, .01));
                    refSpinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.refRef.put((Reflector)part, (float) refSpinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigReflectors.add(refSpinner);
                    JSpinner effSpinner = new JSpinner(new SpinnerNumberModel((double)Configuration.CUSTOM.refEff.get((Reflector)part), 0, Double.MAX_VALUE, .01));
                    effSpinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.refEff.put((Reflector)part, (float) effSpinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigReflectors.add(effSpinner);
                }
                if(part instanceof FuelCell){
                    if(((FuelCell)part).efficiency==0)continue;
                    String name = part.toString();
                    name = name.substring(name.indexOf("(")+1, name.length()-1);
                    panelConfigSources.add(new JLabel(name));
                    JSpinner effSpinner = new JSpinner(new SpinnerNumberModel((double)Configuration.CUSTOM.sourceEff.get((FuelCell)part), 0, Double.MAX_VALUE, .01));
                    effSpinner.addChangeListener((e) -> {
                        Configuration.CUSTOM.sourceEff.put((FuelCell)part, (float) effSpinner.getValue());
                        if(!buttonConfigCustom.isEnabled())Configuration.load(Configuration.CUSTOM);
                    });
                    panelConfigSources.add(effSpinner);
                }
            }
            panelConfigFuels.add(new JLabel("Fuel"));
            for(Fuel.Type type : Fuel.Type.values()){
                panelConfigFuels.add(new JLabel("["+type.name()+"] Eff."));
                panelConfigFuels.add(new JLabel("["+type.name()+"] Heat"));
                panelConfigFuels.add(new JLabel("["+type.name()+"] Critica."));
            }
            for(Fuel fuel : Configuration.CUSTOM.fuels){
                panelConfigFuels.add(new JLabel(fuel.toString()));
                for(Fuel.Type type : Fuel.Type.values()){
                    JSpinner effSpinner = new JSpinner(new SpinnerNumberModel(fuel.efficiency.get(type).doubleValue(), 0, Double.MAX_VALUE, .01));
                    effSpinner.addChangeListener((e) -> {
                        fuel.efficiency.put(type, (double) effSpinner.getValue());
                    });
                    panelConfigFuels.add(effSpinner);
                    
                    JSpinner heatSpinner = new JSpinner(new SpinnerNumberModel(fuel.heat.get(type).doubleValue(), 0, Double.MAX_VALUE, .01));
                    heatSpinner.addChangeListener((e) -> {
                        fuel.heat.put(type, (double) heatSpinner.getValue());
                    });
                    panelConfigFuels.add(heatSpinner);
                    
                    JSpinner criticalitySpinner = new JSpinner(new SpinnerNumberModel(fuel.criticality.get(type).intValue(), 0, Integer.MAX_VALUE, 1));
                    criticalitySpinner.addChangeListener((e) -> {
                        fuel.criticality.put(type, (int) criticalitySpinner.getValue());
                    });
                    panelConfigFuels.add(criticalitySpinner);
                }
            }
            for(Component c : panelConfigHeatsinks.getComponents()){
                c.setEnabled(!running);
            }
            for(Component c : panelConfigModerators.getComponents()){
                c.setEnabled(!running);
            }
            for(Component c : panelConfigReflectors.getComponents()){
                c.setEnabled(!running);
            }
            for(Component c : panelConfigSources.getComponents()){
                c.setEnabled(!running);
            }
            for(Component c : panelConfigFuels.getComponents()){
                c.setEnabled(!running);
            }
        }
    }//GEN-LAST:event_buttonConfigCustomActionPerformed
    private Reactor getImportReactor(){
        Fuel fuel = Fuel.fuels.get(boxFuel.getSelectedIndex());
        Fuel.Type type = Fuel.Type.values()[boxFuelType.getSelectedIndex()];
        int x = (int) spinnerX.getValue();
        int y = (int) spinnerY.getValue();
        int z = (int) spinnerZ.getValue();
        return Reactor.parse(textAreaImport, fuel, type, x, y, z);
    }
    public static void main(String args[]){
        Configuration.load(Configuration.DEFAULT);
        if(args.length>1&&args[0].replace("_", " ").replace(" ", "").equalsIgnoreCase("discord")){
            BotInterface.main(args);
            return;
        }
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run(){
                instance = new Main();
                instance.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox<String> boxFuel;
    public javax.swing.JComboBox<String> boxFuelType;
    private javax.swing.JComboBox<String> boxGenModel;
    private javax.swing.JComboBox<String> boxGenPlan;
    private javax.swing.JButton buttonConfigCustom;
    private javax.swing.JButton buttonConfigDefault;
    private javax.swing.JButton buttonExportImage;
    private javax.swing.JButton buttonExportJSON;
    private javax.swing.JButton buttonImport;
    private javax.swing.JButton buttonImportExportImage;
    private javax.swing.JButton buttonImportExportJSON;
    private javax.swing.JButton buttonPriorityDown;
    private javax.swing.JButton buttonPriorityUp;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxDrawReactors;
    public javax.swing.JCheckBox checkBoxShowClusters;
    public javax.swing.JCheckBox checkBoxSymmetryX;
    public javax.swing.JCheckBox checkBoxSymmetryY;
    public javax.swing.JCheckBox checkBoxSymmetryZ;
    private javax.swing.JButton jButton1;
    public javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel labelParts;
    public javax.swing.JList<String> listParts;
    private javax.swing.JList<String> listPriorities;
    private javax.swing.JPanel panelConfigFuels;
    private javax.swing.JPanel panelConfigHeatsinks;
    private javax.swing.JPanel panelConfigModerators;
    private javax.swing.JPanel panelConfigReflectors;
    private javax.swing.JPanel panelConfigSources;
    private javax.swing.JPanel panelFuels;
    private javax.swing.JPanel panelGenModel;
    private javax.swing.JPanel panelGenModelSettings;
    private javax.swing.JPanel panelGenPlan;
    private javax.swing.JPanel panelGenPlanSettings;
    private javax.swing.JPanel panelImportDisplay;
    private javax.swing.JPanel panelOutput;
    private javax.swing.JPanel panelParts;
    private javax.swing.JPanel panelPriorities;
    private javax.swing.JPanel panelSize;
    private javax.swing.JSpinner spinnerThreads;
    public javax.swing.JSpinner spinnerX;
    public javax.swing.JSpinner spinnerY;
    public javax.swing.JSpinner spinnerZ;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaGenModelDesc;
    private javax.swing.JTextArea textAreaGenPlanDesc;
    private javax.swing.JTextArea textAreaImport;
    public javax.swing.JTextArea textAreaImportOutput;
    private javax.swing.JTextArea textAreaImportStats;
    private javax.swing.JTextArea textAreaOutput;
    // End of variables declaration//GEN-END:variables
    private ListModel<String> getReactorPartsModel(){
        return new ListModel<String>(){
            @Override
            public int getSize(){
                return ReactorPart.parts.size();
            }
            @Override
            public String getElementAt(int index){
                return ReactorPart.parts.get(index).toString();
            }
            @Override
            public void addListDataListener(ListDataListener l){}
            @Override
            public void removeListDataListener(ListDataListener l){}
        };
    }
    private ListModel<String> getPrioritiesModel(){
        return new ListModel<String>(){
            @Override
            public int getSize(){
                return Priority.priorities.size();
            }
            @Override
            public String getElementAt(int index){
                return Priority.priorities.get(index).toString();
            }
            @Override
            public void addListDataListener(ListDataListener l){}
            @Override
            public void removeListDataListener(ListDataListener l){}
        };
    }
    private int[] generateSelectedParts(){
        int[] parts = new int[ReactorPart.parts.size()];
        for(int i = 0; i<parts.length; i++){
            parts[i] = i;
        }
        return parts;
    }
    private ComboBoxModel<String> getGenerationModels(){
        String[] strs = new String[GenerationModel.models.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = GenerationModel.models.get(i).toString();
        }
        return new DefaultComboBoxModel<>(strs);
    }
    private ComboBoxModel<String> getGenerationPlans(){
        String[] strs = new String[GenerationPlan.plans.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = GenerationPlan.plans.get(i).toString();
        }
        return new DefaultComboBoxModel<>(strs);
    }
    public ComboBoxModel<String> getFuels(){
        String[] strs = new String[Fuel.fuels.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = Fuel.fuels.get(i).toString();
        }
        return new DefaultComboBoxModel<>(strs);
    }
    public ComboBoxModel<String> getFuelModifiers(){
        String[] strs = new String[Fuel.Type.values().length];
        for(int i = 0; i<strs.length; i++){
            strs[i] = Fuel.Type.values()[i].toString();
        }
        return new DefaultComboBoxModel<>(strs);
    }
    public static boolean running = false;
    private static final Object synchronizer = new Object();//This can't be duplicated... right? RIGHT?
    int activeThreads = 0;
    int iterations = 0;
    public void start(){
        for(Component c : panelConfigHeatsinks.getComponents()){
            c.setEnabled(false);
        }
        for(Component c : panelConfigModerators.getComponents()){
            c.setEnabled(false);
        }
        for(Component c : panelConfigReflectors.getComponents()){
            c.setEnabled(false);
        }
        for(Component c : panelConfigSources.getComponents()){
            c.setEnabled(false);
        }
        for(Component c : panelConfigFuels.getComponents()){
            c.setEnabled(false);
        }
        boxGenPlan.setEnabled(false);
        spinnerX.setEnabled(false);
        spinnerY.setEnabled(false);
        spinnerZ.setEnabled(false);
        boxFuel.setEnabled(false);
        boxFuelType.setEnabled(false);
        spinnerThreads.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        Fuel fuel = Fuel.fuels.get(boxFuel.getSelectedIndex());
        Fuel.Type type = Fuel.Type.values()[boxFuelType.getSelectedIndex()];
        int x = (int) spinnerX.getValue();
        int y = (int) spinnerY.getValue();
        int z = (int) spinnerZ.getValue();
        genPlan.reset(fuel,type,x,y,z);
        Reactor.totalReactors = 0;
        iterations = 0;
        Reactor.startTime = System.nanoTime();
        running = true;
        for(int i = 0; i<(int)spinnerThreads.getValue(); i++){
            startGenerationThread();
        }
        startDisplayThread();
    }
    public void stop(){
        for(Component c : panelConfigHeatsinks.getComponents()){
            c.setEnabled(!buttonConfigCustom.isEnabled());
        }
        for(Component c : panelConfigModerators.getComponents()){
            c.setEnabled(!buttonConfigCustom.isEnabled());
        }
        for(Component c : panelConfigReflectors.getComponents()){
            c.setEnabled(!buttonConfigCustom.isEnabled());
        }
        for(Component c : panelConfigSources.getComponents()){
            c.setEnabled(!buttonConfigCustom.isEnabled());
        }
        for(Component c : panelConfigFuels.getComponents()){
            c.setEnabled(!buttonConfigCustom.isEnabled());
        }
        buttonStop.setEnabled(false);
        running = false;
        startShutdownThread();
    }
    private void startGenerationThread(){
        activeThreads++;
        Thread t = new Thread(() -> {
            Fuel fuel = Fuel.fuels.get(boxFuel.getSelectedIndex());
            Fuel.Type type = Fuel.Type.values()[boxFuelType.getSelectedIndex()];
            int x = (int) spinnerX.getValue();
            int y = (int) spinnerY.getValue();
            int z = (int) spinnerZ.getValue();
            Random rand = new Random();
            while(true){
                synchronized(synchronizer){
                    if(!running)break;
                }
                genPlan.run(fuel, type, x, y, z, rand);
                synchronized(synchronizer){
                    iterations++;
                }
            }
            synchronized(synchronizer){
                activeThreads--;
            }
        }, "Generation Thread "+activeThreads);
        t.setDaemon(true);
        t.start();
    }
    private void startDisplayThread(){
        Thread t = new Thread(() -> {
            while(running||activeThreads>0){
                try{
                    updateDisplay();
                    Thread.sleep(100);
                }catch(InterruptedException ex){
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "Display Thread");
        t.setDaemon(true);
        t.start();
    }
    private void updateDisplay(){
        long time = System.nanoTime()-Reactor.startTime;
        ArrayList<Reactor> reactors = genPlan.getReactors();
        String text = "Time: "+toTime(time)+"\n"
                + "Iterations: "+iterations+"\n"
                + "Reactors processed: "+Reactor.totalReactors+"\n"
                + "Reactors per second: "+Math.round(Reactor.totalReactors/(time/1_000_000_000d)*10)/10d+"\n"
                + genPlan.getDetails(reactors);
        textAreaOutput.setText(text);
        if(checkBoxDrawReactors.isSelected()){
            panelOutput.removeAll();
            for(Reactor r : reactors){
                panelOutput.add(new ReactorPanel(r));
            }
            repaint();
        }
    }
    private void startShutdownThread(){
        Thread t = new Thread(() -> {
            while(activeThreads>0){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ex){
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            boxGenPlan.setEnabled(true);
            spinnerX.setEnabled(true);
            spinnerY.setEnabled(true);
            spinnerZ.setEnabled(true);
            boxFuel.setEnabled(true);
            boxFuelType.setEnabled(true);
            spinnerThreads.setEnabled(true);
            buttonStart.setEnabled(true);
        }, "Shutdown Thread");
        t.setDaemon(true);
        t.start();
    }
    public void setAllowedBlocks(ArrayList<ReactorPart> allowedBlocks){
        int[] allowed = new int[allowedBlocks.size()];
        for(int i = 0; i<allowed.length; i++){
            allowed[i] = ReactorPart.parts.indexOf(allowedBlocks.get(i));
        }
        listParts.setSelectedIndices(allowed);
    }
    private void fillConfigPanels(Configuration config){
        panelConfigHeatsinks.removeAll();
        panelConfigFuels.removeAll();
        panelConfigModerators.removeAll();
        panelConfigReflectors.removeAll();
        panelConfigSources.removeAll();
        panelConfigHeatsinks.add(new JLabel("Heatsink"));
        panelConfigHeatsinks.add(new JLabel("Cooling"));
        panelConfigModerators.add(new JLabel("Moderator"));
        panelConfigModerators.add(new JLabel("Efficiency"));
        panelConfigModerators.add(new JLabel("Flux Factor"));
        panelConfigReflectors.add(new JLabel("Reflector"));
        panelConfigReflectors.add(new JLabel("Reflectivity"));
        panelConfigReflectors.add(new JLabel("Efficiency"));
        panelConfigSources.add(new JLabel("Source"));
        panelConfigSources.add(new JLabel("Efficiency"));
        for(ReactorPart part : ReactorPart.parts){
            if(part instanceof Heatsink){
                panelConfigHeatsinks.add(new JLabel(part.toString().replace(" Heatsink", "")));
                panelConfigHeatsinks.add(new JLabel(config.cooling.get((Heatsink)part)+""));
            }
            if(part instanceof Moderator){
                panelConfigModerators.add(new JLabel(part.toString().replace(" Moderator", "")));
                panelConfigModerators.add(new JLabel(config.modEff.get((Moderator)part)+""));
                panelConfigModerators.add(new JLabel(config.modFlux.get((Moderator)part)+""));
            }
            if(part instanceof Reflector){
                panelConfigReflectors.add(new JLabel(part.toString().replace(" Reflector", "")));
                panelConfigReflectors.add(new JLabel(config.refRef.get((Reflector)part)+""));
                panelConfigReflectors.add(new JLabel(config.refEff.get((Reflector)part)+""));
            }
            if(part instanceof FuelCell){
                if(((FuelCell)part).efficiency==0)continue;
                String name = part.toString();
                name = name.substring(name.indexOf("(")+1, name.length()-1);
                panelConfigSources.add(new JLabel(name));
                panelConfigSources.add(new JLabel(config.sourceEff.get((FuelCell)part)+""));
            }
        }
        panelConfigFuels.add(new JLabel("Fuel"));
        for(Fuel.Type type : Fuel.Type.values()){
            panelConfigFuels.add(new JLabel("["+type.name()+"] Eff."));
            panelConfigFuels.add(new JLabel("["+type.name()+"] Heat"));
            panelConfigFuels.add(new JLabel("["+type.name()+"] Critica."));
        }
        for(Fuel fuel : config.fuels){
            panelConfigFuels.add(new JLabel(fuel.toString()));
            for(Fuel.Type type : Fuel.Type.values()){
                panelConfigFuels.add(new JLabel(fuel.efficiency.get(type)+""));
                panelConfigFuels.add(new JLabel(fuel.heat.get(type)+""));
                panelConfigFuels.add(new JLabel((int)fuel.criticality.get(type)+""));
            }
        }
    }
}

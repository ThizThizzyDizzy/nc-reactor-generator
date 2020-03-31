/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author James
 */
public class Exporter extends javax.swing.JFrame{
    BufferedImage image = null;
    String str = null;
    public static void export(Object obj){
        new Exporter().exp(obj).setVisible(true);
    }
    private Exporter exp(Object obj){
        if(obj!=null&&(obj instanceof BufferedImage)){
            image = (BufferedImage)obj;
            JPanel imagePanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g){
                    g.setColor(Color.white);
                    float sizeFac = 16;
                    sizeFac = Math.min(sizeFac, getWidth()/(float)image.getWidth());
                    sizeFac = Math.min(sizeFac, getHeight()/(float)image.getHeight());
                    sizeFac = Math.max(1,sizeFac);
                    int width = (int) (image.getWidth()*sizeFac);
                    int height = (int) (image.getHeight()*sizeFac);
                    g.drawImage(image, 0,0,width,height,getBackground(),null);
                }
            };
            outputPanel.add(imagePanel);
        }else{
            str = Objects.toString(obj);
            JTextArea area = new JTextArea(str);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setEditable(false);
            outputPanel.add(area);
        }
        repaint();
        return this;
    }
    public Exporter(){
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outputPanel = new javax.swing.JPanel();
        buttonSaveFile = new javax.swing.JButton();
        buttonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export Window");
        setResizable(false);

        outputPanel.setLayout(new java.awt.GridLayout(1, 0));

        buttonSaveFile.setText("Save File");
        buttonSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveFileActionPerformed(evt);
            }
        });

        buttonClose.setText("Close Window");
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonSaveFile)
                        .addGap(18, 18, 18)
                        .addComponent(buttonClose))
                    .addComponent(outputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSaveFile)
                    .addComponent(buttonClose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        dispose();
    }//GEN-LAST:event_buttonCloseActionPerformed
    private void buttonSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(image!=null?"PNG Image File":"JSON File", image!=null?"png":"json");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            File f = chooser.getSelectedFile();
            if(!f.getName().contains(".")){
                f = new File(f.getAbsolutePath()+"."+(image!=null?".png":".json"));
            }
            try{
                if(image!=null){
                    ImageIO.write(image, "png", f);
                }else{
                    try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {
                        writer.write(str);
                    }
                }
            }catch(IOException ex){
                JOptionPane.showMessageDialog(this, "Caught "+ex.getClass().getName()+": "+ex.getMessage()+"!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_buttonSaveFileActionPerformed
    public static void main(String args[]){
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){
            java.util.logging.Logger.getLogger(Exporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run(){
                new Exporter().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonSaveFile;
    private javax.swing.JPanel outputPanel;
    // End of variables declaration//GEN-END:variables
}
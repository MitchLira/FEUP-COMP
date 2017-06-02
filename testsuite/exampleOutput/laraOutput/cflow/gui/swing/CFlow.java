package gui.swing;




import weaver.gui.KadabraLauncher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CFlow {

    JFileChooser fc;
    private JButton startButton;
    private JPanel panel;
    private JTextField regexField;
    private JButton srcFolder;
    private JTextField srcField;
    private JButton chooseOutputButton;
    private JTextField srcOut;


    public CFlow() {
        initGui();
    }

    private void initGui(){
        JFrame frame = new JFrame("CFlow");
        frame.setBounds(100, 100, 700, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setVisible(true);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setCurrentDirectory(new java.io.File("."));

        srcFolder.addActionListener((ActionEvent actionEvent) -> {

                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    srcField.setText(fc.getSelectedFile().toString());
                }
        });


        chooseOutputButton.addActionListener((ActionEvent actionEvent) -> {

                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    srcOut.setText(fc.getSelectedFile().toString());
                }

        });


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                try {

                    File srcDir = new File("src");
                    File outDir = new File(srcOut.getText()+"/cflow");
                    FileUtils.copyDirectory(srcDir,outDir);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] args = new String[5];
                args[0] = "src/lara/cflow.lara";
                args[1] = "-p";
                args[2] = srcField.getText();
                args[3] = "-o";
                args[4] = srcOut.getText();

                KadabraLauncher.main(args);

            }
        });



    }



    public  static void main(String [] args) throws Exception {


        CFlow cflow = new CFlow();

        //compile

    }

}

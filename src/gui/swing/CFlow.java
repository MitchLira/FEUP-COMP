package gui.swing;


import weaver.gui.KadabraLauncher;

import javax.swing.*;
import java.awt.event.ActionEvent;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

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


        startButton.addActionListener((ActionEvent actionEvent) -> {

                try {
                    File src = new File("out/artifacts/Cflow_jar/cflow.jar");
                    File dst = new File(srcOut.getText() + File.separator + src.getName());
                    //FileUtils.copyDirectory(srcDir,outDir);
                    Files.copy(src.toPath(), dst.toPath() , StandardCopyOption.REPLACE_EXISTING);

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

            });

    }



    public  static void main(String [] args) throws Exception {
        //To compile a java project:
            //javac -d bin -Xlint $(find . -name \*.java) -cp laraOutput/cflow.jar
        //To run with jar
            //java -cp .:../laraOutput/cflow.jar p1.pt

        CFlow cflow = new CFlow();


    }

}

package gui.swing;




import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;


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

    private void initGui() {
        JFrame frame = new JFrame("CFlow");
        frame.setBounds(100, 100, 700, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setVisible(true);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setCurrentDirectory(new File("."));

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

            utils.Utils.generateParsedCode(srcField.getText(),srcOut.getText());

        });

    }


    public static void main(String[] args) throws Exception {
        //To compile a java project:
        //javac -d bin -Xlint $(find . -name \*.java) -cp laraOutput/cflow.jar
        //To run with jar
        //java -cp .:../laraOutput/cflow.jar p1.pt

        CFlow cflow = new CFlow();

    }

}

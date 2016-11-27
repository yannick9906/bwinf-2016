package tk.yannickfelix.bwinf2016.task2;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <b>Aufgabe 2 Bundeswettbewerb Informatik 2016/17</b>
 * Just a simple GUI Class
 *
 * @author Yannick Félix
 * @since 04.09.2016
 * @version 1.0
 */
public class Frame extends JFrame {

    private JButton loadButton, saveButton;
    private JProgressBar progressBar;
    private JLabel info, preview, status;
    private ImageProcessor imageProcessor;
    private JSlider smootingSlider;

    public Frame() {
        //Some simple JFrame setup
        super("Bundeswettbewerb Informatik 2016 - Aufgabe 2");
        Toolkit t = Toolkit.getDefaultToolkit();

        //Place window in the middle of the screen
        Dimension d = t.getScreenSize();
        int x = (int) ((d.getWidth() -  600) / 2);
        int y = (int) ((d.getHeight() - 300) / 2);

        //Configure window
        setBounds(x,y, 600, 300);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Set up components and listeners
        initComponents();
        initListeners();

        //And open window
        setVisible(true);
    }

    /**
     * This Method initializes all components the frame has
     */
    private void initComponents() {
        //Construct Components
        this.loadButton = new JButton("Datei öffnen");
        this.saveButton = new JButton("Datei speichern");
        this.progressBar = new JProgressBar(0,1);
        this.info = new JLabel("Glättung:");
        this.status = new JLabel("Copyright Yannick Félix 2016 - Some Rights Reserved");
        this.preview = new JLabel("");
        this.smootingSlider = new JSlider(1,20,1);
        this.imageProcessor = new ImageProcessor();

        //Add them to the content pane
        this.getContentPane().add(this.loadButton);
        this.getContentPane().add(this.saveButton);
        this.getContentPane().add(this.progressBar);
        this.getContentPane().add(this.info);
        this.getContentPane().add(this.status);
        this.getContentPane().add(this.preview);
        this.getContentPane().add(this.smootingSlider);

        //Style them
        this.loadButton.setBounds(10, 230, 130, 20);
        this.saveButton.setBounds(150, 230, 130, 20);
        this.info.setBounds(10,170,50,20);
        this.smootingSlider.setBounds(70,172, 215, 20);
        this.progressBar.setBounds(310, 230, 260, 20);
        this.status.setBounds(15, 200, 260, 20);
        this.preview.setBounds(310, 10, 260, 220);
    }

    /**
     * This method initializes all Listneners
     */
    private void initListeners() {
        //'Load'-Button OnClick Listener
        this.loadButton.addActionListener(e -> {
            status.setText("Warte auf Bild...");
            //Open FileChooser Dialog
            JFileChooser filechoose = new JFileChooser();
            filechoose.setAcceptAllFileFilterUsed(false);

            //Only accept Pictures
            filechoose.setFileFilter(new FileNameExtensionFilter("Bild-Dateien", "png", "jpg", "bmp", "ppm"));
            int returnVal = filechoose.showOpenDialog(getContentPane());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //If a file has been chosen
                File file = filechoose.getSelectedFile();

                //Start processing in a new thread to keep the UI running
                Thread t = new Thread(() -> {
                    try {
                        status.setText("Öffne Bild...");
                        //First load it
                        imageProcessor.loadImage(file);
                    } catch (IOException e1) {
                        status.setText("Fehler beim Öffnen des Bildes.");
                    }

                    //Then process it
                    imageProcessor.processImage(smootingSlider.getValue(), progressBar, status);
                    //And show it (StretchIcon is used to keep the ratio)
                    preview.setIcon(new StretchIcon(imageProcessor.getProcessedImg(), true));
                });
                t.start();

            } else {
                status.setText("Kein Bild ausgewählt.");
            }
        });

        //'Save'-Button OnClick Listener
        this.saveButton.addActionListener(e -> {
            status.setText("Warte auf Pfad...");
            //Open FileChooser Dialog
            JFileChooser filechoose = new JFileChooser();
            filechoose.setAcceptAllFileFilterUsed(false);

            //Only accept Pictures
            filechoose.setFileFilter(new FileNameExtensionFilter("Bild-Dateien", "png", "jpg", "bmp", "ppm"));
            int returnVal = filechoose.showSaveDialog(getContentPane());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //If a file has been chosen
                File file = filechoose.getSelectedFile();

                //save in a new thread to keep the UI running
                Thread t = new Thread(() -> {
                    try {
                        status.setText("Speichern...");
                        //save the image
                        imageProcessor.saveImage(file);
                        status.setText("Bild gespeichert.");
                    } catch (IOException e1) {
                        status.setText("Fehler beim Speichern des Bildes.");
                    }
                });
                t.start();

            } else {
                status.setText("Keinen Pfad ausgewählt.");
            }
        });

        //Smoothing-Slider OnChange Listener
        this.smootingSlider.addChangeListener(e -> new Thread(() -> {
            //Only reprocess if an image has been opened.
            if(imageProcessor.isLoaded()) {
                //Then process it
                imageProcessor.processImage(smootingSlider.getValue(), progressBar, status);
                //And show it (StretchIcon is used to keep the ratio)
                preview.setIcon(new StretchIcon(imageProcessor.getProcessedImg(), true));
            }
        }).start());
    }
}

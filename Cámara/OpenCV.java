package Cámara;

import  org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenCV extends JFrame {

    public   JLabel pantallaCamara;
    public JButton btnCapture;
    public VideoCapture capture;
    public Mat image;
    private boolean clicked = false;
    public OpenCV(){

        setLayout(null);

        pantallaCamara = new JLabel();
        pantallaCamara.setBounds(0, 0, 640, 480);
        add(pantallaCamara);

        btnCapture = new JButton("Capturar");
        btnCapture.setBounds(300, 480, 80, 40);
        add(btnCapture);

        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                capture.release();
                image.release();
                System.exit(0);
            }
        });

        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startCamera(){
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;
        ImageIcon icon;


        while(true){
            capture.read(image);
            final MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);

            imageData = buf.toArray();
            icon = new ImageIcon(imageData);
            pantallaCamara.setIcon(icon);

            if(clicked) {
                String name = JOptionPane.showInputDialog(this, "Nombre de Imagen");
                if (name==null){
                    name = new SimpleDateFormat("yyyy-mm-dd-mm-ss").format(new Date());
                }
                Imgcodecs.imwrite("images/"+name+".jpg", image);

                clicked = false;

            }

        }

    }

    public static void main (String args[]){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                OpenCV camara = new OpenCV();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camara.startCamera();
                    }
                }).start();
            }
        });

    }
}

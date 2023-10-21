package Cliente;

import org.opencv.core.Core;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Esta clase representa una aplicación de cámara que utiliza OpenCV para capturar y guardar imágenes.
 */
public class OpenCV extends JFrame {

    /** Etiqueta para mostrar la vista de la cámara. */
    public JLabel pantallaCamara;

    /** Botón para capturar una foto. */
    public JButton btnCapture;

    /** Botón para salir de la aplicación. */
    public JButton btnSalir;

    /** Objeto para capturar video desde la cámara. */
    public VideoCapture capture;

    /** Matriz para almacenar la imagen de la cámara. */
    public Mat image;

    /** Variable para verificar si se hizo clic en el botón de captura. */
    private boolean clicked = false;
    private String lastImageName = null;
    private ArrayList<String> savedImageNames = new ArrayList<>();

    /**
     * Constructor de la clase OpenCV.
     * Inicializa la interfaz gráfica de usuario y los elementos de la cámara.
     */
    public OpenCV() {
        // Configuración de la interfaz gráfica de usuario
        setLayout(null);
        pantallaCamara = new JLabel();
        pantallaCamara.setBounds(0, 0, 640, 480);
        add(pantallaCamara);
        btnCapture = new JButton("Foto");
        btnCapture.setBounds(200, 480, 80, 40);
        add(btnCapture);
        btnSalir = new JButton("Salir");
        btnSalir.setBounds(400, 480, 80, 40);
        add(btnSalir);

        // Acción para salir de la aplicación
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (capture != null) {
                    capture.release();
                }
                if (image != null) {
                    image.release();
                }
                dispose();
            }
        });

        // Acción para capturar una foto
        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });

        // Manejador de eventos de cierre de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (capture != null) {
                    capture.release();
                }
                if (image != null) {
                    image.release();
                }
                dispose();
            }
        });

        // Configuración de la ventana principal
        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Inicia la captura de video desde la cámara y muestra la vista en la ventana.
     * Permite capturar una foto cuando se hace clic en el botón correspondiente.
     */
    public void startCamera() {
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;
        ImageIcon icon;

        while (true) {
            capture.read(image);
            final MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);

            imageData = buf.toArray();
            icon = new ImageIcon(imageData);
            pantallaCamara.setIcon(icon);

            if (clicked) {
                System.out.println("Botón 'Foto' presionado.");

                String newName = JOptionPane.showInputDialog(this, "Nombre de Imagen");
                if (newName == null || newName.isEmpty()) {
                    newName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                }

                // Si hay una imagen anterior, eliminarla
                if (lastImageName != null) {
                    System.out.println("Intentando eliminar imagen anterior: " + lastImageName);

                    File previousFile = new File("images/" + lastImageName + ".jpg");
                    if (previousFile.exists()) {
                        boolean deleted = previousFile.delete();
                        if (deleted) {
                            System.out.println("Imagen anterior eliminada correctamente.");
                        } else {
                            System.out.println("Error al eliminar la imagen anterior.");
                        }
                    } else {
                        System.out.println("La imagen anterior no existe.");
                    }
                } else {
                    System.out.println("No hay imagen anterior para eliminar.");
                }

                // Guarda la nueva imagen
                Imgcodecs.imwrite("images/" + newName + ".jpg", image);
                deleteOldestImage();
                sendImageToServer(image);

                clicked = false;
            }
        }
    }

    /**
     * Envía una imagen al servidor a través de un socket.
     * @param image La imagen a enviar.
     */
    private void sendImageToServer(Mat image) {
        try {
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);
            byte[] byteArray = buf.toArray();

            outputStream.writeObject(byteArray);
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error enviando la imagen al servidor: " + e.getMessage());
        }
    }

    /**
     * Elimina la imagen más antigua del directorio de imágenes.
     */
    private void deleteOldestImage() {
        File directory = new File("images/");
        File[] files = directory.listFiles();

        if (files != null && files.length > 1) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });

            // El primer archivo en la lista ahora será el más antiguo
            if (files[0].delete()) {
                System.out.println("Imagen anterior eliminada correctamente: " + files[0].getName());
            } else {
                System.out.println("Error al eliminar la imagen anterior: " + files[0].getName());
            }
        }
    }

    /**
     * Método principal de la aplicación.
     * Carga la biblioteca nativa de OpenCV y crea una instancia de OpenCV para iniciar la cámara.
     * @param args Los argumentos de línea de comandos (no se utilizan en este caso).
     */
    public static void main(String args[]) {
        // Carga la biblioteca nativa de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Inicia la aplicación en un hilo separado para evitar bloqueos de la GUI
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

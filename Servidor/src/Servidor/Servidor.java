package Servidor;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Date;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La clase Servidor se encarga de recibir y procesar mensajes
 * y/o imágenes enviadas por el cliente. Las imágenes se procesan
 * para extraer texto usando la librería Tesseract.
 */
public class Servidor {
    private JFrame ventana_chat;
    private JTextArea area_chat;
    private JPanel contenedor_areachat;
    private JScrollPane scroll;
    private ServerSocket servidor;
    private Socket socket;
    private BufferedReader lector;
    private PrintWriter escritor;
    private static String ultimaImagenGuardada;

    /**
     * Constructor del servidor. Inicializa la interfaz gráfica.
     */
    public Servidor() {
        hacerInterfaz();
    }

    /**
     * Configura y muestra la interfaz gráfica para el servidor.
     */
    public void hacerInterfaz() {
        ventana_chat = new JFrame("Servidor");
        area_chat = new JTextArea(10, 12);
        scroll = new JScrollPane(area_chat);
        contenedor_areachat = new JPanel();
        contenedor_areachat.setLayout(new GridLayout(1, 1));
        contenedor_areachat.add(scroll);
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_areachat, BorderLayout.NORTH);
        ventana_chat.setSize(300, 220);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread principal = new Thread(() -> {
            try {
                servidor = new ServerSocket(1234);
                while (true) {
                    Socket cliente = servidor.accept();
                    new Thread(() -> manejarCliente(cliente)).start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        principal.start();
    }

    /**
     * Registra una operación en un archivo CSV.
     * @param expresion La expresión evaluada.
     * @param resultado El resultado de la evaluación.
     */
    public void registrarOperacion(String expresion, double resultado) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String fecha = dtf.format(now);
            String registro = "\"" + fecha + "\",\"" + expresion + "\"," + resultado + "\n";
            Files.write(Paths.get("registro_operaciones.csv"), registro.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Maneja la conexión con un cliente específico, procesando sus mensajes e imágenes.
     * @param socketCliente El socket asociado al cliente.
     */
    public void manejarCliente(Socket socketCliente) {
        Thread hiloCliente = new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
                PrintWriter escritorLocal = new PrintWriter(socketCliente.getOutputStream(), true);
                BufferedReader lectorLocal = new BufferedReader(new InputStreamReader(dis));

                while (true) {
                    String tipoMensaje = lectorLocal.readLine();
                    if (tipoMensaje == null || "FIN_CONEXION".equals(tipoMensaje)) {
                        break;
                    }
                    // Procesa imágenes
                    if ("IMAGEN".equals(tipoMensaje)) {
                        int length = dis.readInt();
                        byte[] imageData = new byte[length];
                        dis.readFully(imageData);
                        String imagePath = "receivedImages/" + System.currentTimeMillis() + ".jpg";
                        Files.write(Paths.get(imagePath), imageData);
                        ultimaImagenGuardada = imagePath;
                        area_chat.append("Imagen recibida y guardada en: " + imagePath + "\n");
                        String textFromImage = getTextFromImage(imagePath);
                        area_chat.append("Texto extraído de la imagen: " + textFromImage + "\n");
                        try {
                            ArbolBinarioExp arbol = new ArbolBinarioExp(textFromImage);
                            double resultado = arbol.EvaluaExpresion();
                            area_chat.append("Resultado de la expresión extraída: " + resultado + "\n");
                            escritorLocal.println(resultado);
                            registrarOperacion(textFromImage, resultado);
                        } catch (Exception e) {
                            area_chat.append("Error al evaluar la expresión extraída: " + e.getMessage() + "\n");
                            escritorLocal.println("Error al evaluar la expresión extraída: " + e.getMessage());
                        }
                    } else {
                        String mensaje_recibido = tipoMensaje;
                        area_chat.append("Cliente: " + mensaje_recibido + "\n");
                        try {
                            ArbolBinarioExp arbol = new ArbolBinarioExp(mensaje_recibido);
                            double resultado = arbol.EvaluaExpresion();
                            area_chat.append("Resultado: " + resultado + "\n");
                            escritorLocal.println(resultado);
                            registrarOperacion(mensaje_recibido, resultado);
                        } catch (Exception e) {
                            area_chat.append("Error al evaluar la expresión: " + e.getMessage() + "\n");
                            escritorLocal.println("Error al evaluar la expresión: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        hiloCliente.start();
    }

    /**
     * Extrae texto de una imagen utilizando la librería Tesseract.
     * @param imagePath Ruta de la imagen de la cual extraer texto.
     * @return El texto extraído de la imagen.
     */
    private String getTextFromImage(String imagePath) {
        Tesseract tesseract = new Tesseract();
        try {
            String text = tesseract.doOCR(new File(imagePath));
            return text.trim();
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Error al extraer texto de la imagen.";
        }
    }

    /**
     * Método principal para inicializar el servidor.
     *
     * @param args Argumentos del programa.
     */
    public static void main(String[] args) {
        new Servidor();
    }
}
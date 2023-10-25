package Servidor;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Date;
import java.io.ObjectInputStream;  // Importación requerida para recibir objetos
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Servidor {
    JFrame ventana_chat = null;
    JTextArea area_chat = null;
    JPanel contenedor_areachat = null;
    JScrollPane scroll = null;
    ServerSocket servidor = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;
    private static String ultimaImagenGuardada;

    public Servidor() {
        hacerInterfaz();
    }

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
    private String procesarImagenConTesseract(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Tess4J-3.4.8-src\\Tess4J"); // Asegúrate de que este path es correcto
        String result = tesseract.doOCR(image);
        return result;
    }
    public void manejarCliente(Socket socketCliente) {
        Thread hiloCliente = new Thread(() -> {
            try {
                BufferedReader lectorLocal = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                PrintWriter escritorLocal = new PrintWriter(socketCliente.getOutputStream(), true);

                String mensaje_recibido;
                while ((mensaje_recibido = lectorLocal.readLine()) != null) {
                    if (mensaje_recibido.equals("SOLICITUD_REGISTRO")) {
                        String contenidoRegistro = new String(Files.readAllBytes(Paths.get("registro_operaciones.csv")));
                        escritorLocal.println(contenidoRegistro);
                        continue;
                    }

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
                area_chat.append("Esperando recibir una imagen...\n");  // Añadido para depuración

                InputStream inputStream = socketCliente.getInputStream();
                byte[] lenBytes = new byte[4];
                int bytesRead = inputStream.read(lenBytes, 0, 4);
                if (bytesRead < 4) {
                    area_chat.append("Error: no se pudo leer la longitud de la imagen.\n");
                    return;
                }

                int length = new BigInteger(lenBytes).intValue();
                byte[] imageBytes = new byte[length];
                inputStream.read(imageBytes, 0, length);

                // Convertir los bytes a una imagen en BufferedImage
                InputStream in = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(in);

                area_chat.append("Imagen recibida, intentando guardar...\n"); // Añadido para depuración

                // Guardar la imagen recibida
                guardarImagen(image);

                socketCliente.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        hiloCliente.start();
    }
    private String guardarImagen(BufferedImage image) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = dtf.format(now);

        // Crear carpeta si no existe
        File directorio = new File("receivedImages");
        if (!directorio.exists()) {
            if (!directorio.mkdirs()) {
                area_chat.append("No se pudo crear el directorio 'receivedImages'.\n");
                return null;
            }
            area_chat.append("Directorio 'receivedImages' creado exitosamente.\n");
        }

        String ruta = "receivedImages" + File.separator + "imagen_" + timestamp + ".png";
        try {
            ImageIO.write(image, "png", new File(ruta));
            area_chat.append("Imagen guardada en: " + ruta + "\n");
            ultimaImagenGuardada = ruta;
            return ruta;
        } catch (IOException e) {
            area_chat.append("Error al guardar la imagen: " + e.getMessage() + "\n");
            return null;
        }
    }




    public static void main(String[] args) {
        new Servidor();
    }
}
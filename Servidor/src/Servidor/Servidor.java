package Servidor;

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

        Thread principal = new Thread(new Runnable() {
            public void run() {
                try {
                    servidor = new ServerSocket(1234);
                    while (true) {
                        socket = servidor.accept();
                        manejarCliente(socket);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

    public void manejarCliente(Socket socketCliente) {
        Thread hiloCliente = new Thread(new Runnable() {
            public void run() {
                try {
                    lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                    escritor = new PrintWriter(socketCliente.getOutputStream(), true);
                    String mensaje_recibido;
                    while ((mensaje_recibido = lector.readLine()) != null) {
                        if (mensaje_recibido.equals("SOLICITUD_REGISTRO")) {
                            String contenidoRegistro = new String(Files.readAllBytes(Paths.get("registro_operaciones.csv")));
                            escritor.println(contenidoRegistro);
                            continue;
                        }
                        area_chat.append("Cliente: " + mensaje_recibido + "\n");

                        try {
                            ArbolBinarioExp arbol = new ArbolBinarioExp(mensaje_recibido);
                            double resultado = arbol.EvaluaExpresion();
                            area_chat.append("Resultado: " + resultado + "\n");
                            escritor.println(resultado);
                            registrarOperacion(mensaje_recibido, resultado);
                        } catch (Exception e) {
                            area_chat.append("Error al evaluar la expresión: " + e.getMessage() + "\n");
                            escritor.println("Error al evaluar la expresión: " + e.getMessage());
                        }
                    }
                    socketCliente.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        hiloCliente.start();
    }

    private static void saveReceivedImage(Socket socket) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            byte[] imageBytes = (byte[]) inputStream.readObject();
            Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
            String filename = "received_" + System.currentTimeMillis() + ".jpg";
            Imgcodecs.imwrite(filename, image);
            System.out.println("Imagen guardada como: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al recibir/guardar la imagen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Servidor();
    }
}

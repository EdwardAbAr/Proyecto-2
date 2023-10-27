package Cliente;

import org.opencv.core.Core;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

/**
 * Clase Cliente que establece una interfaz gráfica para interactuar con un servidor.
 * Permite enviar expresiones matemáticas para ser evaluadas y muestra el resultado.
 * También permite interactuar con la cámara y visualizar registros.
 */
public class Cliente {

    // Componentes de la GUI.
    private JFrame ventana_chat;
    private JButton btn_enviar;
    private JButton btn_camara;
    private JButton btn_registro;
    private JTextField txt_mensaje;
    private JTextArea area_chat;
    private JPanel contenedor_areachat;
    private JPanel contenedor_btntxt;
    private JScrollPane scroll;

    // Red y E/S.
    private Socket socket;
    private BufferedReader lector;
    private PrintWriter escritor;

    /**
     * Constructor por defecto que inicializa la interfaz.
     */
    public Cliente() {
        hacerInterfaz();
    }

    /**
     * Método para construir y mostrar la interfaz gráfica del cliente.
     */
    public void hacerInterfaz() {
        ventana_chat = new JFrame("Cliente");
        btn_enviar = new JButton("Enviar");
        btn_camara = new JButton("Camara");
        btn_registro = new JButton("Registro");
        txt_mensaje = new JTextField(4);
        area_chat = new JTextArea(10, 12);
        scroll = new JScrollPane(area_chat);
        contenedor_areachat = new JPanel();
        contenedor_areachat.setLayout(new GridLayout(1,1));
        contenedor_areachat.add(scroll);
        contenedor_btntxt = new JPanel();
        contenedor_btntxt.setLayout(new GridLayout(1,2));
        contenedor_btntxt.add(txt_mensaje);
        contenedor_btntxt.add(btn_enviar);
        contenedor_btntxt.add(btn_camara);
        contenedor_btntxt.add(btn_registro);
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_areachat, BorderLayout.NORTH);
        ventana_chat.add(contenedor_btntxt, BorderLayout.SOUTH);
        ventana_chat.setSize(400, 220);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Agrega acción al botón de cámara.
        btn_camara.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirCamara();
            }
        });

        // Agrega acción al botón de registro.
        btn_registro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    Registro registro = new Registro(socket, lector, escritor);
                    registro.mostrarRegistro();
                    registro.setVisible(true);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Agrega acción al botón de registro.
        Thread principal = new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 1234);
                lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                escritor = new PrintWriter(socket.getOutputStream(), true);
                leer();
                escribir();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        principal.start();
    }
    /**
     * Método para abrir la cámara utilizando la clase OpenCV.
     */
    public void abrirCamara() {
        EventQueue.invokeLater(() -> {
            OpenCV camara = new OpenCV();
            new Thread(() -> camara.startCamera()).start();
        });
    }

    /**
     * Método para leer y mostrar los mensajes del servidor.
     */
    public void leer() {
        Thread leer_hilo = new Thread(() -> {
            try {
                while (true) {
                    String mensaje_recibido = lector.readLine();
                    if (mensaje_recibido == null) {
                        break; // Finaliza si el servidor cierra la conexión.
                    }
                    area_chat.append("Resultado: " + mensaje_recibido + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        leer_hilo.start();
    }

    /**
     * Método para escribir mensajes al servidor.
     */
    public void escribir() {
        Thread escribir_hilo = new Thread(() -> {
            try {
                escritor = new PrintWriter(socket.getOutputStream(), true);
                btn_enviar.addActionListener(e -> {
                    String enviar_mensaje = txt_mensaje.getText();
                    escritor.println(enviar_mensaje);
                    txt_mensaje.setText("");
                });
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        escribir_hilo.start();
    }

    /**
     * Método principal para ejecutar el cliente.
     * @param args Argumentos de línea de comando.
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Cliente();
    }
}
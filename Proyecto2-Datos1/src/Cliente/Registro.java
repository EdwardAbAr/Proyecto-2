package Cliente;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

/**
 * La clase Registro representa una interfaz de cliente para el registro de operaciones.
 */
public class Registro {
    JFrame ventana_chat = null;
    JTextArea area_chat = null;
    JPanel contenedor_areachat = null;
    JScrollPane scroll = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;

    /**
     * Constructor de la clase Registro.
     *
     * @param socket   El socket utilizado para la comunicación.
     * @param lector   El lector de entrada para recibir mensajes.
     * @param escritor El escritor de salida para enviar mensajes.
     */
    public Registro(Socket socket, BufferedReader lector, PrintWriter escritor) {
        this.socket = socket;
        this.lector = lector;
        this.escritor = escritor;
        hacerInterfaz();
    }

    /**
     * Crea la interfaz gráfica de usuario.
     */
    public void hacerInterfaz() {
        ventana_chat = new JFrame("Registro");
        area_chat = new JTextArea(10, 12);
        scroll = new JScrollPane(area_chat);
        contenedor_areachat = new JPanel();
        contenedor_areachat.setLayout(new GridLayout(1, 1));
        contenedor_areachat.add(scroll);
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_areachat, BorderLayout.CENTER); // Solo se usa el chat en el centro
        ventana_chat.setSize(300, 220);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicia un hilo para la comunicación con el servidor.
        Thread principal = new Thread(new Runnable() {
            public void run() {
                try {
                    socket = new Socket("127.0.0.1", 1234);
                    leer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        principal.start();
    }

    /**
     * Lee mensajes del servidor y los muestra en el área de chat.
     */
    public void leer() {
        Thread leer_hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String mensaje_recibido = lector.readLine();
                        if (mensaje_recibido.startsWith("\"")) {
                            area_chat.append("Registro:\n" + mensaje_recibido + "\n\n");
                        } else {
                            area_chat.append("Servidor: " + mensaje_recibido + "\n");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        leer_hilo.start();
    }

    /**
     * Muestra el registro de operaciones en el área de chat.
     */
    public void mostrarRegistro() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("registro_operaciones.csv"));
            String linea;
            while ((linea = br.readLine()) != null) {
                area_chat.append(linea + "\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal que crea una instancia de Registro y establece la comunicación con el servidor.
     *
     * @param args Los argumentos de línea de comandos (no se utilizan en este caso).
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 1234);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            new Registro(socket, lector, escritor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Establece la visibilidad de la ventana de chat.
     *
     * @param b true para hacer visible, false para ocultar.
     */
    public void setVisible(boolean b) {
        ventana_chat.setVisible(b);
    }
}

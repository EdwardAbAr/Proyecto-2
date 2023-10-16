package Cliente;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class Cliente {
    JFrame ventana_chat = null;
    JButton btn_enviar = null;
    JTextField txt_mensaje = null;
    JTextArea area_chat = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;

    public Cliente() {
        hacerInterfaz();
        conectarAlServidor();
    }

    public void hacerInterfaz() {
        ventana_chat = new JFrame("Cliente");
        txt_mensaje = new JTextField(20);
        area_chat = new JTextArea(10, 30);
        JScrollPane scroll = new JScrollPane(area_chat);
        JPanel contenedor_btntxt = new JPanel();
        contenedor_btntxt.setLayout(new GridLayout(1, 2));
        contenedor_btntxt.add(txt_mensaje);
        btn_enviar = new JButton("Enviar");
        contenedor_btntxt.add(btn_enviar);

        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(scroll, BorderLayout.NORTH);
        ventana_chat.add(contenedor_btntxt, BorderLayout.SOUTH);
        ventana_chat.setSize(400, 300);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btn_enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String expresion = txt_mensaje.getText();
                if (escritor != null) {
                    escritor.println(expresion);
                    txt_mensaje.setText("");
                } else {
                    area_chat.append("Error: No connection to the server.\n");
                }
            }
        });
    }

    public void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 1234);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escritor = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception ex) {
            ex.printStackTrace();
            area_chat.append("Error: Could not connect to the server.\n");
        }

        Thread leer_hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String respuesta = lector.readLine();
                        if (respuesta == null) {
                            // Connection closed, break the loop
                            area_chat.append("Server has closed the connection.\n");
                            break;
                        }
                        area_chat.append("Servidor: " + respuesta + "\n");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        leer_hilo.start();
    }

    public static void main(String[] args) {
        new Cliente();
    }
}

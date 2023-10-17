package Servidor;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class Servidor {
    JFrame ventana_chat = null;
    JButton btn_enviar = null;
    JTextField txt_mensaje = null;
    JTextArea area_chat = null;
    JPanel contenedor_areachat = null;
    JPanel contenedor_btntxt = null;
    JScrollPane scroll = null;
    ServerSocket servidor = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;

    public Servidor(){
        hacerInterfaz();
    }

    public void hacerInterfaz() {
        ventana_chat = new JFrame("Servidor");
        btn_enviar = new JButton("Enviar");
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
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_areachat, BorderLayout.NORTH);
        ventana_chat.add(contenedor_btntxt, BorderLayout.SOUTH);
        ventana_chat.setSize(300, 220);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread principal = new Thread(new Runnable() {
            public void run() {
                try {
                    servidor = new ServerSocket(1234);
                    while(true) {
                        socket = servidor.accept();
                        leer();
                        escribir();
                    }
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        principal.start();
    }
    public void leer() {
        Thread leer_hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true) {
                        String mensaje_recibido = lector.readLine();
                        area_chat.append("Cliente: "+mensaje_recibido+"\n");

                    }
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        leer_hilo.start();
    }

    public void escribir() {
        Thread escribir_hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    escritor = new PrintWriter(socket.getOutputStream(), true);
                    btn_enviar.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String enviar_mensaje = txt_mensaje.getText();
                            escritor.println(enviar_mensaje);
                            txt_mensaje.setText("");
                        }
                    });
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        escribir_hilo.start();

    }
    public static void main(String[] args) {

        new Servidor();

    }

}

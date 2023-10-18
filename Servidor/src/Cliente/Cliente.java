package Cliente;

import Cliente.OpenCV;
import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class Cliente {
    JFrame ventana_chat = null;
    JButton btn_enviar = null;
    JButton btn_camara = null;
    JTextField txt_mensaje = null;
    JTextArea area_chat = null;
    JPanel contenedor_areachat = null;
    JPanel contenedor_btntxt = null;
    JScrollPane scroll = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;
    public Cliente() {
        hacerInterfaz();
    }
    public void hacerInterfaz() {
        ventana_chat = new JFrame("Cliente");
        btn_enviar = new JButton("Enviar");
        btn_camara = new JButton("Camara");
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
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_areachat, BorderLayout.NORTH);
        ventana_chat.add(contenedor_btntxt, BorderLayout.SOUTH);
        ventana_chat.setSize(300, 220);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btn_camara.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirCamara();
            }
        });

        Thread principal = new Thread(new Runnable() {
            public void run() {
                try {
                    socket = new Socket("127.0.0.1", 1234);
                    leer();
                    escribir();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        principal.start();
    }

    public void abrirCamara() {
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

    public void leer() {
        Thread leer_hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true) {
                        String mensaje_recibido = lector.readLine();
                        area_chat.append("Servidor: "+mensaje_recibido+"\n");

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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Cliente();

    }

}
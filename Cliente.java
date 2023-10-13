package Cliente;



import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoCliente mimarco=new MarcoCliente();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}


class MarcoCliente extends JFrame{

    public MarcoCliente(){

        setBounds(600,300,280,350);

        LaminaMarcoCliente milamina=new LaminaMarcoCliente();

        add(milamina);

        setVisible(true);
    }

}

class LaminaMarcoCliente extends JPanel{

    public LaminaMarcoCliente(){

        JLabel texto=new JLabel("CLIENTE");

        add(texto);

        campo1=new JTextField(20);

        add(campo1);

        botonEnviar=new JButton("Enviar");

        botonCamara=new JButton("Cámara");

        EnviarExpresion expresion = new EnviarExpresion();

        botonEnviar.addActionListener(expresion);

        add(botonEnviar);

        add(botonCamara);

    }

    private class EnviarExpresion implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            try {

                Socket socket = new Socket("192.168.56.1", 5678);
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                salida.writeUTF(campo1.getText());
                salida.close();


            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }
    private JTextField campo1;

    private JButton botonEnviar;
    private JButton botonCamara;

}
package MangalaCl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Mangala.Message;
import static MangalaCl.Client.sInput;


/**
 *      Enes Kamil YILMAZ
 *         1521221039
 */

class Listen extends Thread{
    public void run(){
        while(Client.socket.isConnected()){
             try {
                //mesaj gelmesini bloking olarak dinyelen komut
                Message received = (Message) (sInput.readObject());
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                /*switch (received.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = received.content.toString();
                        Game.ThisGame.txt_rival_name.setText(name);
                        Game.ThisGame.btn_pick.setEnabled(true);
                        Game.ThisGame.btn_send_message.setEnabled(true);
                        Game.ThisGame.tmr_slider.start();
                        break;
                    case Disconnect:
                        break;
                    case Text:
                        Game.ThisGame.txt_receive.setText(received.content.toString());
                        break;
                    case Selected:
                        Game.ThisGame.RivalSelection = (int) received.content;

                        break;

                    case Bitis:
                        break;

                }*/

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }
    }
}


public class Client {
    public static Socket socket;//her clienta bi socket
    public static ObjectInputStream sInput; //verileri almaya yarar
    public static ObjectOutputStream sOutput; //verileri göndermeye yarar
    public static Listen listenMe;
    
    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();
            
            //ilk mesaj olarak isim gönderiyorum
            Message msg = new Message(Message.Message_Type.Name);
            //msg.content = Game.ThisGame.txt_name.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
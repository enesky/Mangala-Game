package Mangala;

import MangalaCl.Client;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import Mangala.Entry;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Mangala extends javax.swing.JFrame implements ActionListener {
    
    public static Mangala mangala;
    public Thread th;
    public int turn = 3;
    public boolean sent = false, oneMore = false, finish = false;
    public String sent2="";
    public int[][] pit = new int[2][7];
    public int[][] RivalsPit = new int[2][7];
    // 6,5,4,3,2,1,0
    // 0,1,2,3,4,5,6
    
    public Mangala() {
        initComponents();
        mangala = this;
        this.setLocation(250, 250);
        this.setSize(625, 390);
        this.setResizable(false);
        this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        jLabel1.setFont(new Font("Courier New", Font.BOLD, 20));
        jLabel2.setFont(new Font("Courier New", Font.BOLD, 12));
        jLabel3.setFont(new Font("Courier New", Font.BOLD, 12));
        jLabel1.setForeground(Color.white);
        rivalNick.setForeground(Color.white);
        nickL.setForeground(Color.white);
        jLabel2.setForeground(Color.white);
        jLabel3.setForeground(Color.white);
        
        rivalsTurn.setVisible(false);
        myTurn.setVisible(false);
        
        try {// Settings for game right indicators
            File f = new File("src\\images","redArrow.png");
            BufferedImage bi = ImageIO.read(f);
            Image scaled = bi.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            rivalsTurn.setIcon(icon);
            myTurn.setIcon(icon);
        } catch (IOException ex) {
            Logger.getLogger(Mangala.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //For inital condition this sets all buttons disable
        enableButtons(false);
        //Fills 2D Array that used for pit values
        diziDoldur();
        //Gives pits their initial values
        diziAktar(pit);
         
        b1.setFocusable(false);b2.setFocusable(false);b3.setFocusable(false);
        b4.setFocusable(false);b5.setFocusable(false);b6.setFocusable(false);
        a1.setFocusable(false);a2.setFocusable(false);a3.setFocusable(false);
        a4.setFocusable(false);a5.setFocusable(false);a6.setFocusable(false);
        aH.setFocusable(false);bH.setFocusable(false);
        aH.setOpaque(true);
        bH.setOpaque(true);
        b1.setHorizontalAlignment(SwingConstants.CENTER);b2.setHorizontalAlignment(SwingConstants.CENTER);
        b3.setHorizontalAlignment(SwingConstants.CENTER);b4.setHorizontalAlignment(SwingConstants.CENTER);
        b5.setHorizontalAlignment(SwingConstants.CENTER);b6.setHorizontalAlignment(SwingConstants.CENTER);
        a1.setHorizontalAlignment(SwingConstants.CENTER);a2.setHorizontalAlignment(SwingConstants.CENTER);
        a3.setHorizontalAlignment(SwingConstants.CENTER);a4.setHorizontalAlignment(SwingConstants.CENTER);
        a5.setHorizontalAlignment(SwingConstants.CENTER);a6.setHorizontalAlignment(SwingConstants.CENTER);
        aH.setHorizontalAlignment(SwingConstants.CENTER);bH.setHorizontalAlignment(SwingConstants.CENTER);
        nickL.setHorizontalTextPosition(SwingConstants.CENTER);
        rivalNick.setHorizontalTextPosition(SwingConstants.CENTER);
        
        //Naming all buttons for actionperformed actions
        a1.setActionCommand("a1"); a1.addActionListener(this);
        a2.setActionCommand("a2"); a2.addActionListener(this);
        a3.setActionCommand("a3"); a3.addActionListener(this);
        a4.setActionCommand("a4"); a4.addActionListener(this);
        a5.setActionCommand("a5"); a5.addActionListener(this);
        a6.setActionCommand("a6"); a6.addActionListener(this);
        b1.setActionCommand("b1"); b1.addActionListener(this);
        b2.setActionCommand("b2"); b2.addActionListener(this);
        b3.setActionCommand("b3"); b3.addActionListener(this);
        b4.setActionCommand("b4"); b4.addActionListener(this);
        b5.setActionCommand("b5"); b5.addActionListener(this);
        b6.setActionCommand("b6"); b6.addActionListener(this);
        rulesB.setActionCommand("rulesB"); rulesB.addActionListener(this);
        
        //Settings for frame closing actions
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int n = JOptionPane.showConfirmDialog(mangala, "Çıkış yapmakta ısrarcı mıyız?", "Çıkış yapılıyor...", JOptionPane.YES_NO_OPTION);
                if (Rules.isOpen) {
                    Entry.r.setVisible(false);
                    Entry.r.dispose();
                }
                if (n == 0) {//If clicked OK
                    Client.Stop();//DISCONNECT
                    System.exit(1);
                    mangala.dispose();
                } else if (n == 1) {//If clicked Cancel
                    System.out.println("Çıkış iptal edildi.");
                }
            }
        });
        
        th = new Thread(() -> {
            
            //Enable Butttons when game starts
            enableButtons(true);
            
            while (Client.socket.isConnected() && !finish) {
                
                if (gameOver(pit) == 1) {
                    System.out.println(nickL + "IS WON CONGRATS!!!");
                    JOptionPane.showMessageDialog(this,nickL.getText() + "IS WON CONGRATS!!! Score = " + pit[1][6], "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
                    enableButtons(false);
                    Message msg = new Message(Message.Message_Type.Disconnect);
                    msg.content = 1;
                    Client.Send(msg);
                    this.dispose();
                    Entry giris = new Entry();
                    giris.setVisible(true);
                    try {
                        Client.socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Mangala.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(gameOver(pit) == 0){
                    System.out.println(rivalNick + "IS WON CONGRATS!!!");
                    JOptionPane.showMessageDialog(this,rivalNick.getText() + "IS WON CONGRATS!!! Score = " + pit[0][0], "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
                    enableButtons(false);
                    Message msg = new Message(Message.Message_Type.Disconnect);
                    msg.content = 0;
                    Client.Send(msg);
                    this.dispose();
                    Entry giris = new Entry();
                    giris.setVisible(true);
                    try {
                        Client.socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Mangala.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (turn == 1) { //turn = 1 means that its first clients turn
                    myTurn.setVisible(true);
                    rivalsTurn.setVisible(false);
                }
                else if(turn == 0) { //turn = 0 means that its rival clients turn
                    myTurn.setVisible(false);
                    rivalsTurn.setVisible(true); 
                }
                
                if (sent2.equals("ok")) { //If message Sent2 = ok that means rival is made his turn and now its turn for other player
                    diziAktar(pit);
                    sent2 = "tamam";
                    if (turn == 1)
                        turn = 0;
                    else if(turn == 0)
                        turn = 1;
                }
                try {
                    th.sleep(100);
                    if (sent && sent2.equals("tamam")) { //Now player turns have been set. Its time for changing pit values.
                        diziAktar(pit);
                        pit = RivalsPit;
                        sent = false;
                        sent2 = "";
                        RivalsPit = new int[2][7];
                    }                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(Mangala.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
             if (!Client.socket.isConnected()) { //If client is not connected yet or disconnected this sets buttons disable
                 enableButtons(false);
            }
        });
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aH = new javax.swing.JTextField();
        bH = new javax.swing.JTextField();
        rulesB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nickL = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        newGameB = new javax.swing.JButton();
        rivalNick = new javax.swing.JLabel();
        b6 = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        b4 = new javax.swing.JButton();
        b2 = new javax.swing.JButton();
        a3 = new javax.swing.JButton();
        b1 = new javax.swing.JButton();
        b5 = new javax.swing.JButton();
        a1 = new javax.swing.JButton();
        a2 = new javax.swing.JButton();
        a4 = new javax.swing.JButton();
        a5 = new javax.swing.JButton();
        a6 = new javax.swing.JButton();
        rivalsTurn = new javax.swing.JLabel();
        myTurn = new javax.swing.JLabel();
        bck = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        aH.setText("Hazine1");
        aH.setPreferredSize(new java.awt.Dimension(60, 24));
        getContentPane().add(aH, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 90, 61, 228));
        aH.getAccessibleContext().setAccessibleName("");

        bH.setText("Hazine2");
        bH.setPreferredSize(new java.awt.Dimension(60, 24));
        getContentPane().add(bH, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 61, 228));

        rulesB.setText("Kurallar");
        getContentPane().add(rulesB, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 86, -1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Mangala");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, -1, -1));

        jLabel2.setText("<==");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, -1, -1));

        jLabel3.setText("==>");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 270, -1, -1));

        nickL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nickL.setText("Nick Name");
        nickL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nickL.setMaximumSize(new java.awt.Dimension(100, 16));
        nickL.setMinimumSize(new java.awt.Dimension(100, 16));
        nickL.setPreferredSize(new java.awt.Dimension(100, 16));
        getContentPane().add(nickL, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 150, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(252, 70, 120, 10));

        newGameB.setText("Yeni Oyun");
        newGameB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameBActionPerformed(evt);
            }
        });
        getContentPane().add(newGameB, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, -1, -1));

        rivalNick.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rivalNick.setText("Rakip Bekleniyor");
        rivalNick.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rivalNick.setMaximumSize(new java.awt.Dimension(100, 16));
        rivalNick.setMinimumSize(new java.awt.Dimension(100, 16));
        rivalNick.setPreferredSize(new java.awt.Dimension(100, 16));
        getContentPane().add(rivalNick, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 180, 190, -1));

        b6.setText("1");
        getContentPane().add(b6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 57, 57));

        b3.setText("1");
        getContentPane().add(b3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 57, 57));

        b4.setText("1");
        getContentPane().add(b4, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 57, 57));

        b2.setText("1");
        getContentPane().add(b2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, 57, 57));

        a3.setText("1");
        getContentPane().add(a3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 250, 57, 57));

        b1.setText("1");
        getContentPane().add(b1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 57, 57));

        b5.setText("1");
        getContentPane().add(b5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 100, 57, 57));

        a1.setText("1");
        getContentPane().add(a1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 250, 57, 57));

        a2.setText("1");
        getContentPane().add(a2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 57, 57));

        a4.setText("1");
        getContentPane().add(a4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 250, 57, 57));

        a5.setText("1");
        getContentPane().add(a5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 250, 57, 57));

        a6.setText("1");
        getContentPane().add(a6, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 250, 57, 57));

        rivalsTurn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rivalsTurn.setToolTipText("");
        getContentPane().add(rivalsTurn, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 170, 77, 30));
        getContentPane().add(myTurn, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 210, 77, 26));

        bck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/background.jpg"))); // NOI18N
        bck.setText(".");
        bck.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(bck, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 420));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
        This method makes a action according to string value from clicked button that given all the buttons at constructor. 
        If clicked button named a1, it looks for the redArrows visiblity and location and if player has right to play method sends buttons name to GamePlay method.
    */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        switch (action) {
            case "a1":
                System.out.println("a1 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a1");
                break;
            case "a2":
                System.out.println("a2 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a2");
                break;
            case "a3":
                System.out.println("a3 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a3");
                break;
            case "a4":
                System.out.println("a4 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a4");
                break;
            case "a5":
                System.out.println("a5 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a5");
                break;
            case "a6":
                System.out.println("a6 pressed!");
                if (!myTurn.isVisible()) {
                    JOptionPane.showMessageDialog(this, "Sıranızı beklemelisiniz.", "Sıra rakipte", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                gamePlay("a6");
                break;
            case "b1":
                System.out.println("b1 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "b2":
                System.out.println("b2 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "b3":
                System.out.println("b3 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "b4":
                System.out.println("b4 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "b5":
                System.out.println("b5 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "b6":
                System.out.println("b6 pressed!");
                JOptionPane.showMessageDialog(this, "Rakibin kuyusuna dokunmamalısın.", "Yanlış Kuyu", JOptionPane.ERROR_MESSAGE);
                break;
            case "rulesB":
                if (Rules.isOpen)
                    System.out.println(" Already Open");
                else 
                    Entry.r.setVisible(true);
                break;
        }
    }
    
    //Enables or disables all the buttons according to given parameter
    public void enableButtons(boolean b){
        b1.setEnabled(b);b2.setEnabled(b);b3.setEnabled(b);
        b4.setEnabled(b);b5.setEnabled(b);b6.setEnabled(b);
        a1.setEnabled(b);a2.setEnabled(b);a3.setEnabled(b);
        a4.setEnabled(b);a5.setEnabled(b);a6.setEnabled(b);
        aH.setEnabled(b);bH.setEnabled(b);
    }
    
    /*
        This method checks players pits if their values are equals to zero.
        If it is it makes finish = true; and returns 0 or 1 according to players turn
    */
    public int gameOver(int [][] a){
        if (a[0][1] == 0 && a[0][2] == 0 && a[0][3] == 0 && a[0][4] == 0 && a[0][5] == 0 && a[0][6] == 0) {
            a[0][0] = a[1][0] + a[1][1] + a[1][2] + a[1][3] + a[1][4] + a[1][5];
            a[1][0] = a[1][1] = a[1][2] = a[1][3] = a[1][4] = a[1][5] = 0 ;
            finish = true;
            return 0; //if 0 return rivalNick
        } 
        if (a[1][0] == 0 && a[1][1] == 0 && a[1][2] == 0 && a[1][3] == 0 && a[1][4] == 0 && a[1][5] == 0) {
            a[1][6] = a[0][1] + a[0][2] + a[0][3] + a[0][4] + a[0][5] + a[0][6];
            a[0][1] = a[0][2] = a[0][3] = a[0][4] = a[0][5] = a[0][6] = 0 ;
            finish = true;
            return 1; // if 1 return nickL
        }
        return 2;
    }
    
    //This method fills the 2D array that used for pits values for initial state.
    public void diziDoldur() {
        for (int i = 0; i < 2; i++) 
            for (int j = 0; j < 7; j++) 
                pit[i][j] = 4;
        
        pit[1][6] = 0; // aH set to 0
        pit[0][0] = 0; // bH set to 0
    }

    /*
    This method intercrosses the 2D array that used for pit values.
    Then gives it to the RivalsPit[][].
    This method used when a player makes a play.
    */
    public void diziCevir() {
        for (int i = 0; i < 2; i++) 
            for (int j = 0; j < 7; j++) 
                RivalsPit[i][j] = pit[(i + 1) % 2][6 - j];
    }
    
    //This method sets buttons texts to their values.
    public void diziAktar(int [][] a) {
        a1.setText(a[1][0] + "");a2.setText(a[1][1] + "");a3.setText(a[1][2] + "");
        a4.setText(a[1][3] + "");a5.setText(a[1][4] + "");a6.setText(a[1][5] + "");
        aH.setText(a[1][6] + "");
        b1.setText(a[0][6] + "");b2.setText(a[0][5] + "");b3.setText(a[0][4] + "");
        b4.setText(a[0][3] + "");b5.setText(a[0][2] + "");b6.setText(a[0][1] + "");
        bH.setText(a[0][0] + "");
    }
    
    /*
    This method is core method for the gameplay.
    Simply this method doing the rock moves through pits and checks the game rules.
    */
    public int movePit(int pitsValue, int rightPitCoord) {
        int temp = 0, lastIndex = 0;
        boolean passed = false, onChest = false, mutual = false, evenGoes2Chest = false;
        if (pitsValue > 0) {
            if (pitsValue == 1) {
                if (pit[1][rightPitCoord] == 0) { //yandaki pitte taş yoksa 3.kural gerçekleşir
                   pit[1][rightPitCoord] += 1; 
                    if (rightPitCoord != 6) { 
                        pit[1][6] += pit[0][rightPitCoord+1] + pit[1][rightPitCoord];
                        pit[1][rightPitCoord] = pit[0][rightPitCoord+1] = 0;
                        mutual = true;
                    }
                }
                if (rightPitCoord == 6) {
                    oneMore = true;
                }
                else
                    pit[1][rightPitCoord] += 1;
                if(mutual)
                    JOptionPane.showMessageDialog(this, "Son taşınız, boş bir kuyunuza denk geldiği için boş kuyunuzun\n karşısındaki kuyudaki taşları hazinenize kattınız.", "", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            } else if (pitsValue > 1 && pitsValue <= 7) {
                for (int i = 0; i < pitsValue - 1; i++) {
                    if (i + rightPitCoord < 7){
                        if (i+rightPitCoord == 6) 
                            onChest = true;
                        if (i + rightPitCoord <= 5) { //ilk 6 kuyudayken 3.kurala bakarız
                            int a = (rightPitCoord-1)+(pitsValue-1);
                            int b = pitsValue - 2;
                            if (i == b && pit[1][a] == 0) {
                                mutual = true;
                            } 
                        }
                        pit[1][i + rightPitCoord]++;
                    }
                    else { //üst sıraya geçince...
                        mutual = onChest = false;
                        passed = true;
                        pit[0][6 - temp]++;
                        lastIndex = 6-temp;
                        temp++;
                    }
                }
                if (passed && pit[0][lastIndex] % 2 == 0) { 
                    //Son taş karşı kuyulardan birini çift sayı yaparsa. O kuyudaki taşların hepsini hazinesine katar.
                            pit[1][6] += pit[0][lastIndex];
                            pit[0][lastIndex] = 0;
                            evenGoes2Chest = true;
                   }
                if (onChest && !passed) 
                    oneMore = true;
                if(evenGoes2Chest)
                    JOptionPane.showMessageDialog(this, "Son taşınız, rakibin kuyularından birini çift sayı yaptı\n ve o kuyudaki tüm taşları hazinenize kattınız.", "", JOptionPane.INFORMATION_MESSAGE);
                if(mutual){
                    pit[1][6] += pit[0][(rightPitCoord-1) + (pitsValue-1)+1] + pit[1][(rightPitCoord-1) + (pitsValue-1)];
                    pit[0][(rightPitCoord-1) + (pitsValue-1)+1] = pit[1][(rightPitCoord-1) + (pitsValue-1)] = 0;
                    JOptionPane.showMessageDialog(this, "Son taşınız, boş bir kuyunuza denk geldiği için boş kuyunuzun\n karşısındaki kuyudaki taşları hazinenize kattınız.", "", JOptionPane.INFORMATION_MESSAGE);
                }
                return 1;
            } else if (pitsValue > 7) { 
                pitsValue--;//İlk kuyuya 1 tane bıraktık.
                while (rightPitCoord<7) {//Alt bölgedeki işlemler...                    
                    pit[1][rightPitCoord]++;
                    pitsValue--;
                    rightPitCoord++;
                }
                rightPitCoord--; //üst tarafa geçince 7 olan rightPitCoord değerini 6 yaparız.
                while (pitsValue>0) {//Üst bölgedeki işlemler        
                    pit[0][rightPitCoord]++;
                    rightPitCoord--;
                    pitsValue--;
                }
                rightPitCoord++; //son index için son kuyu değerini çift sayın kontrolü için alırız.
                if (pit[0][rightPitCoord] % 2 == 0) {
                    pit[1][6] += pit[0][rightPitCoord];
                    pit[0][rightPitCoord] = 0;
                    evenGoes2Chest = true;
                }
                if(evenGoes2Chest)
                    JOptionPane.showMessageDialog(this, "Son taşınız, rakibin kuyularından birini çift sayı yaptı\n ve o kuyudaki tüm taşları hazinenize kattınız.", "", JOptionPane.INFORMATION_MESSAGE);
                return 1;
            }
        } else {
            System.out.println("nothing to do");
            return 0;
        }
        return -999;
    }
    
    /*
        This method is second important method because it gives the parameter to movePit method and 
        makes the comminication messaging through clients.
    */
    public void gamePlay(String s) {
        switch (s) {
            case "a1":
                if (pit[1][0] == 0) 
                    break; //kS += 1;
                pit[1][0] = movePit(pit[1][0], 1);
                break;
            case "a2":
                if (pit[1][1] == 0)
                    break;
                pit[1][1] = movePit(pit[1][1], 2);
                break;
            case "a3":
                if (pit[1][2] == 0) 
                    break;
                pit[1][2] = movePit(pit[1][2], 3);
                break;
            case "a4":
                if (pit[1][3] == 0) 
                    break;
                pit[1][3] = movePit(pit[1][3], 4);
                break;
            case "a5":
                if (pit[1][4] == 0) 
                    break;
                pit[1][4] = movePit(pit[1][4], 5);
                break;
            case "a6":
                if (pit[1][5] == 0) 
                    break;
                pit[1][5] = movePit(pit[1][5], 6);
                break;
            default:
                System.out.println("Select a valid kuyu");
        }
        
        diziAktar(pit);
        
        if (Client.socket.isConnected() && !oneMore) {
        //TODO bağlantı yoksa , rakip gelmediyse ve 1 oyun hakkın daha yoksa oyuna başlatma izin verme
                   
            Message msg = new Message(Message.Message_Type.Pits);
            diziCevir();
            System.out.println("RivalsPit karşıya yollandı.");
            msg.content = RivalsPit;
            Client.Send(msg);
            
            Message msg2 = new Message(Message.Message_Type.Sent);
            sent2 = "ok";
            msg2.content = sent2;
            Client.Send(msg2);
            sent = true;       
             
            System.out.println("gameplayden mesaj yollandı");
        }
        else
            System.out.println("Client bağlantısı yok.");
        
        if (oneMore) { //Oyun hakkı silinir.
            JOptionPane.showMessageDialog(this, "Son taşınızı hazinenize kattığınız için bir oyun hakkı daha kazandınız.", "1 Oyun hakkı kazandınız.", JOptionPane.INFORMATION_MESSAGE);
            oneMore = false;
        }
    }

    private void newGameBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameBActionPerformed
        // TODO add your handling code here:
        System.out.println("DISCONNECT AND GO TO ENTRY FRAME");
        //DISCONNECT
        finish = true;
        try {
            Client.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Mangala.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
        Entry giris = new Entry();
        giris.setVisible(true);
    }//GEN-LAST:event_newGameBActionPerformed

    //Cant access to variables so i made these methods
    //With this methods i can access these variables
    public JLabel getnickL() {
        return nickL;
    }
    public JLabel getrivalNick() {
        return rivalNick;
    }
    public JLabel getMyTurn(){
        return myTurn;
    }
    public JLabel getRivalsTurn(){
        return rivalsTurn;
    }
    public JButton geta1() {
        return a1;
    }
    public JButton geta2() {
        return a2;
    }
    public JButton geta3() {
        return a3;
    }
    public JButton geta4() {
        return a4;
    }
    public JButton geta5() {
        return a5;
    }
    public JButton geta6() {
        return a6;
    }
    public JTextField getaH() {
        return aH;
    }
    public JButton getb1() {
        return b1;
    }
    public JButton getb2() {
        return b2;
    }
    public JButton getb3() {
        return b3;
    }
    public JButton getb4() {
        return b4;
    }
    public JButton getb5() {
        return b5;
    }
    public JButton getb6() {
        return b6;
    }
    public JTextField getbH() {
        return bH;
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Mangala.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Mangala.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Mangala.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Mangala.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Mangala().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton a1;
    private javax.swing.JButton a2;
    private javax.swing.JButton a3;
    private javax.swing.JButton a4;
    private javax.swing.JButton a5;
    private javax.swing.JButton a6;
    private javax.swing.JTextField aH;
    private javax.swing.JButton b1;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JButton b5;
    private javax.swing.JButton b6;
    private javax.swing.JTextField bH;
    private javax.swing.JLabel bck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel myTurn;
    private javax.swing.JButton newGameB;
    private javax.swing.JLabel nickL;
    private javax.swing.JLabel rivalNick;
    private javax.swing.JLabel rivalsTurn;
    private javax.swing.JButton rulesB;
    // End of variables declaration//GEN-END:variables

}

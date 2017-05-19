package multithread;

import static java.lang.Math.random;                                            //Importazione classe Random
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MultiThread {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Thread iniziata...");
        long start = System.currentTimeMillis();
        Monitor monitor = new Monitor();
        Thread tic = new Thread(new TicTacToe("TIC", monitor));                 //Creazione dei thread
        Thread tac = new Thread(new TicTacToe("TAC", monitor));
        Thread toe = new Thread(new TicTacToe("TOE", monitor));
        tic.start();                                                            //Avvio dei thread
        tac.start();
        toe.start();

        try {
            tic.join();                                                         //Aspetta che i thread finiscano la loro esecuzione
            tac.join();
            toe.join();
        } 
        catch (InterruptedException e) {    
        }
        System.out.println("Punteggio = " + monitor.getPunteggio());            //Stampa il punteggio
        long end = System.currentTimeMillis();
        System.out.println("Main Thread completata! tempo di esecuzione: " + (end - start) + "ms");  
    }
}

class TicTacToe implements Runnable {                                           //Classe dei thread
    private String nome;
    private String msg;
    Monitor monitor;
    //Costruttore
    public TicTacToe (String n, Monitor m) {
        nome = n;
        monitor = m;
    }
    @Override                                                                   //Annotazione per il compilatore
    public void run() {                                                         //Istruzioni eseguite dal threrad quando è in funzione
        for (int i = 10; i > 0; i--) {
            monitor.incrementaPunteggio(nome, msg);
            msg = "<" + nome + "> " + nome + ": " + i;
            System.out.println(msg);
        }
    } 
}

class Monitor {                                                                 //Classe del monitor
    String last = " ";                                                          //Ultimo thread visualizzato
    int punteggio = 0;
    
    public int getPunteggio() {                                                 //Stampa il punteggio
        return punteggio;
    }
    
    public synchronized void incrementaPunteggio(String thread, String msg) {   //Metodo condiviso dai thread
        Random rand = new Random();                                             //Creazione di un numero casuale
        int j = 100;
        int n = 300-j;
        int tempo = rand.nextInt(n)+j;
        msg += ": " + tempo + " :";
        if( thread.equals("TOE") && last.equals("TAC")) {                       //Controlla quando TAC viene prima di TIC 
            punteggio++;                                                        //Se si verifica la condizione aggiorna il punteggio
        }
        try {
            TimeUnit.MILLISECONDS.sleep(tempo);                                 //Thread in pausa per un tempo casuale
        } 
        catch (InterruptedException e) {
        }            
        last = thread;                                                          //Ultimo thread visualizzato 
    }
}

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
        System.out.println("PunteggioSync = " + monitor.getPunteggioSync());    //Stampa il punteggio del metodo synchronized
        System.out.println("PunteggioNoSync = " + monitor.getPunteggioNoSync());//Stampa il punteggio del metodo non synchronized
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
            Random rand = new Random();                                         //Creazione di un numero casuale
            int j = 100;
            int n = 300-j;
            int tempo = rand.nextInt(n)+j;
            monitor.incrementaPunteggioSync(nome, msg, tempo);                  //Richiama il metodo synchronized
            monitor.incrementaPunteggioNoSync(nome, msg, tempo);                //Richioama lo stesso metodo non synchronized
            msg = "<" + nome + "> " + nome + ": " + i;
            System.out.println(msg);
        }
    } 
}

class Monitor {                                                                 //Classe del monitor
    String lastSync = " ";                                                      //Ultimo thread visualizzato dal metodo condiviso synchronized
    String lastNoSync = " ";                                                    //Ultimo thread visualizzato dal metodo condiviso non synchronized
    int punteggioSync = 0;                                                      //Punteggio del metodo condiviso synchronized
    int punteggioNoSync = 0;                                                    //Punteggio del metodo condiviso non synchronized
    
    public int getPunteggioSync() {                                             //Stampa il punteggio del metodo condiviso synchronized
        return punteggioSync;
    }
    
    public int getPunteggioNoSync() {                                           //Stampa il punteggio del metodo condiviso no synchronized
        return punteggioNoSync;
    }
    
    public synchronized void incrementaPunteggioSync(String thread, String msg, int tempo){//Metodo synchronized condiviso dai thread
        msg += ": " + tempo + " :";
        if( thread.equals("TOE") && lastSync.equals("TAC"))                     //Controlla quando TAC viene prima di TIC 
        {                       
            punteggioSync++;                                                    //Se si verifica la condizione aggiorna il punteggio
        }
        try {
            TimeUnit.MILLISECONDS.sleep(tempo);                                 //Thread in pausa per un tempo casuale
        } 
        catch (InterruptedException e) {
        }            
        lastSync = thread;                                                      //Ultimo thread visualizzato 
    }
    
    public void incrementaPunteggioNoSync(String thread, String msg, int tempo){//Metodo condiviso dai thread
        msg += ": " + tempo + " :";
        if( thread.equals("TOE") && lastNoSync.equals("TAC"))                   //Controlla quando TAC viene prima di TIC 
        {                       
            punteggioNoSync++;                                                  //Se si verifica la condizione aggiorna il punteggio
        }
        try {
            TimeUnit.MILLISECONDS.sleep(tempo);                                 //Thread in pausa per un tempo casuale
        } 
        catch (InterruptedException e) {
        }            
        lastNoSync = thread;                                                    //Ultimo thread visualizzato
    }
}

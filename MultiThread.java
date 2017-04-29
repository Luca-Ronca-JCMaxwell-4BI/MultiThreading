package multithread;

import static java.lang.Math.random; //importazione classe Random
import java.util.Random;
import java.util.concurrent.TimeUnit; 
import static multithread.TicTacToe.punteggio;  //Importazione della variabile statica punteggio contenuta in TicTacToe

public class MultiThread {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Thread iniziata...");
        long start = System.currentTimeMillis();
        
        Thread tic = new Thread(new TicTacToe("TIC"));  // creazione dei thread
        Thread tac = new Thread(new TicTacToe("TAC"));
        Thread toe = new Thread(new TicTacToe("TOE"));
        tic.start();    // partenza dei thread
        tac.start();
        toe.start();

        try {
            tic.join(); //Aspetta che i thread si interrompano
            tac.join();
            toe.join();
        } 
        catch (InterruptedException e) {    
        }
        System.out.println("Punteggio = " + punteggio); //stampa quante volte TOE è venuto dopo TAC
        long end = System.currentTimeMillis();
        System.out.println("Main Thread completata! tempo di esecuzione: " + (end - start) + "ms");  //stampa il tempo impiegato
    }
}
class TicTacToe implements Runnable {
    private String t;
    private String msg;
    public static boolean conf = false;
    public static int punteggio = 0;
    public TicTacToe (String s) {
        this.t = s;
    }
    @Override // Annotazione per il compilatore
    public void run() {  //istruzioni eseguite dal threrad quando è in funzione
        for (int i = 10; i > 0; i--) {
            if("TAC".equals(t))  //controlla se il thread precedente era TAC
            {
                conf = true;
            }
            Random rand = new Random(); //creazione di un numero casuale
            int j = 100;
            int n = 300-j;
            int tempo = rand.nextInt(n)+j;
            
            msg = "<" + t + "> ";
            try {
                TimeUnit.MILLISECONDS.sleep(tempo); //attivazione in un tempo casuale
            } 
            catch (InterruptedException e) {
            }
            if("TOE".equals(t) && conf == true)  //controlla se TOE è venuto dopo TAC
            {
                punteggio ++;  // incremento del puntegggio 
            }
            else
            {
                conf = false; 
            }
            msg += t + ": " + i;
            System.out.println(msg);
        }
    } 
}

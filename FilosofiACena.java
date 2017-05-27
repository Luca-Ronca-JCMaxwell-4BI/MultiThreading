package filosofiacena;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// Monitor 
class Tavola {
    boolean forchetta[];                                                        //Risorsa condivisa dai filosofi
    static int M;                                                                      //Numero bottiglie di vino
    int posti;                                                                  //posti a tavola
    
    public enum Azione {                                                        //Variabile enumerativa che comprende le azioni che un filosofo può compiere
        pensa                   (0),                                            
        vuolePrendereForchette  (1),                                            //Gli servono 2 forchette
        aspettaForchette        (2),                                            //Se non sono disponibili aspetta
        mangia                  (3),                                            //Se sono disponibili mangia
        siAlza                  (4),                                            //Si alza dopo 3 portate 
        beve                    (5);                                            //Tra una portata e l'altra beve un bicch8iere di vino
        private final int idx;
        
        //Costruttore
        private Azione (int idx) {
            this.idx = idx;
        }

        public int getIDX() { 
            return this.idx; 
        }
    }
    
    //Costruttore
    public Tavola(int N, int M) {
        this.posti = N;
        this.forchetta = new boolean[N];
        for (int i=0; i<N; i++) { 
            this.forchetta[i] = true; 
        }
        this.M = M;
    }
    
    public synchronized boolean prendiForchette (int Sinistra) {                //Metodo sincronizzato che gestisce come i filosofi prendono le forchette
        int Destra = Sinistra + 1;
        if (Destra == posti){
            Destra = 0;
        }
        if (forchetta[Sinistra] && forchetta[Destra]){                          //Il filosofo prende la sua forchetta insieme a quella del suo vicino
            forchetta[Sinistra] = false; 
            forchetta[Destra] = false; 
            return true;
        } 
        else {
            return false;  
        }
    }    

    public synchronized void aspettaForchette() {                               //Metodo sincronizzato chegewstisce l'attesa delle forchette
            try {
                wait();                                                         //Mette il thread in attesa delle risorse mancanti
            } catch (InterruptedException ex){
                Logger.getLogger(Tavola.class.getName()).log(Level.SEVERE, null, ex);
            }  
    }
    
    public synchronized void posaForchette (int Sinistra) {                     //Metodo sincronizzato, il filosofo posa le forchette quando ha finito
        int Destra = Sinistra + 1;      
        if (Destra == posti){
            Destra = 0;
        }
        forchetta[Sinistra] = true; 
        forchetta[Destra] = true;
        notifyAll();                                                            //Segnala a tutti i thread che ha concluso l'operazione e che le risorse sono nuovamente disponibili
    }  
}

class Bottiglia{                                                                //Bottiglie di vino
    int nBicchieri = 4;                                                         //Bicchieri di vino per bottiglia
    
    //Costruttore
    public Bottiglia(){
    }
    
    //Getter/Setter
    public void setNBicchieri(int n){
        nBicchieri = n;
    }
    public int getNBicchieri(){
        return nBicchieri;
    }
}

//Monitor
class Monitor {                                                                 //Servge per stampare sullo schermo cosa succede durante la cena
    int filosofi[][] = new int[Tavola.Azione.values().length][FilosofiACena.Nfilosofi]; 
    String msg;
    
    public synchronized void aggiornaSituazione(int id, Tavola.Azione azione) { //Metodo sincronizzato, gestisce il variare delle azioni dei filosofi
        msg = "F[" + id + "] " + azione.name();                                 //Stampa ciò che sta facendo il filosofo
        for(int i=0; i<Tavola.Azione.values().length; i++){ 
            filosofi[i][id] = 0; 
        }
        filosofi[azione.getIDX()][id] = 1;                                      //Aggiorna l'azione
        for(Tavola.Azione a: Tavola.Azione.values()){
            for(int i=0; i<FilosofiACena.Nfilosofi; i++){ 
                if(filosofi[a.getIDX()][i] == 1){ 
                } 
            }
        } 
        System.out.println(msg);
    }
}

class Filosofo implements Runnable{                                             //Classe che crea i thread
    int id;                                                                     //"Nome" del filosofo  
    Tavola.Azione azione;                                                       //Azione compiutadal filosofo
    int mangiato = 0;                                                           //Numero di portate mangiate
    Tavola tavolo;             
    Monitor monitor;       
    
    //Costruttore
    public Filosofo(int i, Tavola tav, Monitor monitor) {
        this.id = i;
        this.tavolo = tav;
        this.azione = Tavola.Azione.pensa;
        this.monitor = monitor;
    }
    
    @Override                                                                   //Annotazione del compilatore
    public void run () {                                                        //Istruzioni che deve eseguire il thread
        Bottiglia vino = new Bottiglia();                                       //Bottiglia di vino
        while (true) {
            monitor.aggiornaSituazione(id, azione);                             //Stampa cosa sta facendo il filosofo
            switch (azione){
                case pensa:                                                     //Azioni svolte quando pensa
                    try { 
                        TimeUnit.MILLISECONDS.sleep(100); 
                    } 
                    catch (InterruptedException e) {
                    }
                    azione = Tavola.Azione.vuolePrendereForchette;              //Dopo che pensa vuole mangiare
                    System.out.println("F[" + id + "] ha pensato a qualcosa");
                    break;

                case vuolePrendereForchette:                                    //Azioni svolte quando vuole prendere le forchette
                    if (tavolo.prendiForchette(id)) {                           //Se sono disponibili prernde le forchette
                        System.out.println("F[" + id + "] ha preso le forchette");
                        azione = Tavola.Azione.mangia;
                    } else {
                        System.out.println("F[" + id + "] non ha trovato almeno una delle forchette");
                        azione = Tavola.Azione.aspettaForchette;                //Altriment aspetta
                    }
                    break;

                case aspettaForchette:                                          //Azioni svolte quando aspetta le forchette
                    tavolo.aspettaForchette();                                  //Aspetta...
                    azione = Tavola.Azione.vuolePrendereForchette;              //Appena qualcuno ha finito prende le forchette
                    break;

                case mangia:                                                    //Azioni svolte quando mangia
                    try { 
                        TimeUnit.MILLISECONDS.sleep(200); 
                    } 
                    catch (InterruptedException e) {
                    }
                    mangiato++;                                                 //Incrementa il numero delle portate mangiate
                    System.out.println("F[" + id + "] ha finito di mangiare la portata N. " + mangiato);
                    tavolo.posaForchette(id);                                   //Appena finito posa le forchette
                    if(mangiato == 3) {                                         //Se ha mangiato 3 portate si alza
                        azione = Tavola.Azione.siAlza;
                        System.out.println("F[" + id + "] si alza da tavola");
                    } 
                    else { 
                        azione = Tavola.Azione.beve;                            //Altrimenti beve un bicchiere di vino
                    }
                    break;
                    
                case beve:                                                      //Azioni svolte quando beve
                    int n = vino.getNBicchieri() - 1;                           //Il filosofo consuma un bicchiere
                    if(n == 0){                                                 //Se la bottiglia è vuota
                        if(Tavola.M != 0)                                       //Si prende una bottiglia nuova
                        {
                            vino.setNBicchieri(4);
                            Tavola.M --;
                        }
                        else{                                                   //Sempre se ne siano rimaste
                            System.out.println("Bottiglie di vino teminate");   
                        }
                    }
                    else{
                        vino.setNBicchieri(n);
                    }
                    System.out.println("F[" + id + "] ha bevuto un bicchiere di vino");
                    azione = Tavola.Azione.pensa;                               //Dopo aver bevuto un po il filosofo pensa
                    break;
                    
                case siAlza:                                                    //Azioni svolte quando si alza
                    System.out.println("F[" + id + "] si alza da tavola");      
                    return;                                                     //Interrompe il ciclo e si alza da tavola 
                    
                default:                                                        //In teoria questo non dovrebbe capitare
                    System.out.println("ERRORE");                               //Ma se succede stampa un messaggio di errore
                    break;
            }
        }
    }
}

public class FilosofiACena {                                                    //Classe del main
    public final static int Nfilosofi = 5;                                      //Numero di filosofi invitati a cena
    public static int NBottiglie = 4;                                           //Scorte di vino presenti
    
    public static void main(String[] args) {
        Tavola tavola = new Tavola(Nfilosofi, NBottiglie);                      //Sceglie il tavolo
        Monitor m = new Monitor();                                              //Crea un monitor
        Thread[] filosofo = new Thread[Nfilosofi];                              
        for (int i=0; i<Nfilosofi; i++) {
            filosofo[i] = new Thread( new Filosofo(i, tavola, m) );
            filosofo[i].start();
        }
    }
}

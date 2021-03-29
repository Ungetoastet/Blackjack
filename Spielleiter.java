import java.util.ArrayList;
import java.util.Random;

public class Spielleiter
{
    private ArrayList<Spieler> mitspieler;
    private ArrayList<Karte> kartenDeck;
    private int FertigeSpieler;
    private int MatchID;
    private boolean RundeGestartet;

    public Spielleiter()
    {
        // MatchID wird erstellt (Debug)
        Random rand = new Random();
        MatchID = rand.nextInt(100);
        TSPrint("Spielleiter erstellt.", null);

        // Mitspielerliste wird erstellt
        mitspieler = new ArrayList<Spieler>();
        // Bank-Spieler wird hinzugefügt
        new Spieler("< Bank >", this);
        mitspieler.get(0).Geld = 9999;

        // Kartendeck wird generiert
        kartenDeck = new ArrayList<Karte>();
        KartenDeckGenerieren();
    }

    private void KartenDeckGenerieren()
    {
        // Alle möglichen Werte für Karten
        String[] zahl = {"Ass", "Zwei", "Drei", "Vier", "Fuenf", "Sechs", "Sieben", "Acht", "Neun", "Zehn", "Bube", "Dame", "Koenig"};
        String[] zeichen = {"Karo", "Herz", "Pik", "Kreuz"};
        int[] werte = {1,2,3,4,5,6,7,8,9,10,10,10,10};

        // Jede mögliche Wert-Kombination wird erstellt
        for(int i:werte)
        {
            for(int j=0; j<4; j++)
            {
                Karte genKarte = new Karte(i, zeichen[j]+zahl[i-1]);
                kartenDeck.add(genKarte);
            }
        }
    }

    public void SpielerHinzufuegen(Spieler sp)
    {
        // Der Spieler wird der Spielerliste hinzugefügt
        mitspieler.add(sp);
        TSPrint("Spieler hinzugefuegt: " + sp.name, null);
    }

    // Ein voller, automatisierter Testprozess
    public String FullTest()
    {
        TSPrint("Voller test wurde gestartet", null);
        
        // Testspieler werden erstellt
        for(int i=0; i<2; i++)
        {
            new Spieler("tester " + i, this);
        }

        // Automatischer Einsatz
        for(Spieler sp : mitspieler)
        {
            if(sp.name != "< Bank >")
            {
                sp.EinsatzSetzen(150);
            }
        }

        // Es werden maximal 5 Karten gezogen
        for(int i=0; i<5; i++)
        {
            for(Spieler sp : mitspieler)
            {
                // Es wird nur eine Karte gezogen, falls man auf oder unter 16pts ist
                if(sp.PtsStand <= 16)
                {
                    sp.KarteZiehen();
                }
            }
        }

        // Jeder Spieler beendet die Runde, Siegerermittlung wird automatisch gestartet
        for(Spieler sp : mitspieler)
        {
            sp.Stand();
        }

        return "Full Test done! Check console for details.";    
    }

    public Karte KarteZiehen()
    {
        // Man kann nur eine Karte ziehen, falls die Runde gestartet wurde
        if(RundeGestartet)
        {
            // Falls das Deck leer ist, wird ein neues Generiert
            if(kartenDeck.size() <= 1)
            {
                KartenDeckGenerieren();
                TSPrint("Neues Kartendeck generiert.", null);
            }

            // Zufällige Karte ziehen und diese aus dem Deck entfernen
            Random rand = new Random();
            int index = rand.nextInt(kartenDeck.size());
            Karte randomElement = kartenDeck.get(index);
            kartenDeck.remove(index);

            // Gezogene Karte ausgeben
            return randomElement;    
        }
        else
        {
            // Fehlermeldung geben falls Runde nicht gestartet wurde
            TSPrint("Konnte Karte nicht ziehen, da die Runde noch nicht gestartet wurde!", null);
            return (new Karte(-1, "NOPE"));
        }
    }

    // Ein erweitertes Print/Ausgabe system
    public void TSPrint(String s, Spieler sp)
    {
        // Signatur - von welchem Spieler kommt die Meldung?
        String sig = (sp!=null)?sp.name:"Spielleiter";
        // Zeit - zu welcher Zeit wurde die Meldung gegeben
        String[] time = java.time.LocalTime.now().toString().split(":");
        String timeStamp = "[" + MatchID + "@" + time[0] + ":" + time[1] + "]";
        // Die erweiterte Meldung ausgeben
        System.out.println(timeStamp + " (" + sig + ") " + s);
    }

    public void SpielerFertig(Spieler s)
    {
        TSPrint(s.name + " ist raus.", null);
        // Fertige Spieler zählen
        FertigeSpieler ++;
        // Siegerermittlung und Reset falls alle fertig sind
        if(FertigeSpieler == mitspieler.size())
        {
            SiegerErmittlung();
        }
    }

    private void SiegerErmittlung()
    {
        // Punktestand der Bank speichern
        int BankPts = mitspieler.get(0).PtsStand;

        // Alle Mitspieler gegen die Bank überprüfen
        for(Spieler sp : mitspieler)
        {
            // Die Bank beim vergleich überspringen
            if(sp.name!="< Bank >")
            {
                // Siegüberprüfung
                if(sp.PtsStand >= BankPts && sp.PtsStand <= 21 || BankPts > 21 && sp.PtsStand <= 21)
                {
                    // Geld auszahlen
                    sp.Geld += sp.AktEinsatz*2;
                    TSPrint("hat die Runde gewonnen und " + sp.AktEinsatz*3 + "$ gewonenn!", sp);    
                }
                else
                {
                    TSPrint("hat die Runde verloren!", sp);

                    // Spieler rauswerfen, falls er Pleite ist
                    if(sp.Geld < 100)
                    {
                        TSPrint("ist pleite!", sp);
                        // Spieler aus Spielerliste entfernen
                        mitspieler.remove(sp);
                        // Spielerobjekt zerstören
                        sp.spielleiter = null;
                        sp = null;
                    }
                }
            }
        }
        // Alles zurücksetzen
        Reset();
    }

    private void Reset()
    {
        // Alle Rundenwerte zurücksetzen
        FertigeSpieler = 0;
        for(Spieler sp : mitspieler)
        {
            sp.PtsStand = 0;
            sp.AktEinsatz = 0;
            sp.fertig = false;
            sp.hand = new ArrayList<Karte>();
        }
    }

    public void RundeStarten()
    {
        RundeGestartet = false;
        
        // Checken, ob alle Spieler einen Einsatz gesetzt haben (Bank überspringen)
        for(Spieler sp:mitspieler)
        {
            if(sp.AktEinsatz == 0 && sp.name != "< Bank >")
            {
                RundeGestartet = false;
                return;
            }
        }
        // Alle Spieler bereit machen
        for(Spieler sp:mitspieler)
        {
            sp.fertig = false;
        }
        RundeGestartet = true;
        TSPrint("Runde gestartet!", null);

        // Für Bank direkt alle Karten ziehen
        boolean BankImSpiel = true;
        // Bank-Spieler zwischenspeichern
        Spieler Bank = mitspieler.get(0);
        while(BankImSpiel)
        {
            Bank.KarteZiehen();
            if(Bank.PtsStand >= 17)
            {
                BankImSpiel = false;
                Bank.Stand();
            }
        }
    }

    public void Hilfe()
    {
        // Erklärung für das Spiel
        TSPrint("\n\nErklärung: \nJeder Mitspieler spielt gegen die Bank.\nJeder Spieler muss mindestens 100$ einsetzen.\nGewinnt man gegen die Bank, wird das doppelte vom Einsatz wieder ausgezahlt.\nDie Bank muss bei einem Punktestand unter 17 eine weitere Karte ziehen.\n\nViel Erfolg!", null);
    }
}

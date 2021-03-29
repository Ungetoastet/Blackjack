import java.util.ArrayList;

public class Spieler
{
    public String name;
    public int PtsStand;
    public int Geld;
    public int AktEinsatz;
    public Spielleiter spielleiter;
    public Boolean fertig;
    public ArrayList<Karte> hand;

    public Spieler(String n, Spielleiter sl)
    {
        // Werte übernehmen
        name = n;
        fertig = true;
        Geld = 1000;
        AktEinsatz = 0;
        spielleiter = sl;

        // Spieler beim Spielleiter anmelden
        spielleiter.SpielerHinzufuegen(this);
        // Hand erstellen
        hand = new ArrayList<Karte>();
    }

    public void EinsatzSetzen(int Einsatz)
    {
        // Hier sind viele verschachtelte if-else Bedingungen um unterschiedliche Meldungen auszugeben (anstatt ein großes if mit &&)
        // Falls noch kein Einsatz gesetzt wurde..
        if(AktEinsatz == 0)
        {   
            // ...und genug Geld zur Verfügung ist... 
            if(Einsatz <= Geld)
            {
                // ...und der Einsatz groß genug ist...
                if(Einsatz >= 100)
                {
                    // ...dann den Einsatz anmelden und versuchen die Runde zu starten
                    AktEinsatz = Einsatz;
                    Geld -= Einsatz;
                    spielleiter.TSPrint("Einsatz gesetzt: " + Einsatz, this);
                    spielleiter.RundeStarten();    
                }
                else
                {
                    spielleiter.TSPrint("Einsatz zu klein! (min 100)", this);
                }    
            }
            else
            {
                spielleiter.TSPrint("nicht genug Geld zur Verfügung!", this);
            }
        }
        else
        {
            spielleiter.TSPrint("- Einsatz bereits gesetzt mit " + AktEinsatz, this);
        }
    }

    public void KontoStand()
    {
        // Kontostand ausgeben
        spielleiter.TSPrint("Kontostand: " + Geld + "$", this);
    }

    public void KarteZiehen()
    {
        // Falls der Spieler noch im Spiel ist...
        if(!fertig)
        {
            // Eine Karte ziehen
            Karte gezogen = spielleiter.KarteZiehen();
            if(gezogen.wert == -1)
            {
                return;
            }
            else
            {
                // Karte zur Hand hinzufügen
                hand.add(gezogen);
                int ptsTemp = 0;
                int asse = 0;
                boolean assAlsEins = false;
                
                // Asse checken und diese kleiner rechnen, falls nötig
                for(Karte k : hand)
                {
                    // Alle Asse zählen
                    if(k.wert == 1)
                    {
                        asse ++;
                        ptsTemp += k.wert + 10;
                    }
                    else
                    {
                        ptsTemp += k.wert;
                    }
                }
                // Asse als eins rechnen
                if(ptsTemp>21)
                {
                    ptsTemp -= asse*10;
                    assAlsEins = (asse>0);
                }
                PtsStand = ptsTemp;
                spielleiter.TSPrint("Karte gezogen: " + gezogen.name + " - Stand: " + PtsStand + (assAlsEins?" (Ass als 1)" : " "), this);
                // Falls 21 übertroffen wird, automatisch aussteigen
                if(PtsStand>21)
                {
                    Stand();
                }    
            }
        }
        else
        {
            spielleiter.TSPrint("hat versucht eine Karte zu ziehen, spielt aber nicht (mehr).", this);
        }
    }

    public void Stand()
    {
        // Spielleiter mittteilen, dass Spieler mit der Runde fertig ist
        if(!fertig)
        {
            fertig = true;
            spielleiter.SpielerFertig(this);
        }
    }
}

package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import it.polito.tdp.meteo.DAO.MeteoDAO;


public class Model {
	private int combinazione=0;
	MeteoDAO dao=new MeteoDAO();
	private List<Rilevamento> rilevamenti;
	private List<Rilevamento> soluzionebest=null;
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private int costomin=COST*NUMERO_GIORNI_TOTALI+100*NUMERO_GIORNI_TOTALI;

	public Model() {
	}

	
	public String getUmiditaMedia(int mese) {
		String stampa="";
		TreeMap <String,Integer> risultati=dao.getmediaumiditaMese(mese);
		for(String s:risultati.keySet()) {
			stampa+=s+"  "+risultati.get(s)+"\n";
		}
		return stampa;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		this.rilevamenti=dao.getAllRilevamentiiniziali(mese);
		String soluzione="";
		if(rilevamenti.size()!=45) {
			return "Sei un coglione, il db ha solo 44 rilevazioni per i primi 15 giorni di quel mese";
		}
		List <Rilevamento> parziale=new ArrayList<>();
		cerca(parziale,0);
		soluzione+="il costo è di: "+costomin+"\n";
		for(Rilevamento r:soluzionebest) {
		soluzione+=r.getLocalita()+"  "+r.getUmidita()+"  "+r.getData()+"\n";
		}
		if(soluzionebest.size()==0) {
			return "Non c'è nemmeno una soluzione che vada bene";
		}
		soluzione=soluzione.substring(0, soluzione.length()-1);
		return soluzione;
	}
	
	public void cerca(List <Rilevamento> parziale,int livello) {
		int costo=0;
		TreeMap <String,Integer> citta=new TreeMap<>();
		//controllo se almeno 3 consecutive
		int consecutivi=1;
		for(int i=0;i<parziale.size()-1;i++) {
			if(parziale.get(i).getLocalita().equals(parziale.get(i+1).getLocalita())) {
				consecutivi++;
			}
			else {
				if(consecutivi<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					return;
				}
				consecutivi=1;
			}
		}
		//controllo quante volte in ogni citta
		for(Rilevamento r:parziale) {
			if(!citta.containsKey(r.getLocalita())) {
				citta.put(r.getLocalita(), 1);
			}
			else {
				citta.put(r.getLocalita(),citta.get(r.getLocalita())+1);
			}
		}
		for(Rilevamento r: parziale) {
			costo=costo+r.getUmidita();
		}
		for(Rilevamento r:parziale) {
			int indice=parziale.indexOf(r);
			if(indice!=parziale.size()-1) {
				if(!r.getLocalita().equals(parziale.get(indice+1).getLocalita())) {
					costo=costo+COST;
				}
			}
		}
		if(livello==NUMERO_GIORNI_TOTALI) {
			for(String s: citta.keySet()) {
				if(citta.get(s)>6) {
					return;
				}
			}
			if(costo<costomin) {
				soluzionebest=new ArrayList<>(parziale);
			    costomin=costo;
			}
			return;
		}
		for(int colonna=0;colonna<3;colonna++)
			{
			parziale.add(rilevamenti.get(livello*3+colonna));
			cerca(parziale,livello+1);
			parziale.remove(parziale.size()-1);
			}
	}
}

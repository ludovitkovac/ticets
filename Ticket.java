package com.luigi;

import java.util.Iterator;
import java.util.LinkedList;

public class Ticket {
	
	private String nazov;
	private Double cena;
	private Double pravdepodobnostVyhry;
	
	// [0] = suma, [1] = pocet
	private LinkedList<Double[]> listVyhraPocet;
	
	public String getNazov() {
		return nazov;
	}
	
	public void setNazov(String nazov) {
		this.nazov = nazov;
	}
	
	public Double getCena() {
		return cena;
	}
	
	public void setCena(Double cena) {
		this.cena = cena;
	}
	
	public Double getPravdepodobnostVyhry() {
		return pravdepodobnostVyhry;
	}
	
	public void setPravdepodobnostVyhry(Double pravdepodobnostVyhry) {
		this.pravdepodobnostVyhry = pravdepodobnostVyhry;
	}

	public LinkedList<Double[]> getListVyhraPocet() {
		return listVyhraPocet;
	}

	public void setListVyhraPocet(LinkedList<Double[]> listVyhraPocet) {
		this.listVyhraPocet = listVyhraPocet;
	}
	
	public Double getNajvysiaVyhra() {
		
		Double najvyssia = 0.0;
		
		for (Iterator<Double[]> iterator = listVyhraPocet.iterator(); iterator.hasNext();) {
			Double[] doubles = iterator.next();
			
			if (najvyssia < doubles[0]) {
				najvyssia = doubles[0];
			}
		}
		
		return najvyssia;
	}
	
	private Double getNajnizsiaVyhra() {
		
		Double najnizsia = 0.0;
		
		for (Iterator<Double[]> iterator = listVyhraPocet.iterator(); iterator.hasNext();) {
			Double[] doubles = iterator.next();
			
			if (najnizsia > doubles[0]) {
				najnizsia = doubles[0];
			}
		}
		
		return najnizsia;
	}
	
	public Double getPocetVsetkychVyhier() {
		
		Double pocetVsetkychVyhier = 0.0;
		
		for (Iterator<Double[]> iterator = listVyhraPocet.iterator(); iterator.hasNext();) {
			Double[] doubles = iterator.next();
			
			pocetVsetkychVyhier = pocetVsetkychVyhier + doubles[1];
		}
		
		return pocetVsetkychVyhier;
	}
	
	public Double getPocetVsetkychVyhierBezNajnizsej() {
		
		Double pocetVsetkychVyhierBezNajnizsej = 0.0;
		
		for (Iterator<Double[]> iterator = listVyhraPocet.iterator(); iterator.hasNext();) {
			Double[] doubles = iterator.next();
			
			if (!(getNajnizsiaVyhra() == doubles[1])) {
				pocetVsetkychVyhierBezNajnizsej = pocetVsetkychVyhierBezNajnizsej + doubles[1];
			}
		}
		
		return pocetVsetkychVyhierBezNajnizsej;
	}
	
	public Double getHodnotenieZrebu() {
		
		return 0.00;
	}
}

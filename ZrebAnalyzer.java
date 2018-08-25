package com.luigi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZrebAnalyzer {
	
	public static void main(String[] args) {
		HashMap<String, Double> hodnotenieTiketu = new HashMap<String, Double>();

		ArrayList<Ticket> tickets = getTicketList();
		
		for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
			Ticket ticket = iterator.next();
			
			hodnotenieTiketu.put(ticket.getNazov(), ticket.getHodnotenieZrebu());
		}
		
		for (Entry<String, Double> entry : hodnotenieTiketu.entrySet()) {
		    String key = entry.getKey();
		    Double value = entry.getValue();
		    
		    System.out.println(key + " " + value);
		}
	}
	
	public static Double vypocetHodnotyTicketu(Ticket ticket) {
		
		return 1.11;
	}
	
	public static ArrayList<Ticket> getTicketList() {

		// TIPOS URL podstránky Žreby
		final String URL = "http://www.tipos.sk/Default.aspx?CatID=650";
		
		// zoznam url s informáciami o žreboch
		ArrayList<String> urls = new ArrayList<>();
		
		Document doc = null;
		
		try {
			// načítanie HTML kódu podstránky Žreby
			doc = Jsoup.connect(URL).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// nájdenie elementov s odkazmi na stránky s informáciami o žreboch
		Elements elements = doc.getElementsByClass("sibsLink");
		
		// vytvorenie zoznamu url adries stránok s informáciami o žreboch
		for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			
			Elements aElements = element.getAllElements().select("a");
			
			if (aElements != null) {
				String aElementValue = aElements.first().attr("href");
				
				if (aElementValue.contains("Default.aspx?CatID=")) {
					String urlLink = aElementValue.replaceFirst("http://www.tipos.sk/Default.aspx?CatID=", "");
					urls.add("http://www.tipos.sk/" + urlLink);
				}
			}
		}
		
		// zoznam žrebov
		ArrayList<Ticket> ticets = new ArrayList<>();
		
		// prejdenie zoznamu url adries stránok s informáciami o žreboch
		for (Iterator<String> iterator = urls.iterator(); iterator.hasNext();) {
			Ticket ticket = new Ticket();
			
			String url = iterator.next();
			Document docZreb = null;
			
			try {
				// pripojenie sa na stránku s informáciami o žrebe
				docZreb = Jsoup.connect(url)
			    .header("Accept-Encoding", "gzip, deflate")
			    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
			    .maxBodySize(0)
			    .timeout(600000)
			    .get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Elements h1Elements = docZreb.getAllElements().select("h1");
			
			// názov žrebu
			String nazovZrebu = "";
					
			if (h1Elements != null) {
				for (Iterator<Element> iterator1 = h1Elements.iterator(); iterator1.hasNext();) {
					Element element = (Element) iterator1.next();
					
					if (element.text().contains(" #")) {
						int lastIndex = element.text().indexOf(" #");
						nazovZrebu = element.text().substring(0, lastIndex);
						ticket.setNazov(nazovZrebu);
						// System.out.println(nazovZrebu);
					}
				}	
			}
			
			if (nazovZrebu != "") {
				
				Elements liElements = docZreb.getAllElements().select("li");
				
				if (liElements != null) {
					for (Iterator<Element> iterator2 = liElements.iterator(); iterator2.hasNext();) {
						Element liElement = (Element) iterator2.next();
						
						if (liElement.text().contains("Cena stieracieho žrebu je ")) {
							Integer beginIndex = liElement.text().indexOf("Cena stieracieho žrebu je ") + 26;
							Integer endIndex = liElement.text().indexOf(" €.");
							
							String sCena = liElement.text().substring(beginIndex, endIndex);
							Double dCena = Double.valueOf(sCena.replace(",", "."));
							ticket.setCena(dCena);
							// System.out.println(dCena);
						}

						if (liElement.text().contains("Pravdepodobnosť výhry je až 1 : ") ||
							liElement.text().contains("Pravdepodobnosť výhry je 1 : ") ||
							liElement.text().contains("Pravdepodobnost výhry je 1 : ")) {
							
							Integer beginIndex = null;
							
							if (liElement.text().contains("Pravdepodobnosť výhry je až 1 : ")) {
								beginIndex = liElement.text().indexOf("Pravdepodobnosť výhry je až 1 : ") + 32;
							} else if(liElement.text().contains("Pravdepodobnosť výhry je 1 : ")) {
								beginIndex = liElement.text().indexOf("Pravdepodobnosť výhry je 1 : ") + 29;
							} else if(liElement.text().contains("Pravdepodobnost výhry je 1 : ")) {
								beginIndex = liElement.text().indexOf("Pravdepodobnost výhry je 1 : ") + 29;
							}
							
							Integer endIndex = liElement.text().lastIndexOf(".");

							String sPravdepodobnost = liElement.text().substring(beginIndex, endIndex);
							Double dPravdepodobnost = Double.valueOf(sPravdepodobnost.replace(",", "."));
							ticket.setPravdepodobnostVyhry(dPravdepodobnost);
							// System.out.println(dPravdepodobnost);
						}
					}	
					
					
					Elements tbodyElements = docZreb.getAllElements().select("tbody");
					
					if (tbodyElements != null) {
						
						LinkedList<Double[]> listVyhraPocet =  new LinkedList<>();
						
						for (Iterator<Element> iterator2 = tbodyElements.iterator(); iterator2.hasNext();) {
							Element tbodyElement = iterator2.next();
														
							Elements trElements = tbodyElement.getAllElements().select("tr");

							for (Iterator<Element> iterator3 = trElements.iterator(); iterator3.hasNext();) {
								Element trElement = iterator3.next();
								
								Double vyskaPocet[] = new Double[2];

								String suma = trElement.getAllElements().select("td").get(0).text().replace(" ", "");
								
								try {
									vyskaPocet[0] = Double.valueOf(suma.replace(",", "."));
								} catch (NumberFormatException e) {
									continue;
								}
								
								String pocet = trElement.getAllElements().select("td").get(1).text().replace(" ", "");
								vyskaPocet[1] = Double.valueOf(pocet.replace(",", "."));
								
								listVyhraPocet.add(vyskaPocet);
							}
						}
						ticket.setListVyhraPocet(listVyhraPocet);
					}
				}
				ticets.add(ticket);
			}
		}
		
		return ticets;
	}
}

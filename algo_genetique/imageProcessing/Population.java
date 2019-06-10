package imageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Population {
	
	protected final static int POPULATION_SIZE = 200; //taille de la population, constante 
	protected static double tauxMutation = 0.01; //taux de mutation
	private List<Image> listeImages;
	
	public Population(List<Image> liste) {
		this.listeImages = liste;
	}

	public void selection(double tauxSelection) {
		Collections.sort(listeImages); //ordonne les images selon leur fitness dans la liste
		int finSelect = (int) (listeImages.size()*tauxSelection);
		listeImages = listeImages.subList(0,finSelect); //réduit la liste d'images à la sélection
	}
	
	@Override
	public String toString() {
		String afficheImages = "";		
		afficheImages += listeImages.get(0).toString(); // on affiche le meilleur fitness de la population
		return afficheImages;
	}
	
	//permet de croiser les images selectionnées et retourne une nouvelle liste d'images 
	public List<Image> croisement() {
		int currentPopulation = listeImages.size(); //taille de la population après selection
		int nbImagesAGenerer = POPULATION_SIZE-currentPopulation; //nombre d'images à générer
		ArrayList<Image> listeImagesEnfants = new ArrayList<Image>(nbImagesAGenerer);
		
		for(int i = 0; i < nbImagesAGenerer; i++) {
			int rangImage1 = (int) (Math.random() * ( currentPopulation - 1 ));
			int rangImage2 = (int) (Math.random() * ( currentPopulation - 1 ));
			Image image1 = listeImages.get(rangImage1); //on recupère une première image choisie aléatoirement dans la liste
			Image image2 = listeImages.get(rangImage2); //on recupère une deuxième image choisie aléatoirement dans la liste
			
			ArrayList<ConvexPolygon> listePolygonesEnfant = new ArrayList<ConvexPolygon>(50);
			HashMap<Integer, ConvexPolygon> polygonesEnfantsMap = new HashMap<>(50);
			
			ArrayList<Integer> listeIndicesDisponibles = new ArrayList<Integer>(50);
			int rangPolygone;
			int z;

			// Nombre de polygones qu'il y a dans une image
			for (int w= 0; w < 50; w++) {
				listeIndicesDisponibles.add(w);
			}
			
			// On va selectionner 25 polygones au hasard parmi la liste de image1
			for(int j = 0; j < 25; j++) {
				
				z = (int) (Math.random() * ( listeIndicesDisponibles.size()-1 )); //Selection de l'indice de l'indice du polygone à conserver
				rangPolygone=listeIndicesDisponibles.get(z);     //Récupération de l'indice du polygone à conserver via l'indice
				
				if(listeIndicesDisponibles.contains(new Integer(rangPolygone))) {
					listeIndicesDisponibles.remove(new Integer(rangPolygone));
					polygonesEnfantsMap.put(rangPolygone, image1.getListePolygones().get(rangPolygone));
				}
				
					
			}
			// on selectionne les 25 restants dans image2 parmi les rangs non selectionnés dans image1
			for(int k = 0; k < 50; k++) {
				
				if(listeIndicesDisponibles.contains(k))
					polygonesEnfantsMap.put(k, image2.getListePolygones().get(k));
				
			}
			//on copie polygonesEnfantsMap dans listePolygonesEnfant
			for(int j = 0; j < polygonesEnfantsMap.size(); ++j) {
				listePolygonesEnfant.add(polygonesEnfantsMap.get(j));
			}
				
			Image imageEnfant = new Image(listePolygonesEnfant); //on genere une nouvelle image a partir de cette nouvelle liste de polygones
			listeImagesEnfants.add(imageEnfant); //et on inscrit cette image dans listeImagesEnfants
		}
		
		//on peut muter les images générées ensuite
		for (Image img : listeImagesEnfants) {
            if (Math.random() <= tauxMutation) {
            	//le taux de mutation peut réduire ou augmenter la probabilité que les images mutent
            	img.mutation();
            }
		}
		
		this.listeImages.addAll(listeImagesEnfants); // on ajoute la liste d'images générées dans la liste d'images de population
		return this.listeImages;
	}

	public List<Image> getListeImages() {
		return listeImages;
	}

	public void setListeImages(List<Image> listeImages) {
		this.listeImages = listeImages;
	}
	

}

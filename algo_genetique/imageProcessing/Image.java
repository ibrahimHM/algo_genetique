package imageProcessing;

import java.util.Collections;
import java.util.List;

public class Image implements Comparable<Image>{

	//une Image est déterminée par une liste de polygones et un fitness donné
	private double fitness; //distance par rapport à l'image référence (score)
	private List<ConvexPolygon> listePolygones;
	
	
	public Image(List<ConvexPolygon> liste) {
		this.listePolygones = liste;
		this.fitness = 0;		
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public List<ConvexPolygon> getListePolygones() {
		return listePolygones;
	}

	public void setListePolygones(List<ConvexPolygon> listePolygones) {
		this.listePolygones = listePolygones;
	}
	
	public void mutation() {
		
		double typeMutation = Math.random();
		
		//mutation par permutation		
		if(typeMutation < 0.5) {
			int z = (int) (Math.random() * 10);
			for(int k=0; k<z; k++) {
		        int i = (int) (Math.random() * ( this.listePolygones.size() - 1 ));
		        int j = (int) (Math.random() * ( this.listePolygones.size() - 1 ));
		        if(i != j)
				Collections.swap( this.listePolygones, i, j); //on permute deux polygones de la liste choisis aléatoirement
			}
		}
		
		//mutation par délétion / insertion
		if(typeMutation >= 0.5) {
			int y = (int) Math.random() * ( this.listePolygones.size() - 1 );
			listePolygones.remove(y); //on supprime un polygone de la liste
			listePolygones.add(y,new ConvexPolygon((int) ((Math.random() * 3)+3))); 
			//on génère un nouveau polygone qu'on ajoute dans la liste
		}
	}

	@Override
	public String toString() {
		return ""+this.fitness;
	}
	
	//permet d'ordonner ensuite la colection d'image selon le fitness et de manière croissante 
	@Override
    public int compareTo(Image compareImage) {
       
        if(this.fitness < compareImage.getFitness())
            return -1;
        else if(this.fitness > compareImage.getFitness())
            return 1;
        return 0;        
    }
	
}

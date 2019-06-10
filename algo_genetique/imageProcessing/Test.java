package imageProcessing;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Test extends Application{
	
	public static void main(String[] args){
		launch(args);
	}
	
	public void start(Stage myStage){
		String targetImage = "monaLisa-100.jpg";
		Color[][] target=null;
		int maxX=0;
    	int maxY=0;
		try{
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	ConvexPolygon.max_X= maxX;
        	ConvexPolygon.max_Y= maxY;
        	target = new Color[maxX][maxY];
        	for (int i=0;i<maxX;i++){
        		for (int j=0;j<maxY;j++){
        			int argb = bi.getRGB(i, j);
        			int b = (argb)&0xFF;
        			int g = (argb>>8)&0xFF;
        			int r = (argb>>16)&0xFF;
        			int a = (argb>>24)&0xFF;
        			target[i][j] = Color.rgb(r,g,b);
        		}
        	}
        }
        catch(IOException e){
        	System.err.println(e);
        	System.exit(9);
        }
		System.out.println("Read target image " + targetImage + " " + maxX + "x" + maxY);
		
		//Liste qui va contenir les images générées
		List<Image> allImages = new ArrayList<Image>();		
		
		//on génère le nombre d'image selon la taille de la population spécifiée
		for(int nbImages=0; nbImages<Population.POPULATION_SIZE; nbImages++) {
			
		// gÃ©nÃ©ration de 10 triangles
		List<ConvexPolygon> ls = new ArrayList<ConvexPolygon>();
		for (int i=0;i<50;i++)
			ls.add(new ConvexPolygon((int) ((Math.random() * 3)+3)));
		
		//on construit une image à partir de la liste de polygones
		Image currentImage = new Image(ls);
		
		// formation de l'image par superposition des polygones
		Group image = new Group();
		for (ConvexPolygon p : ls)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui possÃ¨de une mÃ©thode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(maxX,maxY);
		image.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image gÃ©nÃ©rÃ©e				
		
		double res=0;
		for (int i=0;i<maxX;i++){
			for (int j=0;j<maxY;j++){
				Color c = pr.getColor(i, j);
				res += Math.pow(c.getBlue()-target[i][j].getBlue(),2)
				+Math.pow(c.getRed()-target[i][j].getRed(),2)
				+Math.pow(c.getGreen()-target[i][j].getGreen(),2);
			}
		}
		
		//on inscrit le score dans le fitness de l'image
		currentImage.setFitness(Math.sqrt(res));
		System.out.println(Math.sqrt(res));		
		
		//on ajoute l'image courante à la liste d'images
		allImages.add(currentImage);		
		}
		
		//on construit la population à partir de notre liste d'images
		Population population = new Population(allImages);
		System.out.println("Population créée avec succès");
		System.out.println("####################### \n");
		
		double tauxSelection = 0.7; //taux de selection, on selectionne ici 70% des meilleures images
		////Selection des x meilleurs		
		population.selection(tauxSelection); //
		
		System.out.println("Meilleure image de la population initiale: "+population);
		System.out.println("#######################");
		
		//fin de la première génération
		
		double bestFitness = 150.0;
		int generation = 1;
		
		//initialisation image
		Group imageGroup = new Group();
		WritableImage wimgNew = new WritableImage(maxX,maxY);
		
		ArrayList<Image> listeImagesEnfants = new ArrayList<Image>(100);
		int cas = 1;
		
		while(generation < 300) {			//300 générations, on a aussi la possibilité d'arreter au bout d'un bestFitness donné
			
			int lastImage = population.getListeImages().size()-1; //rang dernière image
			
			//cette condition nous permet pour une population constituant des images ayant toutes les mêmes fitness
			// de muter la moitié de la population
			// pour éviter de stagner par manque de diversité génétique
			if(bestFitness == population.getListeImages().get(lastImage).getFitness()) {
				cas = 2;
				tauxSelection = 0.5;
				Population.tauxMutation = 1;				
			}else {
				cas= 1;
				tauxSelection = 0.7;
				Population.tauxMutation = 0.01;
			}
			
			switch(cas) {			
				
				//cas generation d'une population par croisement
				case 1:
					// On genere une nouvelle population apres croisement
					for(Image img : population.croisement()) {		      
						
						imageGroup.getChildren().clear();
						// formation de l'image par superposition des polygones
								for (ConvexPolygon p : img.getListePolygones()) {
									if(!imageGroup.getChildren().contains(p))								
									imageGroup.getChildren().add(p);
								}
								
								// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
								// WritableImage, qui possÃ¨de une mÃ©thode pour obtenir un PixelReader.
								
								imageGroup.snapshot(null,wimgNew);
								PixelReader pr = wimgNew.getPixelReader();
								// On utilise le PixelReader pour lire chaque couleur
								// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
								// de la couleur du pixel cible et celui du pixel de l'image gÃ©nÃ©rÃ©e				
								
								double res=0;
								for (int i=0;i<maxX;i++){
									for (int j=0;j<maxY;j++){
										Color c = pr.getColor(i, j);
										res += Math.pow(c.getBlue()-target[i][j].getBlue(),2)
										+Math.pow(c.getRed()-target[i][j].getRed(),2)
										+Math.pow(c.getGreen()-target[i][j].getGreen(),2);
									}
								}
								//on inscrit le fitness de cette nouvelle image pour pouvoir ensuite la classer
								img.setFitness(Math.sqrt(res));
								
					}
					break;				
					
					//cas generation d'une population par mutation uniquement
				case 2 :					
					for (Image img : population.getListeImages()) {
						//on copie l'image originale dans l'image que l'on va muter
						Image imageMutee = new Image(img.getListePolygones());
						
							imageMutee.mutation();		//on mute l'image				
							
							imageGroup.getChildren().clear();
							// formation de l'image par superposition des polygones
							for (ConvexPolygon p : imageMutee.getListePolygones()) {
							if(!imageGroup.getChildren().contains(p))								
							imageGroup.getChildren().add(p);
							}
							
							// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
							// WritableImage, qui possÃ¨de une mÃ©thode pour obtenir un PixelReader.
							
							imageGroup.snapshot(null,wimgNew);
							PixelReader pr = wimgNew.getPixelReader();
							// On utilise le PixelReader pour lire chaque couleur
							// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
							// de la couleur du pixel cible et celui du pixel de l'image gÃ©nÃ©rÃ©e				
							
							double res=0;
							for (int i=0;i<maxX;i++){
							for (int j=0;j<maxY;j++){
							Color c = pr.getColor(i, j);
							res += Math.pow(c.getBlue()-target[i][j].getBlue(),2)
							+Math.pow(c.getRed()-target[i][j].getRed(),2)
							+Math.pow(c.getGreen()-target[i][j].getGreen(),2);
							}
							}
							
							//on enregitre le fitness de l'image mutée et l'ajoute à la liste des images mutées
							imageMutee.setFitness(Math.sqrt(res));
							listeImagesEnfants.add(imageMutee);
						
					}
					//on ajoute la liste des images mutées à la liste d'images de population
					population.getListeImages().addAll(listeImagesEnfants);
					break;
			
			}
			
					listeImagesEnfants.clear(); //on vide listeImageEnfants
					
			generation++;			
			System.out.println("generation"+generation);
			System.out.println("Meilleure image à: "+population);
			System.out.println("Taux de mutation = "+Population.tauxMutation+"\n");
			population.selection(tauxSelection);
			bestFitness = population.getListeImages().get(0).getFitness(); //meilleur fitness de cette population
		}
		
		// Stockage de l'image dans un fichier .png
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimgNew, null); 
				try {
					ImageIO.write(renderedImage, "png", new File("test.png"));
					System.out.println("wrote image in " + "test.png");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// affichage de l'image dans l'interface graphique
				Scene scene = new Scene(imageGroup,maxX, maxY);
				myStage.setScene(scene);
				myStage.show();		
	}

}

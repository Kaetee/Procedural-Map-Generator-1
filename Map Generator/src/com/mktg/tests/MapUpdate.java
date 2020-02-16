package com.mktg.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class MapUpdate extends Thread{
	int mapX = DesktopLauncher.mapX;
	int mapY = DesktopLauncher.mapY;
	int[][] dir = DesktopLauncher.dirLand;
	int[][] field = DesktopLauncher.field;
	DesktopLauncher launcher;
	boolean end = false;
	
	@Override
	public void run() {
		loop:
		while(true){
			if(end){
				break loop;
			}
	        if(launcher != null){
	        	while(launcher.done = false){
	        	}

		        int type;  
		        File[] imgFiles = new File[6];  
		        for (int i = 0; i < 6; i++) {  
		            imgFiles[i] = new File(i + ".png");  
		        }  
		   
		        BufferedImage[] buffImages = new BufferedImage[6];  
		        for (int i = 0; i < 6; i++) {  
		            try {
						buffImages[i] = ImageIO.read(imgFiles[i]);
					} catch (IOException e1) {
						e1.printStackTrace();
					}  
		        }  
		        type = buffImages[0].getType();  
		  
		        BufferedImage finalImg = new BufferedImage(mapX, mapY, type);  
		  
		        int num = 0;
		        for (int rows = 0; rows < mapY; rows++) {  
		            for (int cols = 0; cols < mapX; cols++) {
		            	num = field[cols][rows];
		            	if(launcher.displayType == 0)
			                finalImg.createGraphics().drawImage(buffImages[num], cols, rows, null);
		                if(dir[cols][rows] == 3)
		                	num = 5;
		                else
		                	num = dir[cols][rows];
		                if(launcher.displayType == 1)
			                finalImg.createGraphics().drawImage(buffImages[num], cols, rows, null);
		            }  
		        }
		        
		        
				
		        launcher.map = new ImageIcon(finalImg);
	        	
	    		launcher.update();
            	launcher.validate();
            	launcher.repaint();
            	try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
	 }
}
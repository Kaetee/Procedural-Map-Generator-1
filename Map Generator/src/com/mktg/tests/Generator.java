package com.mktg.tests;


public class Generator extends Thread{
	static boolean done = false;
	int[] amount;
	int turnsPerUpdate = DesktopLauncher.configData[10];
	DesktopLauncher launcher;
	boolean flip = true;
	boolean exit = false;
	
	@Override
	public void run() {
		DesktopLauncher.preInit();
		DesktopLauncher.init();
		int[][] field = DesktopLauncher.field;
		int[][] dirLand = DesktopLauncher.dirLand;
		int[][] dirOcean = DesktopLauncher.dirOcean;
		int[][] mount = DesktopLauncher.mount;
		
		amount = launcher.getConfigGen();
		
		
		loop:
		{
			for(int k=0; k<amount.length; k++){
				
				if(exit){
					break loop;
				}
				if(flip){
					for(int c=0; c<amount[k]/turnsPerUpdate; c++){
						if(exit){
							break loop;
						}
						DesktopLauncher.genLand(turnsPerUpdate, field);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}else{
					for(int c=0; c<amount[k]/turnsPerUpdate; c++){
						if(exit){
							break loop;
						}
						DesktopLauncher.genOcean(turnsPerUpdate, field, dirOcean);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					
				}
				flip = !flip;
				
			}
			
			DesktopLauncher.fixLand(DesktopLauncher.configData[8],field);
			
			for(int i=0; i<DesktopLauncher.configData[9]; i++){
				DesktopLauncher.genMountains(1,field,mount);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		done = true;
	}
}
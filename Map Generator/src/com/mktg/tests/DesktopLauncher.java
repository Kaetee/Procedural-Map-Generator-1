package com.mktg.tests;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class DesktopLauncher extends JFrame {
	static int mapX = 1024;
	static int mapY = 786;
	static int conc;
	boolean done = false;
	int current = 1;
	static int[][] field = new int[mapX][mapY];
	static int[][] dirLand = new int[mapX][mapY];
	static int[][] dirOcean = new int[mapX][mapY];
	static int[][] mount = new int[mapX][mapY];
	static int[][] spawnTypes = new int[16][9];
	static int[][] spawnAreas = new int[9][2];
	public static int ecNormal = 18;
	public static int ecSpecial = 9;
	public static int spawnZoneAmount = 5;
	public static int seedAmount = 10;
	public static int[] configData = {18, 9, 5, 10, 9, 16, 4, 64, 2, 10, 4};
//	public static int[] configGen = {64,32,32,16,64,24,32,32,32,32,64,32,16,0,0,0,0,0,0,0,0,0,0,0,};
	public static int[] configGen = {64,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
	
	// 0 - Pangea
	// 1 - Dual Continents
	static Random random = new Random();
	static JLabel button = new JLabel();
	static JTextField[] configField = new JTextField[configData.length];
	static JTextField[] configGenField = new JTextField[configGen.length];
	static JLabel[] configDesc = {new JLabel("ecNormal (9):"), // expand chance
								  new JLabel("ecSpecial (9):"),
								  new JLabel("spwnZoneAmnt:"),
								  new JLabel("seedAmount:"),
								  new JLabel("rcNormal (9):"), // remove chance
								  new JLabel("rcSpecial (9):"),
								  new JLabel("rcBorder:"),
								  new JLabel("rBorder:"),
								  new JLabel("fixAmount:"),
								  new JLabel("mntAmount:"),
								  new JLabel("Turns/Refresh:"),
								  new JLabel("Repeater:"),};
	JButton gen = new JButton("Generate");
	JButton stop = new JButton("Stop");
	JPanel layoutMain = new JPanel(new GridBagLayout());
	JButton displaySwitch[] = new JButton[6];
	JPanel optionsPanel = new JPanel();
	JPanel mapPanel = new JPanel();
	ImageIcon map = new ImageIcon();
	public int displayType = 0;
	final static GridBagConstraints gbc = new GridBagConstraints();
	Generator t1 = new Generator();
	Painter t2 = new Painter();
	MapUpdate t3 = new MapUpdate();
	private boolean generating = false;
	
	public void setConfigGen(int[] input){
		configGen = input;
	}
	
	public void setConfigGen(int index , int value){
		configGen[index] = value;
	}
	
	public int[] getConfigGen(){
		for(int i=0; i<configGen.length; i++){
		}
		return configGen;
	}
	
	public DesktopLauncher() {
		//constructor
		super("Continents");
		setSize(new Dimension(1424,822));
		setLayout(new GridBagLayout());
		setResizable(false);
		button = new JLabel();
		
		
		gen.setSize(new Dimension(256,64));
		gen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				init();
				for(int i=0; i<configData.length; i++){
					configData[i] = Integer.parseInt(configField[i].getText());
				}
				for(int i=0; i<configGen.length; i++){
					configGen[i] = Integer.parseInt(configGenField[i].getText());
				}
				//t3.end = true;
				generating = true;
				t1.start();
				t2.start();
				t1.launcher = DesktopLauncher.this;
				t2.launcher = DesktopLauncher.this;
			}
		});
		
		stop.setSize(new Dimension(256,64));
		stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				t1.exit = true;
				t2.end = true;
				t1 = new Generator();
				t2 = new Painter();
				generating = false;
			}
		});
		
		for(int i=0; i<configField.length; i++){
			configField[i] = new JTextField(""+configData[i]);
		}
		
		for(int i=0; i<configGenField.length; i++){
			configGenField[i] = new JTextField(""+configGen[i]);
		}
		
		genGUI();
		//update();
		validate();
		repaint();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void switchUpdateThread(){
		//t3.start();
		//t3.launcher = DesktopLauncher.this;
	}
	
	public void genGUI(){
		GridBagLayout optionsLayout = new GridBagLayout();
		GridBagConstraints ogbc = new GridBagConstraints();
		GridBagConstraints mgbc = new GridBagConstraints();
		GridBagConstraints maingbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		ogbc.fill = GridBagConstraints.HORIZONTAL;
		
		ogbc.gridx = 0;
		ogbc.gridy = 0;
		ogbc.gridheight = 1;
		ogbc.gridwidth = 3;
		optionsPanel.setLayout(optionsLayout);
		optionsPanel.add(gen, ogbc);
		
		ogbc.gridx = 0;
		ogbc.gridy = 1;
		ogbc.gridheight = 1;
		ogbc.gridwidth = 3;
		optionsPanel.setLayout(optionsLayout);
		optionsPanel.add(stop, ogbc);

		ogbc.gridx = 0;
		ogbc.gridy = 2;
		ogbc.gridheight = 1;
		ogbc.gridwidth = 1;
		
		String[] k = {"Continents","Land Dir","Mountains (WIP)","x1","Ocean Dir","x2"};
		for(int c=0; c<k.length/3; c++){
			for(int i=(c*3); i<3+(c*3); i++){
				ogbc.gridx = i-(c*3);
				
				final int j = i;
				displaySwitch[i] = new JButton(k[i]);
				displaySwitch[i].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						displayType = j;
					}
					
				});
				displaySwitch[i].setPreferredSize(new Dimension(128, 32));
				optionsPanel.add(displaySwitch[i], ogbc);
			}
			ogbc.gridy++;
		}
		
		

		for(int i=0; i<configData.length; i++){
			ogbc.gridy = 4+i;
			ogbc.gridx = 0;
			optionsPanel.add(configDesc[i], ogbc);
			ogbc.gridx = 1;
			optionsPanel.add(configField[i], ogbc);
		}
		
		ogbc.gridx = 0;
		ogbc.gridy = 4+configData.length;
		ogbc.gridwidth = 3;
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1, 10));
		optionsPanel.add(panel, ogbc);
		ogbc.gridy = 4+configData.length;
		optionsPanel.add(new JSeparator(), ogbc);
		
		ogbc.gridx = 1;
		ogbc.gridy = 6+configData.length;
		ogbc.gridwidth = 1;
		optionsPanel.add(new JLabel("genLand"), ogbc);
		ogbc.gridx = 2;
		optionsPanel.add(new JLabel("genOcean"), ogbc);
		ogbc.gridx = 0;
		ogbc.gridy = 7+configData.length;
		optionsPanel.add(configDesc[configDesc.length-1], ogbc);
		
		boolean flip = true;
		for(int i=0; i<configGen.length; i++){
			ogbc.gridx = (flip) ? 1 : 2;
			ogbc.gridy = 7+configData.length+(i/2);
			optionsPanel.add(configGenField[i], ogbc);
			flip = !flip;
		}
		
		mapPanel.setLayout(new GridBagLayout());
		mgbc.gridx = 3;
		mgbc.gridy = 0;
		mgbc.gridheight = 1;
		mgbc.gridwidth = 1;
		mapPanel.setPreferredSize(new Dimension(1024,786));
		button.setPreferredSize(new Dimension(1024,786));
		
		button.setBorder(new BevelBorder(1));
		mapPanel.add(button, mgbc);
		
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		layoutMain.add(optionsPanel, maingbc);
		maingbc.gridx = 1;
		maingbc.gridy = 0;
		layoutMain.add(mapPanel, maingbc);
		add(layoutMain, gbc);
	}
	
	public static void main(String[] args) throws IOException {
		DesktopLauncher ds = new DesktopLauncher();
		ds.setVisible(true);
		//clrMountains(2, field);
			
		    
	}
	
	public void update(){
        mapPanel.remove(button);
		button = new JLabel(map);
		mapPanel.add(button);
		validate();
		repaint();
	}
	
	
	
	public static void preInit(){
		for(int rows=0; rows<mapY; rows++){
			for(int cols=0; cols<mapX; cols++){
				dirOcean[cols][rows] = 3;
			}
		}
		
		{
			for(int i=0; i<100; i++){
				dirOcean[random.nextInt(mapX)][random.nextInt(mapY)] = random.nextInt(2);
			}
		}
		
		genOceanDir(field);
		
		int x = 1;
		int y = 1;
		
		
		for(int i=0; i<9; i++){
			spawnAreas[i][0] = x;
			spawnAreas[i][1] = y;
			x = x+2;
			if(x == 7){
				x = 1;
				y = y+2;
			}
		}
		
		{
			spawnTypes[1][1] = spawnTypes[1][4] = spawnTypes[1][7] = 0;
			spawnTypes[1][0] = spawnTypes[1][2] = spawnTypes[1][6] = spawnTypes[1][8]=  2;
			spawnTypes[1][3] = spawnTypes[1][5] = 1;
			
			
			spawnTypes[2][0] = spawnTypes[2][2] = spawnTypes[2][6] = spawnTypes[2][8] = 0;
			spawnTypes[2][1] = spawnTypes[2][3] = spawnTypes[2][5] = spawnTypes[2][7] = 2;
			spawnTypes[2][4] = 1;
		}

		for(int i=0; i<mapX; i++){
			for(int j=0; j<mapY; j++){
				field[i][j] = 0;
			}
		}
	}
	
	
	
	public void genMap(int[][] field, int[][] spawnAreas, int[][] spawnTypes, GridBagConstraints gbc){
		for(int i=0; i<mapX; i++){
			for(int j=0; j<mapY; j++){
				dirLand[i][j] = 3;
			}
		}
		
		// The map is split into an 8x8 set segments.
		// You start from x=1 and y=1 so that you always start at least 1 segment away from the edge.
		// You end at x=7 and y=7 so that you're always at most 1 segment away from the other edge
		// This is so that you never spawn on either edge.
		// The increments are in 2 because the size of each segment is 2x2
		// (8x8 - 2x2 (for each side being taken off) = 6x6. 6x6/2 = 2x2)
		
		int chosen = random.nextInt(16);
		chosen = 1;
		
		for(int i=0; i<configData[2]; i++){
			if(chosen > 0){
				if((spawnTypes[chosen][i] == 1) || 
						   (spawnTypes[chosen][i] == 2 && random.nextInt(2) == 0) ||
						   (spawnTypes[chosen][i] == 3 && random.nextInt(3) == 0)){
							for(int j=0; j<configData[3]; j++){
								field[random.nextInt(mapX/4)+((spawnAreas[i][0])*(mapX/8))]
								     [random.nextInt(mapY/4)+((spawnAreas[i][1])*(mapY/8))] = 1;////////////////////////////
							}
							
						}
			}else{
				for(int j=0; j<configData[3]; j++){
					field[random.nextInt(mapX/4)+(1+(2*random.nextInt(3))*(mapX/8))]
						     [random.nextInt(mapY/4)+(1+(2*random.nextInt(3))*(mapY/8))] = 1;
				}
			}
		}

		init();
		genLand(32, field);
		genOcean(16,field, dirOcean);
		genLand(64, field);
		genOcean(24, field, dirOcean);
		genLand(32, field);
		genOcean(16, field, dirOcean);
		genLand(32, field);
		genOcean(32, field, dirOcean);
		genLand(32, field);
		genOcean(32, field, dirOcean);
		genLand(16, field);
		
//		genLand(64, field);
//		genOcean(16, field);
//		genLand(32, field);
//		genOcean(32, field);
//		genLand(64, field);
//		genOcean(16, field);
//		genLand(48, field);
//		genOcean(32, field);
//		fixLand(40, field);
		//System.out.println("10^");
		
		//Mountain Generator
		conc = random.nextInt(7);
		for(int i=0; i<(conc+1); i++){
			int x = 0;
			int y = 0;
			boolean searching = true;
			while(searching){
				x = random.nextInt(mapX);
				y = random.nextInt(mapY);
				if(field[x][y] == 1){
					field[x][y] = 5;
					searching = false;
				}
			}
		}
	}
	
	
	
	public void genImage(int[][] field) throws IOException{
		int sizeY = mapY;   //we assume the no. of rows and cols are known and each chunk has equal width and height  
        int sizeX = mapX;  
  
        int chunkWidth, chunkHeight;  
        int type;  
        //fetching image files  
        File[] imgFiles = new File[6];  
        for (int i = 0; i < 6; i++) {  
            imgFiles[i] = new File(i + ".png");  
        }  
  
       //creating a bufferd image array from image files  
        BufferedImage[] buffImages = new BufferedImage[6];  
        for (int i = 0; i < 6; i++) {  
            buffImages[i] = ImageIO.read(imgFiles[i]);  
        }  
        type = buffImages[0].getType();  
        chunkWidth = 1;
        chunkHeight = 1;
  
        //Initializing the final image  
        BufferedImage finalImg = new BufferedImage(chunkWidth*sizeX, chunkHeight*sizeY, type);  
        BufferedImage finalImg2 = new BufferedImage(chunkWidth*sizeX, chunkHeight*sizeY, type);  
  
        int num = 0;  
        for (int rows = 0; rows < mapY; rows++) {  
            for (int cols = 0; cols < mapX; cols++) {
            	num = field[cols][rows];
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * cols, chunkHeight * rows, null);
                if(dirLand[cols][rows] == 3)
                	num = 5;
                else
                	num = dirLand[cols][rows];
                finalImg2.createGraphics().drawImage(buffImages[num], chunkWidth * cols, chunkHeight * rows, null);
            }  
        }
        //System.out.println("Image concatenated.....");  
        ImageIO.write(finalImg, "png", new File("C:\\Users\\Marcel\\Desktop\\Cont\\ContinentsMap.png"));
        ImageIO.write(finalImg2, "png", new File("C:\\Users\\Marcel\\Desktop\\Cont\\ContinentsMapX.png"));
        
       //correct(field);
	}
	
	
	
	public static void init(){
		for(int i=0; i<mapX; i++){
			for(int j=0; j<mapY; j++){
					if(field[i][j] == 1){
						conc = random.nextInt(5);
						if(conc == 0 || conc == 1 || conc == 2){
							dirLand[i][j] = 0;
						}else{
							dirLand[i][j] = 1;
						}
					}
			}
		}
		
		for(int i=0; i<mapX; i++){
			for(int j=0; j<mapY; j++){
				dirLand[i][j] = 3;
				mount[i][j] = 0;
			}
		}
		
		// The map is split into an 8x8 set segments.
		// You start from x=1 and y=1 so that you always start at least 1 segment away from the edge.
		// You end at x=7 and y=7 so that you're always at most 1 segment away from the other edge
		// This is so that you never spawn on either edge.
		// The increments are in 2 because the size of each segment is 2x2
		// (8x8 - 2x2 (for each side being taken off) = 6x6. 6x6/2 = 2x2)
		
		int chosenSeeder = random.nextInt(16);
		chosenSeeder = 0;
		
		if(chosenSeeder > 0){
			for(int i=0; i<configData[2]; i++){
				int chosenArea = random.nextInt(9);
				if((spawnTypes[chosenSeeder][chosenArea] == 1) || 
						   (spawnTypes[chosenSeeder][chosenArea] == 2 && random.nextInt(2) == 0) ||
						   (spawnTypes[chosenSeeder][chosenArea] == 3 && random.nextInt(3) == 0)){
							for(int j=0; j<configData[3]; j++){
								field[random.nextInt(mapX/4)+((spawnAreas[chosenArea][0])*(mapX/8))]
								     [random.nextInt(mapY/4)+((spawnAreas[chosenArea][1])*(mapY/8))] = 1;
							}
							
				}
			}
		}else{
			for(int i=0; i<configData[2]; i++){
				int temp = random.nextInt(9);
				
				for(int j=0; j<configData[3]; j++){
					field[random.nextInt(mapX/4)+((spawnAreas[temp][0])*(mapX/8))]
					     [random.nextInt(mapY/4)+((spawnAreas[temp][1])*(mapY/8))] = 1;
				}
			}
		}
		
	}
	
	public static void clrMountains(int amount, int[][] field){
		int check = 0;
		int count = 0;
		for(int x=0; x<amount; x++){
			for(int i=0; i<mapX; i++){
				for(int j=0; j<mapY; j++){
					if(field[i][j] == 2){
						if(field[i-1][j] == 1){
							check++;
						}
						if(field[i][j-1] == 1){
							check++;
						}
						if(field[i-1][j-1] == 1){
							check++;
						}
						if(field[i+1][j] == 1){
							check++;
						}
						if(field[i][j+1] == 1){
							check++;
						}
						if(field[i+1][j+1] == 1){
							check++;
						}
						if(field[i-1][j+1] == 1){
							check++;
						}
						if(field[i+1][j-1] == 1){
							check++;
						}
						
						if(check == 8){
							count++;
							conc = random.nextInt(100);
							if(conc != 0)
								field[i][j] = 1;
						}
						check = 0;
						
						conc = random.nextInt(4);
						if(conc == 0)
							field[i][j] = 1;
					}
				}
			}
		}
	}
	
	public static void genMountains(int amount, int[][] field, int[][] mount){
		int chance = 0;
		int count = 0;
		int localCount[] = new int[8];
		for(int i=0; i<8; i++){
			localCount[i] = 0;
		}
		
		for(int k=0; k<amount; k++){
			for(int rows=5; rows<mapY-5; rows++){
				for(int cols=5; cols<mapX-5; cols++){
					for(int c=1; c<6; c++){
						if(field[cols-c][rows] > 1){
							localCount[0]++;
						}
						if(field[cols+c][rows] > 1){
							localCount[1]++;
						}
						if(field[cols][rows-c] > 1){
							localCount[2]++;
						}
						if(field[cols][rows+c] > 1){
							localCount[3]++;
						}
						

						if(field[cols-c][rows-c] > 1){
							localCount[4]++;
						}
						if(field[cols+c][rows-c] > 1){
							localCount[5]++;
						}
						if(field[cols-c][rows+c] > 1){
							localCount[6]++;
						}
						if(field[cols+c][rows+c] > 1){
							localCount[7]++;
						}
					}
					
					for(int i=0; i<8; i++){
						if(localCount[i] > count)
							count = localCount[i];
					}
					
					chance = 2*count;
					
					conc = random.nextInt(32 - chance);
					
					if(conc == 0 || conc == 1){
						mount[cols][rows] = 5;
						field[cols][rows] = 5;
					}
				}
			}
		}
	}
	
	public static void genMountainsg(int amount, int[][] field, int[][] field2){
		int chance = 0;
		int count = 0;
		for(int k=0; k<amount; k++){
			for(int i=0; i<mapX; i++){
				for(int j=0; j<mapY; j++){
					if(i>1 && j>1 && i<(mapX-2) && j<(mapY-2)){
						if(field[i][j] != 1){
							continue;
						}
						for(int c=5; c>1; c--){
							if(field[i-1][j] == c){
								if(field[i-2][j] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i][j-1] == c){
								if(field[i][j-2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i-1][j-1] == c){
								if(field[i-2][j-2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i+1][j] == c){
								if(field[i+2][j] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i][j+1] == c){
								if(field[i][j+2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i+1][j+1] == c){
								if(field[i+2][j+2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i+1][j-1] == c){
								if(field[i+2][j-2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							if(field[i-1][j+1] == c){
								if(field[i-2][j+2] == c){
									chance = 5;
								}else{
									chance = 2;
								}
								count++;
							}
							
							if(count > 0){
								if(c == 5){
									if(count == 1){
										conc = random.nextInt(205);
									}else{
										conc = random.nextInt(70);
									}
								}else if(c == 4){
									if(count < 3){
										conc = random.nextInt(20);
									}else{
										conc = random.nextInt(60);
									}
								}else if(c == 3){
									if(count < 4){
										conc = random.nextInt(15);
									}else{
										conc = random.nextInt(50);
									}
								}else{
									if(count > 3){
										conc = random.nextInt(20*(count*count));
									}else{
										conc = random.nextInt(10);
									}
								}
								
								if(chance > 0){
									for(int x=0; x<chance; x++){
										if(x == conc){
											conc = random.nextInt(4);
											if(conc == 0){
												if(c == 5)
													field[i][j] = c-1;
												else{
													if(random.nextInt() == 0)
														field[i][j] = c+1;
												}
											}else{
												if(c == 2)
													field[i][j] = c;
												else
													field[i][j] = c-1;
											}
										}
									}
								}
								count = 0;
								chance = 0;
							}
						}
					}
				}
			}
		}
	}
	
	public static void expLand(int[][] field, int[][]field2){
		for(int i=0; i<mapX; i++){
			for(int j=0; j<mapY; j++){
				for(int x=0; x<5; x++){
					for(int y=0; y<5; y++){
						field2[x+(i*5)][y+(j*5)] = field[i][j];
					}
				}
			}
		}
	}
	
	public static void correct(int[][] field1){
		ArrayList<int[]> ocean = new ArrayList<int[]>();;
		boolean reset = false;
		boolean relay = false;
		ocean.add(new int[2]);
		
		for(int rows=0; rows<mapY; rows++){
			for(int cols=0; cols<mapX; cols++){
				if(field[cols][rows] == 1)
					reset = true;
			}
			if(!reset){
				ocean.get(ocean.size()-1)[0] = rows;
				ocean.get(ocean.size()-1)[1]++;
				relay = false;
			}
			if(reset && !relay){
				ocean.add(new int[2]);
				relay = true;
			}
		}
		
		int max = -1;
		for(int i=0; i<ocean.size(); i++){
			if(ocean.get(ocean.size()-1)[1] > max){
				max = i;
			}
		}
		
		if(max > -1){
			int[][] newField = new int[mapX][mapY];
			int changeY = ocean.get(max)[0];
			for(int rows=0; rows<mapY; rows++){
				if(changeY == mapY)
					changeY = 0;
				for(int cols=0; cols<mapX; cols++){
					newField[cols][rows] = field[cols][changeY];
				}
				changeY++;
			}
			
			field = newField;
		}
	}
	
	public static void genOceanDir(int[][] field){
		boolean oceanClear = true;
		int dir0 = 0;
		int dir1 = 0;
		System.out.println("unclear!");
		loop:
			while(true){
				oceanClear = true;
				for(int rows=1; rows<mapY-1; rows++){
					for(int cols=1; cols<mapX-1; cols++){
						dir0 = dir1 = 0;
						if(dirOcean[cols][rows] == 3){
							if(dirOcean[cols-1][rows] == 0){
								dir0++;
							}else if(dirOcean[cols-1][rows] == 1){
								dir1++;
							}
							if(dirOcean[cols+1][rows] == 0){
								dir0++;
							}else if(dirOcean[cols+1][rows] == 1){
								dir1++;
							}
							if(dirOcean[cols][rows-1] == 0){
								dir0++;
							}else if(dirOcean[cols][rows-1] == 1){
								dir1++;
							}
							if(dirOcean[cols][rows+1] == 0){
								dir0++;
							}else if(dirOcean[cols][rows+1] == 1){
								dir1++;
							}
							
							if(dirOcean[cols-1][rows-1] == 0){
								dir0++;
							}else if(dirOcean[cols-1][rows-1] == 1){
								dir1++;
							}
							if(dirOcean[cols+1][rows-1] == 0){
								dir0++;
							}else if(dirOcean[cols+1][rows-1] == 1){
								dir1++;
							}
							if(dirOcean[cols-1][rows+1] == 0){
								dir0++;
							}else if(dirOcean[cols-1][rows+1] == 1){
								dir1++;
							}
							if(dirOcean[cols+1][rows+1] == 0){
								dir0++;
							}else if(dirOcean[cols+1][rows+1] == 1){
								dir1++;
							}
							
							if(dir0 > dir1){
								if(random.nextInt(3) == 1){
									dirOcean[cols][rows] = 0;
								}
							}else if(dir0 < dir1){
								if(random.nextInt(3) == 1){
									dirOcean[cols][rows] = 1;
								}
							}else if(dir0 == dir1 && dir0>0){
								if(random.nextInt(3) == 1){
									dirOcean[cols][rows] = random.nextInt(2);
								}
							}
							
							oceanClear = false;
						}
					}
				}
				if(oceanClear){
					System.out.println("clear!");
					break loop;
				}
			}
	}
	
	public static void genOcean(int amount, int[][] field, int[][] dir){
		for(int i=0; i<mapY; i++){
			field[0][i] = 0;
			field[mapX-1][i] = 0;
		}
		int counter = 0;
		int vcount = 0;
		int hcount = 0;
		for(int x = 0; x<amount; x++){
			for(int rows=0; rows<mapY; rows++){
				for(int cols=0; cols<mapX; cols++){
					if(cols>0 && rows>0 && cols<mapX-1 && rows<mapY-1){
						counter = 0;
						vcount = 0;
						hcount = 0;
						{
							if(field[cols-1][rows] == 0){
								vcount++;
							}
							if(field[cols+1][rows] == 0){
								vcount++;
							}
							if(field[cols][rows-1] == 0){
								hcount++;
							}
							if(field[cols][rows+1] == 0){
								hcount++;
							}
							
							if(field[cols-1][rows-1] == 0){
								counter++;
							}
							if(field[cols+1][rows-1] == 0){
								counter++;
							}
							if(field[cols-1][rows+1] == 0){
								counter++;
							}
							if(field[cols+1][rows+1] == 0){
								counter++;
							}
							
						}
						
						if(dir[cols][rows] == 1){
							if(hcount > 0){
								if(rows < (mapY/20)){ // || rows > ((mapY-1)-(mapY/20))
									conc = random.nextInt(3);
								}else{
									conc = random.nextInt(9-(hcount+counter+vcount));
								}
								if(conc == 0 || conc == 1 || conc == 2){
									field[cols][rows] = 0;
								}
							}else if(counter > 0){
								conc = random.nextInt(30-counter)/2;
								if(conc == 0 || conc == 1 || conc == 2){
									field[cols][rows] = 0;
								}
							}
						}else{
							if(vcount > 0){
								if(rows < (mapY/20)){ // || rows > ((mapY-1)-(mapY/20))
									conc = random.nextInt(3);
								}else{
									conc = random.nextInt(9-(hcount+counter+vcount));
								}
								if(conc == 0 || conc == 1 || conc == 2){
									field[cols][rows] = 0;
								}
							}else if(counter > 0){
								conc = random.nextInt(30-counter)/2;
								if(conc == 0 || conc == 1 || conc == 2){
									field[cols][rows] = 0;
								}
							}
						}
						if(field[cols][rows] == 0){
							dirLand[cols][rows] = 3;
						}
						
					}
				}
			}
		}
	}
	
	public static void genLand(int amount, int[][] field){
		int counter = 0;
		int vcount = 0;
		int hcount = 0;
		int dir0 = 0;
		int dir1 = 0;
		int nextX = 1;
		int nextY = 1;
		int prevX = -1;
		int prevY = -1;
		for(int x = 0; x<amount; x++){
			for(int rows=0; rows<mapY; rows++){
				for(int cols=0; cols<mapX; cols++){
						counter = 0;
						vcount = 0;
						hcount = 0;
						dir0 = 0;
						dir1 = 0;
						if(rows == 0)
							//prevY = (mapY-2);
							prevY = 0;
						else prevY = -1;
						if(rows == (mapY-1))
							//nextY = -(mapY-2);
							nextY = 0;
							else nextY = 1;
						if(cols == 0)
							prevX = (mapX-2);
						else prevX = -1;
						if(cols == (mapX-1))
							nextX = -(mapX-2);
						else nextX = 1;
						{
							if(field[cols+prevX][rows] == 1){
								hcount++;
								if(dirLand[cols+prevX][rows] == 0)
									dir0++;
								else if(dirLand[cols+prevX][rows] == 1)
									dir1++;
							}
							if(field[cols+nextX][rows] == 1){
								hcount++;
								if(dirLand[cols+nextX][rows] == 0)
									dir0++;
								else if(dirLand[cols+nextX][rows] == 1)
									dir1++;
							}
							if(field[cols][rows+prevY] == 1){
								vcount++;
								if(dirLand[cols][rows+prevY] == 0)
									dir0++;
								else if(dirLand[cols][rows+prevY] == 1)
									dir1++;
							}
							if(field[cols][rows+nextY] == 1){
								vcount++;
								if(dirLand[cols][rows+nextY] == 0)
									dir0++;
								else if(dirLand[cols][rows+nextY] == 1)
									dir1++;
							}
							
							if(field[cols+prevX][rows+prevY] == 1){
								counter++;
								if(dirLand[cols+prevX][rows+prevY] == 0)
									dir0++;
								else if(dirLand[cols+prevX][rows+prevY] == 1)
									dir1++;
							}
							if(field[cols+nextX][rows+prevY] == 1){
								counter++;
								if(dirLand[cols+nextX][rows+prevY] == 0)
									dir0++;
								else if(dirLand[cols+nextX][rows+prevY] == 1)
									dir1++;
							}
							if(field[cols+prevX][rows+nextY] == 1){
								counter++;
								if(dirLand[cols+prevX][rows+nextY] == 0)
									dir0++;
								else if(dirLand[cols+prevX][rows+nextY] == 1)
									dir1++;
							}
							if(field[cols+nextX][rows+nextY] == 1){
								counter++;
								if(dirLand[cols+nextX][rows+nextY] == 0)
									dir0++;
								else if(dirLand[cols+nextX][rows+nextY] == 1)
									dir1++;
							}
							
						}
						
						if(dir0 > dir1){
							dirLand[cols][rows] = 0;
						}else if(dir1 > dir0){
							dirLand[cols][rows] = 1;
						}else{
							dirLand[cols][rows] = random.nextInt(2);
						}
						
						if(dirLand[cols][rows] == 1){
							if(hcount > 0){
								if(rows < (mapY/20)){ // || rows > ((mapY-1)-(mapY/20))
									conc = random.nextInt(3);
								}else{
									conc = random.nextInt(configData[1]-(hcount+counter+vcount));
								}
								if(conc == 0 || conc == 1){
									field[cols][rows] = 1;
								}
							}else if(counter > 0){
								conc = random.nextInt(configData[0]-counter);
								if(conc == 0 || conc == 1){
									field[cols][rows] = 1;
								}
							}
						}else{
							if(vcount > 0){
								if(rows < (mapY/20)){ // || rows > ((mapY-1)-(mapY/20))
									conc = random.nextInt(3);
								}else{
									conc = random.nextInt(configData[1]-(hcount+counter+vcount));
								}
								if(conc == 0 || conc == 1){
									field[cols][rows] = 1;
								}
							}else if(counter > 0){
								conc = random.nextInt(configData[0]-counter);
								if(conc == 0 || conc == 1){
									field[cols][rows] = 1;
								}
							}
						}
						
						if(field[cols][rows] == 0)
							dirLand[cols][rows] = 3;
				}
			}
		}
	}
	
	public static void fixLand(int amount, int[][] field){
		int counter = 0;
		for(int x=0; x<amount; x++){
			counter = 0;
			for(int i=0; i<mapX; i++){
				for(int j=0; j<mapY; j++){
					if(i>0 && j>0 && i<mapX-1 && j<mapY-1){
						if(i<12 || j<12){
							conc = random.nextInt(5);
						}
						counter = 0;
						if(field[i-1][j] == 1)
							counter++;
						if(field[i+1][j] == 1)
							counter++;
						
						if(counter == 2){
							conc = random.nextInt(3);	
							
							if(conc == 0 || conc == 1)
								field[i][j] = 1;
						}else if(counter == 0){
							conc = random.nextInt(2);	
							
							if(conc == 0 || conc == 1)
								field[i][j] = 0;
						}
						

						counter = 0;
						if(field[i][j-1] == 1)
							counter++;
						if(field[i][j+1] == 1)
							counter++;
						
						if(counter == 2){
							conc = random.nextInt(3);	
							
							if(conc == 0 || conc == 1)
								field[i][j] = 1;
						}else if(counter == 0){

							conc = random.nextInt(2);	
							
							if(conc == 0 || conc == 1)
								field[i][j] = 0;
						}
					}
				}
			}
		}
	}
}
//mark stewart
//May-8-2020
//Density Lab Program
//This simulation is designed to simulate the real world a little better than other simulations. The scales have a spread on their readings and one of the scales reads incorrectly.
//The technique might not be obvious.
//Volume is measured by suspending the mass in water. The scale will read higher to due the extra displaced water.
//Since the density of water is known, the volume of the object can be determined.

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;

public class DensityLabAp extends JFrame implements ActionListener, KeyListener
{
	private static final long serialVersionUID = -1; 

	JComboBox<String> whatToMeasure;
	JTextArea sampleName; JTextArea feedback;
	JLabel[] scaleTitle; JLabel[] reading; JLabel[] imageLabel = new JLabel[3];
	JButton[] getReading;	JButton takeAction;

	
	ImageIcon[] im=new ImageIcon[3];
	BufferedImage[] img = new BufferedImage[3];

	JPanel left,right; JPanel[] scale;

	double[] red = new double[2]; double[] mass = new double[3]; 

	double density; double p = 0.05;
	int b; int whichScaleHasMass=-1; int whichScaleHasWater=-1;
	boolean massOnBottom=false, massImmersed=false;

	Color color_dg;

	public static void main(String[] args)
	{
		DensityLabAp labAp = new DensityLabAp();
	}
	public DensityLabAp() {
		setTitle("Improved simulations: Density Lab");
		b = (int)(3*Math.random());
		Font font = new Font("Serif",Font.PLAIN,20);

		feedback = new JTextArea("No feedback yet");	
		feedback.setLineWrap(true);
		feedback.setEditable(false);
		feedback.setPreferredSize(new Dimension(350,100)); //used to be 300
		feedback.setBorder(new LineBorder(color_dg, 1, true));
		feedback.setFont(font);
		feedback.setLineWrap(true); feedback.setWrapStyleWord(true);

		left = new JPanel(); right = new JPanel();
		left.setLayout(new FlowLayout()); right.setLayout(new GridLayout(3,1));

		scale = new JPanel[3]; 
		scaleTitle = new JLabel[3]; 
		reading = new JLabel[3]; 
		getReading = new JButton[3];

		for (int i=0;i<scale.length;i++)
		{
			imageLabel[i] = new JLabel(""); imageLabel[i].setHorizontalAlignment(JLabel.CENTER);

			scale[i] = new JPanel(); scale[i].setOpaque(true); scale[i].setBackground(new Color(255,255,255));
			scale[i].setBorder(BorderFactory.createMatteBorder(4,4,4,4,new Color(100,155,100) ));
			scale[i].setLayout(new FlowLayout());
		
			scaleTitle[i] = new JLabel("Scale "+(i+1)+"-empty"); scaleTitle[i].setHorizontalAlignment(JLabel.CENTER); scaleTitle[i].setPreferredSize(new Dimension(300,50));
			getReading[i] = new JButton("make mmt");
			getReading[i].addActionListener(this); getReading[i].setActionCommand(i+""); getReading[i].setBackground(new Color(200,255,200));
			reading[i] = new JLabel("-----"); reading[i].setHorizontalAlignment(JLabel.CENTER); reading[i].setPreferredSize(new Dimension(300,50));
			scale[i].add(imageLabel[i]); scale[i].add(scaleTitle[i]); scale[i].add(getReading[i]); scale[i].add(reading[i]);
		}
		makeImages();
		right.add(scale[0]); right.add(scale[1]); right.add(scale[2]);
		
		String name = getCode();
		convert(name);

		String message = "Goal: To measure the mass density of sample to within 0.5%.\n\n"+"Sample Name="+name+"\n\n";
		message +="To do your labwork, select an action then click take action."; 
		message +=" \n\nNote: each time you place water on the scale, a new amount of water is provided.";
		message +=" \n\nNote: One of the three scales is malfunctioning.";
		sampleName = new JTextArea(message);
		sampleName.setLineWrap(true);
		sampleName.setEditable(false);
		sampleName.setPreferredSize(new Dimension(350,400)); //used to be 300
		sampleName.setBorder(new LineBorder(color_dg, 1, true));
		sampleName.setFont(font);
		sampleName.setLineWrap(true); sampleName.setWrapStyleWord(true);
		sampleName.setMargin(new Insets(10,10,10,10)); //not working

		takeAction = new JButton("Click to take action..");
		takeAction.addActionListener(this); takeAction.setActionCommand("Take Action"); takeAction.setBackground(new Color(200,255,200));

		JPanel topLeft = new JPanel(); topLeft.add(sampleName);
		left.add(topLeft); 

		String[] option = new String[9];
		option[0]="A. put sample on scale 1";
		option[1]="B. put sample on scale 2";
		option[2]="C. put sample on scale 3";
		option[3]="D. put water tub on scale 1";
		option[4]="E. put water tub on scale 2";
		option[5]="F. put water tub on scale 3";
		option[6]="G. put sample into water tub";
		option[7]="H. immerse mass in H2O, but suspended above bottom";
		option[8]="I. Remove everything from the scales.";
		
		whatToMeasure = new JComboBox<String>(option);
		whatToMeasure.setPreferredSize(new Dimension(400,30));
		whatToMeasure.setBorder(new LineBorder(color_dg, 1, false));
		whatToMeasure.setBackground(new Color(200,255,200));
		whatToMeasure.setMaximumRowCount(9);
		JPanel centerLeft = new JPanel(); JPanel feedbackPanel= new JPanel(); feedbackPanel.add(feedback); JPanel takeActionPanel = new JPanel(); takeActionPanel.add(takeAction);
		centerLeft.setLayout(new BoxLayout(centerLeft, BoxLayout.Y_AXIS)); centerLeft.add(whatToMeasure); centerLeft.add(feedbackPanel);centerLeft.add(takeActionPanel);

		left.add(centerLeft);
		
		Container contentPane = getContentPane(); contentPane.setBackground(new Color(100,155,100));
		getRootPane().setBorder(BorderFactory.createMatteBorder(4,4,4,4,new Color(100,155,100)));
		contentPane.setLayout(new GridLayout(1,2));
		contentPane.add(left); contentPane.add(right);

		setSize(800,800);		//should be proportional to screen size
		setLocation(100,100);		//ditto
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);	
	}

	public void actionPerformed(ActionEvent e)
	{
		feedback.setText(" ");
		if (e.getActionCommand().equals("Take Action")) {
			char a =((String)whatToMeasure.getSelectedItem()).charAt(0);
			switch (a) {
				case 'A': resetScales(); scaleTitle[0].setText("Scale 1 - with mass");mass[0] = getMass(0); whichScaleHasMass=0; makeImages(); break;
				case 'B': resetScales(); scaleTitle[1].setText("Scale 2 - with mass");mass[1] = getMass(1); whichScaleHasMass=1; makeImages(); break;
				case 'C': resetScales(); scaleTitle[2].setText("Scale 3 - with mass");mass[2] = getMass(2); whichScaleHasMass=2; makeImages(); break;
				case 'D': resetScales(); scaleTitle[0].setText("Scale 1 - with water tub");mass[0] = getWaterMass(0); whichScaleHasWater=0;  makeImages();break;
				case 'E': resetScales(); scaleTitle[1].setText("Scale 2 - with water tub");mass[1] = getWaterMass(1); whichScaleHasWater=1;  makeImages();break;
				case 'F': resetScales(); scaleTitle[2].setText("Scale 3 - with water tub");mass[2] = getWaterMass(2); whichScaleHasWater=2;  makeImages();break;
				case 'G': 
					if (whichScaleHasWater==-1) feedback.setText("No water tubs on scale. Put water tub on scale before immersing mass.");
					else if (whichScaleHasMass!=-1) feedback.setText("Mass already in water tub.");
					else {scaleTitle[whichScaleHasWater].setText("Scale "+(1+whichScaleHasWater)+"- with water tub and mass at bottom"); 
						mass[whichScaleHasWater]+=getMass(whichScaleHasWater);
						whichScaleHasMass = whichScaleHasWater;
						massOnBottom=true;
						makeImages();
					}
					break;
				case 'H': 
					if (whichScaleHasWater==-1) feedback.setText("No water tubs on scale. Put water tub on scale before immersing mass.");
					else if (whichScaleHasMass!=-1) feedback.setText("Mass already in water tub.");
					else {scaleTitle[whichScaleHasWater].setText("Scale "+(1+whichScaleHasWater)+"- with water tub and mass immersed but suspended"); 
						mass[whichScaleHasWater]+=getDisplacedWater(whichScaleHasWater);
						massImmersed=true;
						makeImages();
						whichScaleHasMass = whichScaleHasWater;
					}
					break;
				case 'I':
					resetScales();
					break;
				default: break;
			}
		}
		else {
			int A = Integer.parseInt(e.getActionCommand());
			reading[A].setText(mass[A]+" g");
		}
	}
	
	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {}
	public void keyReleased(KeyEvent k) {}

	void convert(String s) {
		red[0] = 2+Math.abs((s.hashCode()%1000)/100.0);
		red[1] = 20+20*Math.random();	
	}
	void resetScales() {
		whichScaleHasMass = -1;
		whichScaleHasWater =-1;
		for (int i=0;i<3;i++) {
			scaleTitle[i].setText("Scale "+(i+1)+" - empty");mass[i] = 0;
			reading[i].setText("-----");
		}
		massOnBottom=false; massImmersed=false;
		makeImages();
	}

	double getMass(int which) {
		double[] m = new double[3];
		double r = red[1]*red[0];
		m[(b+0)%3] = r-p*r + 2*r*p*Math.random();	//uniform distribution. A normal distribution might be more realistic
		m[(b+1)%3] = r-p*r + 2*r*p*Math.random();
		m[(b+2)%3] = (r-p*r + 2*r*p*Math.random())+10;
		return m[which];	
	}

	double getDisplacedWater(int which) {
		double[] m = new double[3];
		double r = 1*red[1];
		m[(b+0)%3] = r-p*r + 2*r*p*Math.random();
		m[(b+1)%3] = r-p*r + 2*r*p*Math.random();
		m[(b+2)%3] = (r-p*r + 2*r*p*Math.random())+10;
		return m[which];	
	}
	double getWaterMass(int which) {
		double[] m = new double[3];
		double water = 100+50*Math.random();
		m[(b+0)%3] = water-p*water + 2*water*p*Math.random();
		m[(b+1)%3] = water-p*water + 2*water*p*Math.random();
		m[(b+2)%3] = (water-p*water + 2*water*p*Math.random() )+10;
		return m[which];	
	}
	String getCode() {	//the code is used to generate the density. The teacher would also need to able to do this.
		String s="";
		for (int i=0;i<4;i++) {
			s+= (char)((int)('A')+(int)(26*Math.random())); 
		}	
		return s;		
	}

	public void makeImages() {
		int sizeX=120,sizeY=100;
		for (int i=0;i<3;i++) {
			img[i] = new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_INT_RGB);
			img[i].createGraphics();
			Graphics2D g = (Graphics2D)img[i].getGraphics();
			g.setColor(new Color(200,220,200));
			g.fillRect(0,0,sizeX,sizeY);
			g.setColor(Color.BLACK);
			g.drawRect(20,80,80,10);
			g.drawLine(60,80,60,70);
			g.drawLine(20,70,100,70);

			g.setColor(Color.BLACK);
			if (whichScaleHasMass==i) {g.fillOval(50,50,20,20);}

			if (!(massOnBottom || massImmersed) && whichScaleHasWater==i) { //draw mass on scale
				g.setColor(Color.BLUE);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,2*.1f)); //makes it semi-transparent
				g.fillRect(30,20,60,50);
			}

			if (massOnBottom && (whichScaleHasWater==i)) { //draw mass inside water at bottom
				g.fillOval(50,50,20,20);
				g.setColor(Color.BLUE);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,2*.1f)); 
				g.fillRect(30,20,60,50);
			}
			if (massImmersed && (whichScaleHasWater==i)) { //draw mass immersed but not touching bottom
				g.setColor(Color.BLACK);
				g.fillOval(50,40,20,20);
				g.drawLine(60,0,60,50);
				g.setColor(Color.BLUE);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,2*.1f)); 
				g.fillRect(30,20,60,50);
			}
			im[i] = new ImageIcon(img[i]);
			imageLabel[i].setIcon(im[i]);
		} 
	}

}

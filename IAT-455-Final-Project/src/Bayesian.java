import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.lang.String; 

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Bayesian extends Frame implements MouseListener{

	BufferedImage mandy;
	BufferedImage mandybackground;
	BufferedImage mandyMatte;
	BufferedImage backgroundImg1, backgroundImg2, backgroundImg3, backgroundImg4;
	BufferedImage backgroundImg1btn, backgroundImg2btn, backgroundImg3btn, backgroundImg4btn;
	BufferedImage mandyMatteFinal;
	BufferedImage selected, resizedSelected;
	BufferedImage plainBg;

	
	int width, height;
	//boolean lumK, colDif, difMat, chromK;
	int type = 0;
	int bgType = 0;
	
	BufferedImage luminance;
	BufferedImage chromaKey;
	
	BufferedImage colourDifferenceMatte;
	BufferedImage suppressImg;
	BufferedImage updateBackgroundImage;
	BufferedImage colorDiffResultImage;
	BufferedImage finalLumaImg;
	BufferedImage finalChromaImg;
	
	BufferedImage differenceMatting;
	BufferedImage subtractedImage;
	BufferedImage overImage;
	public Bayesian(String selectedPath){
		//constructor
		
		try {
			
			mandy = ImageIO.read(new File("images/img3.jpg"));
			
			mandybackground = ImageIO.read(new File("images/img3-bg.jpg"));
			backgroundImg1 = ImageIO.read(new File("images/bg-img1.jpg"));
			backgroundImg2 = ImageIO.read(new File("images/bg-img2.jpg"));
			backgroundImg3 = ImageIO.read(new File("images/bg-img3.jpg"));
			backgroundImg4 = ImageIO.read(new File("images/bg-img4.jpg"));

			backgroundImg1btn = ImageIO.read(new File("images/bg-img1.jpg"));
			backgroundImg2btn = ImageIO.read(new File("images/bg-img2.jpg"));
			backgroundImg3btn = ImageIO.read(new File("images/bg-img3.jpg"));
			backgroundImg4btn = ImageIO.read(new File("images/bg-img4.jpg"));
			selected = ImageIO.read(new File(selectedPath));
			
			plainBg = ImageIO.read(new File("images/plainwhitebg.jpg"));
			

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Final Project");
		this.setVisible(true);
		
		width = mandy.getWidth();
		height = mandy.getHeight();
		addMouseListener(this);
		
		
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
		
		resizedSelected = resize(selected, width, height);
		luminance = getLuminance(resizedSelected);
		chromaKey = chromaKey(resizedSelected);
		
		//colour difference method
		suppressImg = suppressToBlack(resizedSelected);
		colourDifferenceMatte = createColourDifference(resizedSelected);
		
		//difference matting 
		subtractedImage = combineImages(resizedSelected, plainBg, Operations.subtract);
		differenceMatting = createDifferenceMatting(subtractedImage);
//		overImage = over(mandy, differenceMatting, backgroundImg1);
		
	}
	
	boolean appropriateImage;
	public boolean imageCheck (BufferedImage src) { //this checks if the 
		//image selected by the user is an appropriate size
		//if appropriate image is false, either scale down the image, or display error
		if (src.getHeight() == height && src.getWidth() == width) {
			appropriateImage = true;
		} else {
			appropriateImage = false;
		}
		return appropriateImage;
	}
	
	public BufferedImage suppressToBlack(BufferedImage src) {//turning the background to black
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		
		int red, blue, green;
		for (int i = 0; i < src.getWidth(); i++){ 
			for (int j = 0; j < src.getHeight(); j++) {
				int rgb = src.getRGB(i, j);
				red = getRed(rgb);
				blue = getBlue(rgb);
				green = getGreen(rgb);
				
				//chroma keying out the green screen
				if(green > blue && green > red){
					green = 0;
					red = 0;
					blue = 0;
				}
				
				result.setRGB(i, j, new Color(red, green, blue).getRGB());
			}
		}
		
		return result;
	}
	
	//creating the invert of the matte
	public BufferedImage invert(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(),
				src.getHeight(), src.getType());

		int red1;
		int green1;
		int blue1;
		int newRed, newGreen, newBlue;
		
		for(int i = 0; i < src.getWidth(); i++){ 
			for(int j = 0; j < src.getHeight(); j++) {
				int rgb = src.getRGB(i, j);
		
				//rgb values of car image
				red1 = getRed(rgb);
				green1 = getGreen(rgb);
				blue1 = getBlue(rgb);

				newRed = 255 - red1;
				newGreen = 255 - green1;
				newBlue = 255 - blue1;				
				result.setRGB(i, j, new Color(newRed, newGreen, newBlue).getRGB());
				
				}
			  }
		return result;
			}
	
		
	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2, Operations op)
	{
		BufferedImage result = new BufferedImage(src1.getWidth(),src1.getHeight(), src1.getType());
		int resultHeight = result.getHeight();
		int resultWidth = result.getWidth();
		int newR, newG, newB;
		for (int i = 0; i < resultWidth; i++) {
			for (int j = 0; j < resultHeight; j++) {
				int rgb, rgb2;
				int r, g, b;
				switch(op) {
				case multiply:			
					rgb = src1.getRGB(i, j);
					rgb2 = src2.getRGB(i, j);
	
					newR = (getRed(rgb) * getRed(rgb2)) /255;
					newG = (getGreen(rgb) * getGreen(rgb2)) /255;
					newB = (getBlue(rgb) * getBlue(rgb2)) /255;
				
					 r = clip(newR);
					 g = clip(newG);
					 b = clip(newB);
				
					result.setRGB(i, j, new Color(r, g, b).getRGB());		
					break; 
				
				case add: 
					 rgb = src1.getRGB(i, j);
					 rgb2 = src2.getRGB(i, j);
				
					newR = (getRed(rgb) + getRed(rgb2));
					newG = (getGreen(rgb) + getGreen(rgb2));
					newB = (getBlue(rgb) + getBlue(rgb2));
				
					 r = clip(newR);
					 g = clip(newG);
					 b = clip(newB);
				
					result.setRGB(i, j, new Color(r, g, b).getRGB());
					break;
					
				case subtract: 
					 rgb = src1.getRGB(i, j);
					 rgb2 = src2.getRGB(i, j);
				
					newR = Math.abs((getRed(rgb) - getRed(rgb2)));
					newG = Math.abs((getGreen(rgb) - getGreen(rgb2)));
					newB = Math.abs((getBlue(rgb) - getBlue(rgb2)));
					
					 r = clip(newR);
					 g = clip(newG);
					 b = clip(newB);
				
					result.setRGB(i, j, new Color(r, g, b).getRGB());
					break;
					
				}
			}
		}
		
		// Write your code here
		
		return result;
	}
	

	 
	 public BufferedImage getLuminance(BufferedImage src1)//combining the first suppressed black image with the cutout
		{
			BufferedImage result = new BufferedImage(src1.getWidth(),src1.getHeight(), src1.getType());

			// Write your code here
			int red, blue, green;
			float luminance;
			for (int i =0; i < src1.getWidth(); i++){ 
				for (int j = 0; j < src1.getHeight(); j++) {
					int rgb1 = src1.getRGB(i, j);
					
					red = getRed(rgb1);
					green = getGreen(rgb1);
					blue = getBlue(rgb1);
					
					luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
					
					if(luminance >= 0.5f){//creating matte based on the luminance. Change the number to change sensitivity
						red = 255;
						green = 255;
						blue = 255;
					} else {
						red = 0;
						blue = 0;
						green = 0;
					}
						
					result.setRGB(i, j, new Color(red, green, blue).getRGB());
				}
			}
			
			return result;
		}
	 
	 public BufferedImage chromaKey(BufferedImage src1){

			BufferedImage result = new BufferedImage(src1.getWidth(),src1.getHeight(), src1.getType());

			// Write your code here
			int red, blue, green;
			int kred, kblue, kgreen;
			
			int krgb = src1.getRGB(width-5, 5);//the key pixel (arbitrarily chosen atm)
			
			kred = getRed(krgb);
			kgreen = getGreen(krgb);
			kblue = getBlue(krgb);
			
			for (int i =0; i < src1.getWidth(); i++){ 
				for (int j = 0; j < src1.getHeight(); j++) {
					int rgb1 = src1.getRGB(i, j);
					
					red = getRed(rgb1);
					green = getGreen(rgb1);
					blue = getBlue(rgb1);
					
					
					if(red < kred - 3 || red > kred + 3 &&//comparing each pixel to a colour range from the key pixel
						green < kgreen - 3|| green > kgreen + 3 &&//Change the numbers of the range to change sensitivity
						blue < kblue - 3 || blue > kblue + 3){
						
						red = 255;
						green = 255;
						blue = 255;
					} else {
						
						red = 0;
						green = 0;
						blue = 0;
					}
					
					result.setRGB(i, j, new Color(red, green, blue).getRGB());
				}
			}
			
			return result;
	 }
	 
	 
	 public BufferedImage createColourDifference(BufferedImage src) {
			BufferedImage colourDifference = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
			int colourDifferenceMatteHeight = colourDifference.getHeight();
			int colourDifferenceMatteWidth = colourDifference.getWidth();
			int newR, newG, newB;
			
			for (int i = 0; i < colourDifferenceMatteWidth; i++) {
				for (int j = 0; j < colourDifferenceMatteHeight; j++) {
					int rgb = src.getRGB(i, j);
					newR = getRed(rgb);
					newG = getGreen(rgb);
					newB = getBlue(rgb);
					
					int matte = newB - Math.max(newG, newR);
					int newMatte = clip (matte);
					colourDifference.setRGB(i, j, new Color(newMatte, newMatte, newMatte).getRGB());
				}
			}
			return colourDifference;
		}
	 public BufferedImage createDifferenceMatting(BufferedImage src) {
			BufferedImage diffMatte = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
			int diffMatteWidth = diffMatte.getWidth();
			int diffMatteHeight = diffMatte.getHeight();
			for (int i = 0; i < diffMatteWidth; i++) {
				for (int j = 0; j < diffMatteHeight; j++) {				
					int rgb = src.getRGB(i, j);
					int r = getRed(rgb);
					int g = getGreen(rgb);
					int b = getBlue(rgb);
					float[] hsb = Color.RGBtoHSB(r, g, b, null);
					float newBrightness = hsb[2] > 0.05 ? 1 : hsb[2];
					int newRgb = Color.HSBtoRGB(hsb[0], 0, newBrightness);
					diffMatte.setRGB(i, j, newRgb);					
				}
			}
			return diffMatte;
		}
		public BufferedImage over(BufferedImage foreground, BufferedImage matte, BufferedImage background) {
			BufferedImage endResult = new BufferedImage(foreground.getWidth(), foreground.getHeight(), foreground.getType());
			int foreWidth = foreground.getWidth();
			int foreHeight = foreground.getHeight();
			int newR, newG, newB;
			
			
			for (int i = 0; i < foreWidth; i++) {
				for (int j = 0; j < foreHeight; j++) {
					
					int rgb = foreground.getRGB(i, j);
					int rgb2 = matte.getRGB(i, j);
					int rgb3 = background.getRGB(i, j);
					
					newR = (getRed(rgb) * getRed(rgb2))/255 + getRed(rgb3) - getRed(rgb3) * getRed(rgb2)/255;
					newG = (getGreen(rgb) * getGreen(rgb2))/255 + getGreen(rgb3) - getGreen(rgb3) * getGreen(rgb2)/255;
					newB = (getBlue(rgb) * getBlue(rgb2))/255 + getBlue(rgb3) - getBlue(rgb3) * getBlue(rgb2)/255;
					int r = clip (newR);
					int g = clip (newG);
					int b = clip (newB);
					endResult.setRGB(i, j, new Color(r, g, b).getRGB());
					}
				}
			return endResult;
		}
		
		public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
		    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    g2d.dispose();

		    return dimg;
		}  
		
		
		public void typeCheck (int type) { //this checks user selection + updates image displayed.
			if(type == 0){
				mandyMatte = getLuminance(selected);
				mandyMatteFinal = finalLumaImg;
			}
			if(type == 1){
				mandyMatte = createColourDifference(selected);
			    mandyMatteFinal = colorDiffResultImage;
			}
			if(type == 2){
				mandyMatte = createDifferenceMatting(selected);
				mandyMatteFinal = overImage;
				
			}
			if(type == 3){
				mandyMatte = chromaKey(selected);
				mandyMatteFinal = finalChromaImg;
			}
			updateBackgroundImage = combineImages(colourDifferenceMatte, backgroundImg1, Operations.multiply);
			colorDiffResultImage = combineImages(suppressImg, updateBackgroundImage, Operations.add);

			finalLumaImg = over(resizedSelected, luminance, backgroundImg1);
			finalChromaImg = over(resizedSelected, chromaKey, backgroundImg1);
			
			overImage = over(resizedSelected, differenceMatting, backgroundImg1);
		}
		
		public void bgTypeCheck (int bgType) { //this checks user selection + updates image displayed.
			if(bgType == 0){
				backgroundImg1 = backgroundImg1btn;
				backgroundImg2 = backgroundImg1btn;
				backgroundImg3 = backgroundImg1btn;
				backgroundImg4 = backgroundImg1btn;
			}
			if(bgType == 1){
				backgroundImg1 = backgroundImg2btn;
				backgroundImg2 = backgroundImg2btn;
				backgroundImg3 = backgroundImg2btn;
				backgroundImg4 = backgroundImg2btn;
			}
			if(bgType == 2){
				backgroundImg1 = backgroundImg3btn;
				backgroundImg2 = backgroundImg3btn;
				backgroundImg3 = backgroundImg3btn;
				backgroundImg4 = backgroundImg3btn;
			}
			if(bgType == 3){
				backgroundImg1 = backgroundImg4btn;
				backgroundImg2 = backgroundImg4btn;
				backgroundImg3 = backgroundImg4btn;
				backgroundImg4 = backgroundImg4btn;
			}
			

			updateBackgroundImage = combineImages(colourDifferenceMatte, backgroundImg1, Operations.multiply);
			colorDiffResultImage = combineImages(suppressImg, updateBackgroundImage, Operations.add);

			finalLumaImg = over(resizedSelected, luminance, backgroundImg1);
			finalChromaImg = over(resizedSelected, chromaKey, backgroundImg1);
			
			overImage = over(resizedSelected, differenceMatting, backgroundImg1);
			
		}
		
	 
	public void paint(Graphics g){
		//java window
		int w = width/2;
		int h = width/2;
		
		this.setSize(w * 7, h * 5);
		bgTypeCheck(bgType);
		typeCheck(type);

		g.drawImage(resizedSelected, w + 20, 50, w, h, this);
		g.drawImage(mandyMatte, w + 20, 300, w, h, this);
		g.drawImage(backgroundImg2, w + 20, 550, w, h, this);
		g.drawImage(mandyMatteFinal, w + 20, 780, w, h, this);
		
		g.setColor(Color.BLACK);
	    Font f1 = new Font("Verdana", Font.PLAIN, 15);  
	    g.setFont(f1);
	    
	    g.drawString("Lumakey", w * 5, 90);
	    g.drawString("Colour Difference Matte", w * 5, 120);
	    g.drawString("Difference Matting", w * 5, 150);
	    g.drawString("Chromakey", w * 5, 180);
	    g.drawString("[Click Here to Save IMG]", w * 5, 680);
	    g.drawString("**Please ensure your images have a plain white background for optimal performance. **", w * 3, 720);
	    g.drawImage(resizedSelected, w*5, 780, w, h, this);
	    
	    g.drawImage(backgroundImg1btn, 940, 220, w, h, this);
		g.drawImage(backgroundImg2btn, 1150, 220, w, h, this);
		g.drawImage(backgroundImg3btn, 940, 430, w, h, this);
		g.drawImage(backgroundImg4btn, 1150, 430, w, h, this);
	    
	}
	
	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int pixel) {
		return (pixel >>> 16) & 0xFF;
	}

	protected int getGreen(int pixel) {
		return (pixel >>> 8) & 0xFF;
	}

	protected int getBlue(int pixel) {
		return pixel & 0xFF;
	}
	
	public static void main(String[] args){
		//calling the constructor/paint methods
		JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    int result = fileChooser.showOpenDialog(fileChooser);
	    if(result == JFileChooser.APPROVE_OPTION){
	    	File selectedFile = fileChooser.getSelectedFile();
	    	System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	    	Bayesian img = new Bayesian(selectedFile.getAbsolutePath());
			img.repaint();
	    }
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		if(e.getButton() == MouseEvent.BUTTON1){
		if(x >= width * 2.5 && x <= width * 2.5 + 100 && y >= 75 && y <= 90){
			/*lumK = true;
			colDif = false;
			difMat = false;
			chromK = false;*/
			type = 0;

			System.out.println(type);
		}
		if(x >= width * 2.5 && x <= width * 2.5 + 100 && y >= 105 && y <= 120){
			/*lumK = false;
			colDif = true;
			difMat = false;
			chromK = false;
			System.out.println("Lumakey " + lumK);*/
			type = 1;
			System.out.println(type);
		}
		if(x >= width * 2.5 && x <= width * 2.5 + 100 && y >= 135 && y <= 150){
			/*lumK = false;
			colDif = false;
			difMat = true;
			chromK = false;*/
			type = 2;
			System.out.println(type);
		}
		if(x >= width * 2.5 && x <= width * 2.5 + 100 && y >= 165 && y <= 180){
			/*lumK = false;
			colDif = false;
			difMat = false;
			chromK = true;*/
			type = 3;
			System.out.println(type);
		}
		
		if (x >= width * 2.5 - 60 && x <= width * 2.5 + 140 && y >= 200 && y <= 420) {
			bgType = 0;
			System.out.println(bgType); //bgimg1
		}		
		if (x >= width * 2.5 + 150 && x <= width * 2.5 + 350 && y >= 200 && y <= 420) {
			bgType = 1;
			System.out.println(bgType); //bgimg2
		}
		if (x >= width * 2.5 - 60 && x <= width * 2.5 + 140 && y >= 430 && y <= 630) {
			bgType = 2;
			System.out.println(bgType); //bgimg3
		}
		if (x >= width * 2.5 + 150 && x <= width * 2.5 + 350 && y >= 430 && y <= 630) {
			bgType = 3;
			System.out.println(bgType); //bgimg4
		}
		
		if(x >= width * 2.5 && x <= width * 2.5 + 180 && y >= 665 && y <= 680){
			System.out.println("Saving img as png");
			saveImage(mandyMatteFinal);
		}
		repaint();
		}
	}
	
	public void saveImage (BufferedImage src) {
		try {
			ImageIO.write(src, "JPEG", new File("images/thisImage.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	       
	} 
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


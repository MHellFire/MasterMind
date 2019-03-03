// Copyright © 2004 Mariusz Helfajer
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE files for details.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
	
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class MasterMind extends Applet implements ActionListener, WindowFocusListener, ItemListener, Runnable
	{
	int i,j,l,x,y,tmp,nItems,nTry,count1,count2,currentPosition,currentRow;
	int[] dotMap,pointMap,secretCode;
	boolean showCode,showNumbers,gameStart,gameGoing;
	String str;
	Button help,about,start,check,show;
	Checkbox repeating,numbers,tries;
	Choice numberItems;
	Label label;
	Font FontComicBig,FontComicMedium,FontComicSmall;
	Thread thread;
	Image logo,image,bigDot;
	Graphics gDC,mDC,wDC;
	Color lights[],darks[],darkGray;
	WindowFrame window;
	WindowInfo info;

//------------------------------------------------------------------------------
	
	public void init()
		{
		setLayout(null);
		x=560; y=20; tmp=0; nItems=4; gameStart=false; gameGoing=false; showCode=true;
		int choiceItem[]={2,3,4,5,6,7,8,9,10};
		dotMap=new int[32];
		pointMap=new int[32];
		secretCode=new int[4];
		lights=new Color[10];
		darks=new Color[10];
		str=new String("Master Mind - Mistrz intelektu");
			
		FontComicBig=new Font("Comic Sans MS",Font.BOLD,28);
		FontComicMedium=new Font("Comic Sans MS",Font.BOLD,16);
		FontComicSmall=new Font("Comic Sans MS",Font.PLAIN,12);
		
		bigDot=getImage(getDocumentBase(),"Graph\\Dot.jpg");
		
		thread=new Thread(this);
		logo=createImage(560,35);
		image=createImage(260,375);
		wDC=image.getGraphics();
		mDC=logo.getGraphics();
		gDC=getGraphics();
						
		add(repeating=new Checkbox("Allow repeating items",false));
		add(numbers=new Checkbox("Show numbers",true));
		add(tries=new Checkbox("Limited tries (max=8)",true));
		repeating.setFont(FontComicSmall);
		numbers.setFont(FontComicSmall);
		tries.setFont(FontComicSmall);
		repeating.setBounds(4,90,140,20);
		numbers.setBounds(4,120,110,20);
		tries.setBounds(4,150,135,20);
		repeating.setBackground(Color.YELLOW);
		numbers.setBackground(Color.YELLOW);
		tries.setBackground(Color.YELLOW);
		repeating.addItemListener(this);
		numbers.addItemListener(this);
		tries.addItemListener(this);
		
		add(label=new Label("Number of items"));
		label.setBounds(3,180,95,20);
		label.setBackground(Color.YELLOW);
		
		add(numberItems=new Choice());
		numberItems.setBounds(101,180,40,20);
		numberItems.addItemListener(this);
		for(i=0;i<choiceItem.length;i++)
			numberItems.add(String.valueOf(choiceItem[i]));
		numberItems.select(2);
		
		add(help=new Button("Help"));
		add(about=new Button("About"));
		add(start=new Button("START"));
		add(check=new Button("CHECK"));
		add(show=new Button("SHOW"));
		help.setBounds(45,340,60,25);
		about.setBounds(45,380,60,25);
		start.setBounds(445,111,100,35);
		check.setBounds(445,155,100,212);
		show.setBounds(445,376,100,35);
		help.addActionListener(this);
		about.addActionListener(this);
		start.addActionListener(this);
		check.addActionListener(this);
		show.addActionListener(this);
		help.setFont(FontComicMedium);
		about.setFont(FontComicMedium);
		start.setFont(FontComicMedium);
		check.setFont(FontComicMedium);
		show.setFont(FontComicMedium);
		help.setBackground(Color.PINK);
		about.setBackground(Color.PINK);
		start.setBackground(Color.ORANGE);
		check.setBackground(Color.ORANGE);
		show.setBackground(Color.ORANGE);
		help.setEnabled(true);
		about.setEnabled(true);
		start.setEnabled(true);
		check.setEnabled(false);
		show.setEnabled(false);

		info=new WindowInfo();
		info.addWindowFocusListener(this);
		info.setTitle("Information");
		info.setBounds(getWidth()/2,getHeight()/2,300,140);
		info.addWindowListener(info);
		window=new WindowFrame();
		window.addWindowListener(window);
			
		darkGray=new Color(60,60,60);
		darks[0]=new Color(0,0,255);
		lights[0]=new Color(128,128,255);
		darks[1]=new Color(0,192,0);
		lights[1]=new Color(64,255,64);
		darks[2]=new Color(140,0,0);
		lights[2]=new Color(255,0,0);
		darks[3]=new Color(255,192,0);
		lights[3]=new Color(255,255,70);
		darks[4]=new Color(210,150,0);
		lights[4]=new Color(245,185,0);
		darks[5]=new Color(220,0,255);
		lights[5]=new Color(220,128,255);
		darks[6]=new Color(255,0,120);
		lights[6]=new Color(255,150,150);
		darks[7]=new Color(40,200,200);
		lights[7]=new Color(100,255,230);
		darks[8]=new Color(0,100,0);
		lights[8]=new Color(0,150,0);
		darks[9]=new Color(250,160,175);
		lights[9]=new Color(250,195,175);
		clearBoard();
		}

//------------------------------------------------------------------------------

	public void start()
		{
		if(thread.isAlive()) thread.resume();
			else thread.start();
		}

//------------------------------------------------------------------------------

	public void run()
		{
		while (true)
			{
			x-=tmp;
			if(x==-440)
				{
				x=560;
				tmp=1;
				}
			else tmp=1;
			try	
				{
				Thread.sleep(4);
				}
			catch(InterruptedException exc) {}
			mDC.setColor(Color.BLACK);
			mDC.clearRect(0,0,560,35);
			mDC.fillRect(0,0,560,35);
			mDC.setColor(Color.GREEN);
			mDC.setFont(FontComicBig);
			mDC.drawString(str,x,y);
			gDC.drawImage(logo,0,0,this);
			}
		}

//------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent event)
		{
		if(event.getSource()==start)
			{
			numberItems.setEnabled(false);
			label.setForeground(Color.lightGray);
			repeating.setEnabled(false);
			numbers.setEnabled(false);
			tries.setEnabled(false);
			start.setEnabled(false);
			check.setEnabled(false);
			showCode=false;										//for testing
			show.setEnabled(true);
			gameStart=true;
			gameGoing=true;
			generateCode();
			clearBoard();
			repaint();
			}
		if(event.getSource()==check)
			{
			boolean ok=false;
			if(!repeating.getState())
				{
				for(i=0;i<4;i++)
					for(j=i+1;j<4;j++)
						if(dotMap[count1-4+i]==dotMap[count1-4+j])
							ok=true;
				}
			if(!ok)
				{
				if(currentRow==7&&tries.getState())
					{
					numberItems.setEnabled(true);
					label.setForeground(Color.BLACK);
					repeating.setEnabled(true);
					tries.setEnabled(true);
					numbers.setEnabled(true);
					start.setEnabled(true);
					check.setEnabled(false);
					show.setEnabled(false);
					gameGoing=false;
					if(!checkDots())
						{
						info.str="You lose !!!";
						info.setVisible(true);
						}
					}
				else if(currentRow>=7)	
					{
					if(!checkDots())
						{
						for(i=0;i<28;i++)
							{
							dotMap[i]=dotMap[i+4];
							pointMap[i]=pointMap[i+4];
							}
						for(i=28;i<32;i++)
							{
							dotMap[i]=-1;
							pointMap[i]=-1;
							}
						check.setEnabled(false);
						currentPosition=0;
						count1-=4;
						count2-=4;
						currentRow=7;
						nTry++;
						repaint();
						}
					}
				else
					{
					checkDots();
					check.setEnabled(false);
					currentPosition=0;
					currentRow++;
					nTry++;
					repaint();
					}
				}
			}
		if(event.getSource()==show)
			{
			numberItems.setEnabled(true);
			label.setForeground(Color.BLACK);
			repeating.setEnabled(true);
			numbers.setEnabled(true);
			tries.setEnabled(true);
			start.setEnabled(true);
			show.setEnabled(false);
			check.setEnabled(false);
			gameGoing=false;
			showCode=true;
			info.str="Game stopped !!!";
			info.setVisible(true);
			repaint();
			}
		if(event.getSource()==help)
			{
			window.setSize(540,500);
			window.setTitle("Help");
			window.setChoice(0);
			window.show();
			}
		if(event.getSource()==about)
			{
			window.setSize(300,200);
			window.setTitle("About Master Mind");
			window.setChoice(1);
			window.show();
			}
		}

//------------------------------------------------------------------------------

	public void itemStateChanged(ItemEvent event)
		{
		if(event.getItemSelectable()==repeating);
			{
			if(!repeating.getState()&&nItems<4)
				repeating.setState(true);
			}
		if(event.getItemSelectable()==numbers)
			{
			showNumbers=numbers.getState();
			repaint();
			}
		if(event.getItemSelectable()==numberItems)
			{
			clearBoard();
			nItems=numberItems.getSelectedIndex()+2;
			if(!repeating.getState()&&nItems<4)
				{
				numberItems.select(2);
				nItems=4;
				}
			repaint();
			}
		}

//------------------------------------------------------------------------------

    public boolean mouseDown(Event event,int x,int y)
		{
		boolean ok=false;
		if(gameGoing)
			{
			if (currentPosition!=4)
				if(x>402&&x<421)
					for(i=0;i<nItems;i++)
						if(y>(119+i*29)&&y<(139+i*29))
							if(repeating.getState())
								{
								if(currentPosition<4)
									{
									dotMap[count1++]=i;
									currentPosition++;
									check.setEnabled(false);
									}
								if(currentPosition==4)
									{
									check.setEnabled(true);
									}
								repaint();
								}
							else
								{
								for(j=0;j<currentPosition;j++)
									if(dotMap[count1-currentPosition+j]==i)
										ok=true;
								if(!ok)
									{
									dotMap[count1++]=i;
									currentPosition++;
									check.setEnabled(false);
									}
								if(currentPosition==4)
									{
									check.setEnabled(true);
									}
								repaint();
								}
			if(currentPosition==4)
				for(i=0;i<8;i++)
					if(currentRow==i)
						for(j=0;j<4;j++)
							if(y<393-i*36&&y>374-i*36)
								if(x<212+j*30&&x>193+j*30)
									{
									if(dotMap[i*4+j]<nItems-1)
										dotMap[i*4+j]++;
									else
										dotMap[i*4+j]=0;
									repaint();
									}
			}
		return(true);
		}

//------------------------------------------------------------------------------

	public void windowGainedFocus(WindowEvent event) {}
	public void windowLostFocus(WindowEvent event)
		{
		if(!info.ok) info.requestFocus();
		}

//------------------------------------------------------------------------------

	public void clearBoard()
		{
		for(i=0;i<32;i++)
			{
			dotMap[i]=-1;
			pointMap[i]=-1;
			}
		count1=0;
		count2=0;
		currentPosition=0;
		currentRow=0;
		nTry=1;
		repaint();
	}

//------------------------------------------------------------------------------

	public void generateCode()
		{
		int tmp;
		if(repeating.getState())
			for(i=0;i<4;i++)
				secretCode[i]=(int)(Math.random()*nItems);
		else
			{
			secretCode[0]=(int)(Math.random()*nItems);
			do {tmp=(int)(Math.random()*nItems);}
				while(tmp==secretCode[0]);
			secretCode[1]=tmp;
			do {tmp=(int)(Math.random()*nItems);}
				while(tmp==secretCode[0]||tmp==secretCode[1]);
			secretCode[2]=tmp;
			do {tmp=(int)(Math.random()*nItems);}
				while(tmp==secretCode[0]||tmp==secretCode[1]||tmp==secretCode[2]);
			secretCode[3]=tmp;
			}
		}

//------------------------------------------------------------------------------

	public boolean checkDots()
		{
		int black=0,white=0,tab[]=new int[4];;
		boolean state=false;
		for(i=0;i<4;i++)
			tab[i]=dotMap[currentRow*4+i];
		for(i=0;i<4;i++)
			for(j=0;j<4;j++)
				if(tab[j]==secretCode[i])
					{
					pointMap[currentRow*4+white++]=1;
					tab[j]=-1;
					j=4;
					}
		for(i=0;i<4;i++)
			if(secretCode[i]==dotMap[currentRow*4+i])
				black++;
		for(i=0;i<black;i++)
			pointMap[currentRow*4+i]=0;
		if(black==4)
			{
			state=true;
			showCode=true;
			gameGoing=false;
			numberItems.setEnabled(true);
			label.setForeground(Color.BLACK);
			repeating.setEnabled(true);
			numbers.setEnabled(true);
			tries.setEnabled(true);
			start.setEnabled(true);
			check.setEnabled(false);
			show.setEnabled(false);
			info.str="You win !!!";
			info.setVisible(true);
			repaint();
			}
		return state;
		}

//------------------------------------------------------------------------------

	public void fixBox(Graphics g,int x,int y,int w,int h,Color c)
		{
		g.setColor(c);
		g.fillRect(x,y,w,h);
		}

//------------------------------------------------------------------------------

	public void fixDisc(Graphics g,int x,int y,int r,Color c)
		{
		g.setColor(c);
		g.fillOval(x,y,r,r);
		}

//------------------------------------------------------------------------------

	public void fixCircle(Graphics g,int x,int y,int r,Color c)
		{
		g.setColor(c);
		g.drawOval(x,y,r,r);
		}

//------------------------------------------------------------------------------

	public void boardDot(Graphics g,int x,int y,int c)
		{
		fixDisc(g,x+2,y+2,18,darkGray);
		fixDisc(g,x,y,18,darks[c]);
		fixDisc(g,x+1,y+1,14,lights[c]);
		fixDisc(g,x+3,y+3,6,Color.WHITE);
		g.setFont(FontComicMedium);
		g.setColor(Color.BLACK);
		if(numbers.getState())
			g.drawString(Integer.toString(c),x+5,y+15);
		}

//------------------------------------------------------------------------------

	public void boardHole(Graphics g,int x,int y)
		{
		fixDisc(g,x+5,y+5,7,Color.BLACK);
		g.setColor(Color.lightGray);
		g.drawArc(x+4,y+4,9,9,-135,180);
		}

//------------------------------------------------------------------------------

	public void pointDot(Graphics g,int x,int y,int c)
		{
		fixDisc(g,x+2,y+2,10,Color.BLACK);
		if(c==0)
			{
			fixDisc(g,x,y,10,darkGray);
			g.setColor(Color.WHITE);
			g.drawArc(x+2,y+2,7,7,70,130);
			}
		if(c==1)
			{
			fixDisc(g,x,y,10,Color.WHITE);
			fixDisc(g,x+3,y+3,4,Color.lightGray);
			fixDisc(g,x+5,y+5,4,Color.WHITE);
			}
		}

//------------------------------------------------------------------------------

	public void pointHole(Graphics g,int x,int y)
		{
		fixDisc(g,x+3,y+3,4,Color.BLACK);
		g.setColor(Color.lightGray);
		g.drawArc(x+2,y+2,6,6,-135,180);
		}

//------------------------------------------------------------------------------

	public void paint(Graphics g)
		{
		setBackground(Color.YELLOW);
		g.setColor(Color.BLACK);
		for(i=150;i<153;i++)
			g.drawLine(i,42,i,415);
		g.setFont(FontComicMedium);
		g.drawString("Options",42,65);
		if(gameGoing)
			{
			g.drawString("Try",475,65);
			g.setFont(FontComicBig);
			if(nTry<10)	g.drawString(""+(nTry),482,100);
				else g.drawString(""+(nTry),474,100);
			}
		wDC.setColor(Color.YELLOW);
		wDC.fillRect(0,0,260,375);
		wDC.setColor(Color.GRAY);
		for(i=0;i<4;i++)
			wDC.fill3DRect(i,i+5,200-2*i,370-2*i,true);
		for(i=0;i<8;i++)
			{
			wDC.draw3DRect(12,77+i*36,136,32,false);
			wDC.draw3DRect(152,77+i*36,36,32,false);
			}
		//secret field
		if(!showCode)
			{
			wDC.fill3DRect(12,22,136,40,true);
			wDC.fill3DRect(13,23,134,38,true);
			wDC.setFont(FontComicMedium);
			wDC.setColor(Color.GREEN);
			wDC.drawString("SECRET CODE",24,45);
			}
		else
			{
			wDC.draw3DRect(12,24,136,32,false);
			if(gameStart==false)
				for(i=0;i<4;i++)
					boardHole(wDC,24+i*30,30);
			else
				for(i=0;i<4;i++)
					boardDot(wDC,24+i*30,30,secretCode[i]);
			}
		for(i=0;i<8;i++)
			{
			//draw colored pins or just holes
			for(j=0;j<4;j++)
				if(dotMap[i*4+j]<0)
					boardHole(wDC,24+j*30,335-i*36);
				else
					boardDot(wDC,24+j*30,335-i*36,dotMap[i*4+j]);
			//draw the score pins or holes
			for(j=0;j<2;j++)
				for(l=0;l<2;l++)
					if(pointMap[i*4+j*2+l]<0)
						pointHole(wDC,157+l*16,332-i*36+j*15);
					else
						pointDot(wDC,157+l*16,332-i*36+j*15,pointMap[i*4+j*2+l]);
			}
		//right field
		wDC.setColor(Color.GRAY);
		wDC.fill3DRect(225,71,35,300,true);
		wDC.fill3DRect(226,72,33,298,true);
		for(i=0;i<10;i++)
			boardHole(wDC,233,80+i*29);
		for(i=0;i<nItems;i++)
			boardDot(wDC,233,80+i*29,i);
		g.drawImage(bigDot,50,240,this);
		g.drawImage(image,170,40,this);
		}
	}

//------------------------------------------------------------------------------

class WindowInfo extends Dialog implements ActionListener, WindowListener
	{
	String str="";
	boolean ok=false;
	Button okButton;
	Font FontComic=new Font("Comic Sans MS",Font.BOLD,24);
	WindowInfo()
		{ 
		super(new Frame());
		setLayout(null);
		add(okButton=new Button("OK"));
		okButton.setBounds(125,80,50,25);
		okButton.addActionListener(this);
		}
	public void paint(Graphics g)
		{
		g.setFont(FontComic);
		if(str.equals("You win !!!")) g.drawString(str,90,60);
		if(str.equals("You lose !!!")) g.drawString(str,88,60);
		if(str.equals("Game stopped !!!")) g.drawString(str,60,60);
		}
	public void actionPerformed(ActionEvent event)
		{
		if(event.getSource()==okButton) 
			{
			ok=true;
			setVisible(false);
			}
		}
	public void windowClosing(WindowEvent event)
		{
		hide();
		}
	public void windowClosed(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowActivated(WindowEvent event) {}
	public void windowDeactivated(WindowEvent event) {}
	}

//------------------------------------------------------------------------------

class WindowFrame extends Frame implements WindowListener
	{
	WindowFrame()
		{ super(); }
	int type;
	public void setChoice(int choice)
		{
		type=choice;
		}
	public void paint(Graphics g)
		{
		Font FontComicBig=new Font("Comic Sans MS",Font.BOLD,22);
		Font FontComicMedium=new Font("Comic Sans MS",Font.PLAIN,14);
		Font FontComicSmall=new Font("Comic Sans MS",Font.PLAIN,12);
		setBackground(Color.BLACK);
		g.setColor(Color.WHITE);
		if(type==0)	//help
			{
			g.setFont(FontComicBig);
			g.drawString("ZASADY GRY",190,70);
			g.setFont(FontComicMedium);
			g.drawString("Nalezy odgadnac kombinacje ukryta pod napisem SECRET CODE. W tym celu",10,120);
			g.drawString("nalezy wybrac odpowiednie opcje gry (options) oraz kliknac na przycisk START.",10,140);
			g.drawString("Allow repeating items - pozwala na powtarzanie w kodzie kolorowych pionkow.",10,177);
			g.drawString("Show numbers - pokazuje lub ukrywa cyfry na kolorowych pionkach.",10,200);
			g.drawString("Limited tries (max=8) - ograniczona liczba prob do 8.",10,223);
			g.drawString("Number of items - na ilu kolorowych pionkach chcemy grac.",10,246);
			g.drawString("Po kliknieciu na START nalezy wybrac z prawego pola kolejne pionki. Gdy",10,280);
			g.drawString("wypelnimy caly rzad (4 kolory) mozemy jeszcze zmienic ich kolejnosc, klikajac",10,300);
			g.drawString("bezposrednio na kazdy z tych kolorowych pionkow. Nastepnie klikamy na CHECK,",10,320);
			g.drawString("aby uzyskac informacje na temat poprawnosci naszego kodu.",10,340);
			g.drawString("Jesli w ulozonej przez odgadujacego kombinacji znajduje sie pionek tego samego",10,380);
			g.drawString("koloru co w kombinacji ukrytej, lecz pionek ten nie stoi w tej samej kolumnie",10,400);
			g.drawString("to otrzymujemy szpilke biala, jesli natomiast znajduje sie w tej samej kolumnie",10,420);
			g.drawString("to otrzymujemy szpilke czarna.",10,440);
			}
		if(type==1)	//about
			{
			g.setFont(FontComicMedium);
			g.drawString("MASTER MIND Version 1.1",60,60);
			g.setFont(FontComicSmall);
			g.drawString("Written by Mariusz Helfajer",66,100);
			g.drawString("Copyright (C) 2004    mhelfajer@poczta.onet.pl",20,150);
			}
		}
	public void windowClosing(WindowEvent event)
		{
		hide();
		}
	public void windowClosed(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowActivated(WindowEvent event) {}
	public void windowDeactivated(WindowEvent event) {}
	}

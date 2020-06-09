import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Hw5 extends JFrame{
	
	public int score=0;
	public int maxscore=0;
	
	int W=700;
	int H=600;
	
	int stage=1;
	
	int barX=0;				//바 x위치
	int barY=(H/7)*6;		//바 y위치
	final int barW=120;		//바 가로
	final int barH=20;		//바 높이
	
	int bindex=0;		//공 인덱스
	int ballX;
	int ballY=(H/9)*4;
	final int ballW=14;
	final int ballH=14;
	
	int blockrow;
	int blockcol;
	
	public Blocks[][] block;
	
	int blockspace=3;
	int bw;
	int bh;
	
	boolean GameOver = false;
	
	public static void main(String[] args) {
		new Hw5();
	}
	
	Hw5()
	{
		setTitle("Hw5");
		setSize(W,H);
		setResizable(false);						// 화면 크기 고정
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);	// 창을 닫으면 모든 스레드 종료
		
		add(new Title());
		
		setVisible(true);
	}
	
	class PlayScreen extends JPanel implements KeyListener, Runnable
	{		
		int count;
		int ballcount;
		
		boolean background=false;
		boolean eventcheck=false;
		
		LinkedList<Balls> ballList;
		
		Clip PlayBGM1;
		Clip PlayBGM2;
		Clip PlayBGM3;
		Clip bounce;
		Clip Broken;
		Clip Event;
		Clip GameOverSound;
		Clip Clear;
		
		PlayScreen()
		{
			setPreferredSize(new Dimension(W,H));
			setFocusable(true);
			requestFocus();
			addKeyListener(this);
			
			stage = 1;
			
			setsound();
			init();
			
			Thread t = new Thread(this);				// run이라는 함수를 가지고 있는 것. (implements Runnable 필수)
			t.start();
		}

		void setsound()
		{
			try {
				PlayBGM1 = AudioSystem.getClip();
				URL url3 = getClass().getResource("PlayBGM1.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url3);
				PlayBGM1.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				PlayBGM2 = AudioSystem.getClip();
				URL url4 = getClass().getResource("PlayBGM2.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url4);
				PlayBGM2.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				PlayBGM3 = AudioSystem.getClip();
				URL url5 = getClass().getResource("PlayBGM3.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url5);
				PlayBGM3.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				bounce = AudioSystem.getClip();
				URL url6 = getClass().getResource("bounce.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url6);
				bounce.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Broken = AudioSystem.getClip();
				URL url7 = getClass().getResource("Broken.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url7);
				Broken.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Event = AudioSystem.getClip();
				URL url8 = getClass().getResource("Event.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url8);
				Event.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				GameOverSound = AudioSystem.getClip();
				URL url9 = getClass().getResource("GameOverSound.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url9);
				GameOverSound.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Clear = AudioSystem.getClip();
				URL url12 = getClass().getResource("GameStart.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url12);
				Clear.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		void soundStop()
		{
			if(stage%3==1)
				PlayBGM2.stop();
			else if(stage%3==2)
				PlayBGM1.stop();
			else if(stage%3==0)
				PlayBGM3.stop();
		}
		
		void init()
		{
			if(stage%3==1)
			{
			PlayBGM2.setFramePosition(0);
			PlayBGM2.loop(Clip.LOOP_CONTINUOUSLY);
			}
			else if(stage%3==2)
			{
			PlayBGM1.setFramePosition(0);
			PlayBGM1.loop(Clip.LOOP_CONTINUOUSLY);
			}
			else if(stage%3==0)
			{
			PlayBGM3.setFramePosition(0);
			PlayBGM3.loop(Clip.LOOP_CONTINUOUSLY);
			}
			
			ballList = new LinkedList<Balls>();
			
			barX=W/2-(barW/2);															//바 x위치
			ballList.add(new Balls(W/2-(ballW/2),(H/9)*7,ballW,ballH,4,stage+5,bindex));
			
			blockrow=2+stage*2;
			blockcol=stage+2;
			
			bw=(W-blockspace*(blockrow-1))/blockrow;
			bh=(H/3)/blockcol;
			
			block = new Blocks[blockrow][blockcol];
			ballcount=1;
			count = 0;
			
			GameOver = false;
			
			makeblock();
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_LEFT)
			{
				if(barX-15>=1)
				{
					barX-=20+(stage*7);					
				}
				else
				{
					barX=1;
				}
			}
			
			if(e.getKeyCode()==KeyEvent.VK_RIGHT)
			{
				if(barX+40<=W-barW)
				{
				barX+=20+(stage*7);
				}
				else
				{
					barX=this.getWidth()-barW;
				}
			}		
			
			if(e.getKeyCode()==KeyEvent.VK_SPACE)
			{
				//blockevnet 테스트
				
				//blockevent(W/2,H/3);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		
		}
		
		void isGameOver()
		{
			if(ballcount<=0)
			{
				soundStop();
				GameOverSound.setFramePosition(0);
				GameOverSound.start();
				GameOver=true;
				stage=-1;
				repaint();
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				ballList.clear();
				
				if(maxscore<score)
					maxscore=score;
				

				return;
			}
			
			if(count>=blockcol*blockrow)
			{
				soundStop();
				stage++;
				score+=1000;
				
				GameOver=true; 
				ballList.clear();
				Clear.setFramePosition(0);
				Clear.start();
				repaint();
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				init();
				return;
			}
		}
		
		public void run() {
			
			while(!GameOver) {
				resolveCollision();
				isGameOver();
				update();
				repaint();
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			
			soundStop();
			ScreenChange(2);
			this.setVisible(false);
		}
		
		public void resolveCollision() {
			
			int event[] = new int[100];
			int eventindex=0;
			
			for(Balls b:ballList)
			{
				if(b.bx+(b.r*2)>=barX-15 && b.bx<=barX+barW+15 && b.by+(b.r*2)>=barY-3 && b.by+ballH<barY+barH+3) 
				{
					b.dx=((b.bx+((b.r*2)/2))-(barX+(barW/2)))/10;
					
					if(b.by+(b.r*2)<barY+(barH/2))
					b.dy=-b.dy;
					
					bounce.setFramePosition(0);
					bounce.start();
				}
			
				if(b.bx+10>W-(b.r*2))	{ b.bx = W-(b.r*2)-10; b.dx = -b.dx;}
				if(b.bx<=0)			{ b.bx = 0; b.dx = -b.dx;}
				if(b.by>=H)			{if(b.delete==false){ballcount--; b.delete=true;}}						//삭제필요, 삭제못해서 ==H 일때만 검사
				if(b.by<35)			{ b.by =30;  b.dy = -b.dy;}
				
				b.blockcollision=false;
			}
			
			for(int i=0;i<blockrow;i++)
			{
				for(int j=0;j<blockcol;j++)
				{
					
					int bcx=block[i][j].blockx;
					int bcy=block[i][j].blocky;
					int bcw=block[i][j].blockw;
					int bch=block[i][j].blockh;
					
					for(Balls b:ballList)
					{
						int x1 = bcx - b.r;
						int y1 = bcy - b.r;
						int x2 = bcx + bcw + b.r;
						int y2 = bcy + bch + b.r;
						
						if(b.bx >= x1-2 && b.bx <= x2+2 && b.by >= y1-2 && b.by <= y2+2) 
						{				
							
							if(block[i][j].blocks>=8)				//이벤트 블록 충돌 검사
							{
								int ex=block[i][j].blockx+((block[i][j].blockw)/2);
								int ey=block[i][j].blocky+((block[i][j].blockh)/2);
								
								event[eventindex]=ex;
								event[eventindex+1]=ey;
								
								eventindex= eventindex+2;
								
								eventcheck=true;
							}
							
							else
							{
								Broken.setFramePosition(0);
								Broken.start();
							}
							
							block[i][j].blockx=0;
							block[i][j].blocky=0;
							block[i][j].blockw=0;
							block[i][j].blockh=0;
							
							if(b.blockcollision==false)
							{
								if(b.prex <= x1-2) {b.bx = b.prex; b.dx = -Math.abs(b.dx);}
								if(b.prex >= x2+2) {b.bx = b.prex; b.dx = Math.abs(b.dx);}
								if(b.prey <= y1-2) {b.by = b.prey; b.dy = -Math.abs(b.dy);}
								if(b.prey >= y2+2) {b.by = b.prey; b.dy = Math.abs(b.dy);}

								b.blockcollision=true;
							}
							
							if(block[i][j].broken==false)
							{
								count++;
								score+=30;
								block[i][j].broken=true;
							}
							
						}
					}
				}
			}
			
			if(eventcheck==true && GameOver==false)
			{
				Event.setFramePosition(0);
				Event.start();
				
				for(int i=0;i<ballcount*2;i+=2)
				{
					if(event[i]!=0)
					{
						blockevent(event[i],event[i+1]);
					}
					
					else
					{
						break;
					}
				}
				
				eventcheck=false;
			}
		}
		
		public void update()
		{
			for(Balls b:ballList)
			{	
				b.prex = b.bx;
				b.prey = b.by;
				
				b.bx+= b.dx;
				b.by+=b.dy;
			}
		}
		
		void makeblock()
		{		
			for(int i=0;i<blockrow;i++)
			{
				for(int j=0;j<blockcol;j++)
				{
					block[i][j]=new Blocks();
					block[i][j].setblock((i*bw)+blockspace*i,(bh*j)+(blockspace*j)+30,bw,bh);
				}
			}
		}
		
		void blockevent(int x,int y)
		{		
			ballList.add(new Balls(x,y,ballW,ballH,-2,stage+5,bindex+1));
			ballList.add(new Balls(x,y,ballW,ballH,0,stage+5,bindex+2));
			ballList.add(new Balls(x,y,ballW,ballH,2,stage+5,bindex+3));
			
			ballcount+=3;
			bindex+=3;
		}

		public void paintComponent(Graphics g) {
			
				super.paintComponent(g);
				
				if(stage%3==1)
				{
					for(int i=0;i<this.getHeight();i+=15)
					{
						g.setColor(new Color((105*i)/this.getHeight()+150,(120*i)/this.getHeight()+100,0));
						g.fillRect(0, i, this.getWidth(),15);
					}
				}
				else if(stage%3==2)
				{
					for(int i=0;i<this.getHeight();i+=15)
					{
						g.setColor(new Color((55*i)/this.getHeight()+200,50,(100*i)/this.getHeight()+150));
						g.fillRect(0, i, this.getWidth(),15);
					}
				}
				else if(stage%3==0)
				{
					for(int i=0;i<this.getHeight();i+=15)
					{
						g.setColor(new Color(0,(120*i)/this.getHeight()+100,(105*i)/this.getHeight()+150));
						g.fillRect(0, i, this.getWidth(),15);
					}
				}
				
				//bar
				for(int i=0;i<10;i++)
				{
					g.setColor(Color.BLACK);
					g.fillRect(barX+3,barY,barW-6,3);
					g.setColor(Color.WHITE);
					g.fillRect(barX+3, barY+3, barW-6, 3);
					g.drawRect(barX+5, barY+8, barW-10, barH-10);
					g.setColor(new Color(230,50,50));
					g.fillRect(barX+3, barY+8, barW-6, barH-10);
				}
				for(Balls b: ballList)
				{
					if(b.delete==false)
					b.draw(g);	
				}
				
				for(int i=0;i<blockrow;i++)
				{
					for(int j=0;j<blockcol;j++)
					{
						g.setColor(Color.WHITE);
						g.drawRect(block[i][j].blockx,block[i][j].blocky,block[i][j].blockw,block[i][j].blockh);
					
						if(block[i][j].blocks<=7)							//이벤트 블럭
						{
							g.setColor(new Color(j*(255/blockcol/7*3),j*(255/blockcol/7*3),j*(255/blockcol/7*3)));
						}
						else
							g.setColor(new Color(200,200,0));
						
						block[i][j].draw(g);
					}
				}
				
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, W, 30);
				g.setColor(Color.WHITE);
				g.fillRect(5, 4, W-25, 22);
				
				g.setColor(Color.BLACK);
				g.setFont(new Font("Cooper Black", 0, 18));
				if(GameOver==false)
				{
					g.setColor(Color.BLACK);
					g.setFont(new Font("Cooper Black", 0, 18));
					g.drawString("Stage",10,20);
					g.drawString(Integer.toString(stage),70,20);
				}
				else
				{
					if(stage<0)
					{
						g.drawString("Game Over!",10,20);
					}
					else
					{
						g.drawString("Stage",10,20);
						g.drawString(Integer.toString(stage),70,20);
						
						g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 100));
						g.setColor(Color.BLACK);
						g.drawString("Stage",110,H/2);
						g.drawString(Integer.toString(stage),510,H/2);
					}
					
				}
				
				g.setColor(Color.BLACK);
				g.setFont(new Font("Cooper Black", 0, 18));
				
				g.drawString("Score:",W-150,20);
			    g.drawString(Integer.toString(score),W-85,20);
			    
				
		}
	}

	class GameOverScreen extends JPanel implements KeyListener
	{	
		Clip GameOverBGM;
		Clip Regame;
		int time=0;
		
		GameOverScreen()
		{
			setFont(new Font("Arial", Font.BOLD,50));
			
			
			setFocusable(true);
			requestFocus();
			addKeyListener(this);

			setsound();
			
			setVisible(true);
		}
		
		void setsound()
		{
			try {
				Regame = AudioSystem.getClip();
				URL url10 = getClass().getResource("Regame.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url10);
				Regame.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				GameOverBGM = AudioSystem.getClip();
				URL url11 = getClass().getResource("GameOverBGM.wav");	
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url11);
				GameOverBGM.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			GameOverBGM.setFramePosition(0);
			GameOverBGM.start();
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
		

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_SPACE)
			{
				GameOverBGM.stop();
				Regame.setFramePosition(0);
				Regame.start();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Regame.stop();
				ScreenChange(0);
				
				score=0;
				this.setVisible(false);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
		
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			
			for(int i=0;i<this.getHeight();i+=15)
			{
				g.setColor(new Color((100*i)/this.getHeight()+50,(100*i)/this.getHeight()+50,(100*i)/this.getHeight()+50));
				g.fillRect(0, i, this.getWidth(),15);
			}

	        g.setFont(new Font("Cooper Black", Font.BOLD, 88));
	        g.setColor(Color.RED);
	        g.drawString("GAME OVER",40,175);
	        g.setColor(Color.WHITE);
	        g.drawString("GAME OVER",45,170);

	        g.setFont(new Font("Cooper Black", Font.BOLD, 30));
	        g.drawString("Score          :",170,280);
	        g.drawString(Integer.toString(score),450,280);
	        
	        g.setFont(new Font("Cooper Black", Font.BOLD, 30));
	        g.drawString("MaxScore  :",170,330);
	        g.drawString(Integer.toString(maxscore),450,330);

	        
	        if(time<=20)
	         {
	         g.setColor(Color.BLACK);
	         g.setFont(new Font("Stencil", Font.BOLD, 25));
	         g.drawString("PRESS SPACEBAR",230,460);
	         }
	         
	         time++;
	         
	         if(time==40)
	        	 time=0;
	         
	         try {
				Thread.sleep(33);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	         repaint();
		}

	}
	
	class Title extends JPanel implements KeyListener
	{
		Clip TitleBGM;
		Clip GameStart;
		
		int time;
		
		Title()
		{
			setFocusable(true);
			requestFocus();
			addKeyListener(this);
			
			setsound();
			
			setVisible(true);
		}

		void setsound()
		{
			try {
				GameStart = AudioSystem.getClip();
				URL url = getClass().getResource("GameStart.wav");	
				
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url);
				GameStart.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				TitleBGM = AudioSystem.getClip();
				URL url2 = getClass().getResource("TitleBGM.wav");	
				AudioInputStream stream = 
						AudioSystem.getAudioInputStream(url2);
				TitleBGM.open(stream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			TitleBGM.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_SPACE)
			{
				TitleBGM.stop();
				GameStart.start();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				ScreenChange(1);
				GameStart.stop();
				this.setVisible(false);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
		
		public void paintComponent(Graphics g) {	
			super.paintComponent(g);

			for(int i=0;i<this.getHeight();i+=15)
			{
				g.setColor(new Color((155*i)/this.getHeight()+100,(100*i)/this.getHeight()+155,0));
				g.fillRect(0, i, this.getWidth(),15);
			}
			
			 g.setColor(Color.WHITE);
			 g.fillRect(0, 75, this.getWidth(),125);
			 
			 g.setColor(new Color(150,150,150));
			 g.fillRect(0, 200, this.getWidth(),125);
			
			 g.setColor(Color.BLACK);
	         g.setFont(new Font("Stencil", Font.ITALIC, 130));
	         g.drawString("Block",47,181);
	         g.setFont(new Font("Stencil", Font.ITALIC, 105));
	         g.drawString("BREAKER",162,296);
	         
	         g.setColor(new Color(230,50,50));
	         g.setFont(new Font("Stencil", Font.ITALIC, 130));
	         g.drawString("Block",50,185);
	         g.setFont(new Font("Stencil", Font.ITALIC, 105));
	         g.drawString("BREAKER",165,300);
	         
	         if(time<=10)
	         {
	         g.setColor(Color.BLACK);
	         g.setFont(new Font("Stencil", Font.BOLD, 30));
	         g.drawString("PRESS SPACEBAR TO PLAY!",140,450);
	         }
	         
	         time++;
	         
	         if(time==20)
	        	 time=0;
	         
	         try {
				Thread.sleep(33);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	         repaint();

		}
	}

	public abstract class object
	{	
		public abstract void draw(Graphics g);
	}
	
	class Balls extends object
	{
		int index;
		
		int bx;
		int by;
		int bw;
		int bh;
		int r;
		
		int prex;
		int prey;
		
		int dx;
		int dy;

		boolean blockcollision=false;
		boolean delete=false;
		
		Balls(int x, int y, int w,int h, int delx,int dely,int i)
		{
			bx=x;
			by=y;
			bw=w;
			bh=h;
			r=w/2;
			
			dx=delx;
			dy=dely;
			
			index=i;
		}
		
		@Override
		public void draw(Graphics g)
		{
			g.setColor(Color.BLACK);
			g.fillOval(bx,by,r*2,r*2);
			g.setColor(Color.WHITE );
			g.fillOval(bx+(bw/2),by+3,(r*2)/3,(r*2)/3);
		}
	}
	
	class Blocks extends object
	{
		int blockx;
		int blocky;
		int blockw;
		int blockh;
		int blocks;
		
		boolean broken;
		
		public Blocks()
		{
			blockx=0;
			blocky=0;
			blockw=0;
			blockh=0;
			blocks=(int)(Math.random()*10+1);
			
			broken=false;
		}
		
		void setblock(int x,int y,int w,int h)
		{
			blockx=x;
			blocky=y;
			blockw=w;
			blockh=h;
		}
		
		@Override
		public void draw(Graphics g)
		{
			if(broken==false)
			{
				g.drawRect(blockx-1,blocky-1,blockw+2,blockh+2);
				g.fillRect(blockx,blocky,blockw,blockh);
			}
		}
	}

	void ScreenChange(int screen) 
	{
			if(screen==0)
			{
				add(new Title());
			}
				
			else if(screen==1)
			{
				add(new PlayScreen());
			}
			else if(screen==2)
			{
				add(new GameOverScreen());
			}		
			
			setVisible(true);
	}
}


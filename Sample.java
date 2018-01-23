import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.event.*;
import java.awt.Color.*;
import java.util.Random;
import java.applet.*;
import java.applet.AudioClip;


public class Sample extends JFrame implements ActionListener
{
	public static void main(String[] args)
	{
		Sample s = new Sample();
		s.setVisible(true);
	}

	private Image img,img2,img3,img4;
	private JPanel p = new JPanel();
	private JButton btn[][] = new JButton[12][12];		//マス目
	private JLabel count = new JLabel();				//ƒゲームの状況を表示
	private JButton reset = new JButton();				//中央上のアイコン
	private String c = "Fight!!";
	private Timer timer;
	private JLabel time = new JLabel();					//タイムの表示
	private AudioClip ac,ac2;
	int sec=0;
	int min=0;


	private Boolean[][] open = new Boolean[12][12];			//マスの開閉を表す
	private Boolean[][] bom_here = new Boolean[12][12];		//爆弾の有無を表す
	private int[][] bom_number = new int[12][12];			//周りの爆弾の数を表す


	Sample()
	{

		try{
			img = ImageIO.read(new File("001.png"));
			img2 = ImageIO.read(new File("07.png"));
			img3 = ImageIO.read(new File("06.png"));
			img4 = ImageIO.read(new File("08.png"));
		}
		catch(Exception ex)
		{
		}
		setTitle("マインスイーパー");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(516,645);
		setBounds(100,100,516,645);

		p.setLayout(null);

		count.setText(c);								//ゲームの状況を表示
		count.setHorizontalAlignment(JLabel.CENTER);
		count.setSize(200,100);
		count.setFont(new Font("Arial",Font.BOLD,30));
		count.setForeground(new Color(0,255,127));
		count.setBackground(new Color(0,0,0));
		count.setOpaque(true);
		count.setBounds(0,0,200,100);
		p.add(count);

		reset.setIcon(new ImageIcon(img));				//中央上のアイコン
		reset.setHorizontalAlignment(JLabel.CENTER);
		reset.setVerticalAlignment(JLabel.CENTER);
		reset.setBackground(new Color(211,211,211));
		reset.setOpaque(true);
		reset.setBounds(200,0,100,100);
		p.add(reset);

		timer = new Timer(1000,this);					//1秒ごとにカウントされるタイマー

		time.setSize(200,100);							//タイマー表示
		time.setFont(new Font("MS明朝",Font.BOLD,25));
		time.setForeground(new Color(0,255,127));
		time.setBackground(new Color(0,0,0));
		time.setHorizontalAlignment(JLabel.CENTER);
		time.setVerticalAlignment(JLabel.CENTER);
		time.setOpaque(true);
		time.setBounds(300,0,200,100);
		p.add(time);
		ac = Applet.newAudioClip( getClass( ).getResource( "bom_sound.wav" ) );
		ac2 = Applet.newAudioClip( getClass( ).getResource( "game_clear.wav" ) );

		timer.start();

		DrawButton();			//パネルを表示させる関数
		Bomset();				//爆弾を生成し設置する関数	

		getContentPane().add(p,BorderLayout.CENTER);

	}

	private int x=0;
	private int y=105;

	void DrawButton()
	{
		p.setLayout(null);

		for(int i=0;i<10+2;i++)			//+2をしているのは見えない外側のパネルを余分に創ることで再帰関数を回してもポインタが指せるようにするため
		{
			for(int j=0;j<10+2;j++)
			{
				
				open[i][j]=false;			//パネルを閉じる
				bom_here[i][j]=false;		//爆弾を設置しない
				bom_number[i][j]=0;			//周りの爆弾の数を0にする
				if(i>0 && i<11)				//再帰関数用の余分を表示させない
				{
					if(j>0 && j<11)
					{
						btn[i][j]= new JButton();
						btn[i][j].setBackground(new Color(65,105,255));	
						btn[i][j].setBounds(x,y,50,50);
						//btn[i][j].setText(" "+bom_number[i][j]);
						btn[i][j].addActionListener( new SampleActionListener( ) );
						p.add(btn[i][j]);
						x+=50;			//幅を変えて設置
					}
				}
				
			}
			x=0;
			if(i>0 && i<11){
				y+=50;					//幅を変えて設置
			}
		}
	}


	void Bomset()
	{						//爆弾を生成、設置する関数
		int ax=0;
		int ay=0;
		for(int i=0;i<10;i++)			//10個生成
		{
			do{
				long seed = Runtime.getRuntime().freeMemory(); //空きメモリを利用した乱数
				Random r = new Random(seed);
				ax = r.nextInt(10)+1;
				long seed2 = Runtime.getRuntime().freeMemory(); 
				Random r2 = new Random(seed2);
				ay = r2.nextInt(10)+1;
			}while(bom_here[ax][ay]);				//爆弾がすでにあるなら設置されていないところを探す

			bom_here[ax][ay]=true;

			bom_number[ax-1][ay+1]+=1;		//周り8マスの爆弾の数を増やす
			bom_number[ax][ay+1]+=1;
			bom_number[ax+1][ay+1]+=1;
			bom_number[ax-1][ay]+=1;
			bom_number[ax+1][ay]+=1;
			bom_number[ax-1][ay-1]+=1;
			bom_number[ax][ay-1]+=1;
			bom_number[ax+1][ay-1]+=1;
			
		}
		/*for(int i=1;i<11;i++){			//デバック用
			for(int j=1;j<11;j++){
				btn[i][j].setText("a"+bom_number[i][j]);
			}
		}*/
	}

	public void actionPerformed(ActionEvent e)
	{					
		if(min>=1){												//分表示まで
			time.setText("経過時間 : "+min+"分"+sec+"秒");
			sec++;
			if(sec>=60){
				min++;
				sec=0;
			}
		}else{
			time.setText("経過時間 : "+sec + "秒");
   			sec++;
   			if(sec>=60){
   				min++;
   				sec=0;
   			}
		}
   		
   	}

   	class SampleActionListener implements ActionListener
   	{
   		public void actionPerformed( ActionEvent e )			
   		{
   			int this_i=0;					//ボタン情報を格納
   			int this_j=0;

   			JButton b;
   			b = (JButton)e.getSource();		//押されたボタンをbに代入

   			for(int i=0;i<10+2;i++)			
   			{
   				for(int j=0;j<10+2;j++)
   				{
   					if(b==btn[i][j])		//押されたボタンの場所を探すためのループ
   					{
   						this_i=i;			//iとjがわかったら格納し、ボタンの情報にアクセス可能にする
   						this_j=j;
   					}
   				}
   			}

   			//btn[this_i][this_j].setText("daide");		//デバック用
   			Open(this_i,this_j);  			//ボタンの場所を引数にとり、マスを開ける再帰関数

   		}
   	}

   	void Open(int i,int j)
   	{
   		if(i<1 || 10<i || j<1 || 10<j)		//余分なマスを省く
   		{
   			return ;
   		}
   		if(open[i][j])						//再帰関数の停止条件
   		{
   			return;
   		}

   		open[i][j]=true;				
   		if(bom_number[i][j]==0){			//再帰で回ってきたとき、周りに爆弾がなければ*
   			btn[i][j].setText(" *");
   		}else{
   			btn[i][j].setText(" "+bom_number[i][j]);	//周りに爆弾があれば数を表示
   		}
   		
   		if(Check())				//ゲームクリアチェックする関数
   		{
   			p.setBackground(new Color(135,206,250));
   			count.setText("Game Clear!");			//ゲーム状況を更新
   			count.setForeground(new Color(255,255,0));
			count.setBackground(new Color(0,0,0));
			count.setOpaque(true);
			timer.stop();
			ac2.play();				//音を鳴らす
			for(int x=1;x<11;x++)
			{
				for(int y=1;y<11;y++)
				{
					btn[x][y].setEnabled(false);		//全てのマスのボタンを無効にし、ゲームクリアの状態を維持
				}
			}
   		}

   		if(bom_here[i][j])			//爆弾を当てた時
   		{
   			btn[i][j].setIcon(new ImageIcon(img2));
   			btn[i][j].setHorizontalAlignment(JButton.LEFT);
			btn[i][j].setVerticalAlignment(JButton.CENTER);
			timer.stop();
			ac.play( );
			count.setText("Game Over!");		//ゲーム状況を更新
			p.setBackground(new Color(255,0,0));
			reset.setIcon(new ImageIcon(img4));
			for(int x=1;x<11;x++)
			{
				for(int y=1;y<11;y++)
				{
					btn[x][y].setEnabled(false);		//全てのマスのボタンを無効にし、ゲームオーバーの状態を維持
				}
			}
			getContentPane().add(p,BorderLayout.CENTER);

   		}

   		if(bom_here[i][j]!=true && bom_number[i][j]==0){		//再帰条件、爆弾がなく、周りにも爆弾がなければゲーム続行
			Open(i-1,j+1);		
			Open(i,j+1);
			Open(i+1,j+1);
			Open(i-1,j);
			Open(i+1,j);
			Open(i-1,j-1);
			Open(i,j-1);
			Open(i+1,j-1);
		}
   	}

   	Boolean Check()						
   	{
   		for(int i=1;i<11;i++)
   		{
   			for(int j=1;j<11;j++)
   			{
   				if(bom_here[i][j]!=true && open[i][j]!=true)		//全てのマスを見て爆弾がなく開いていないマスがあればゲーム続行
   				{
   					return false;
   				}
   			}
   		}
   		return true;		//ループを抜けたらゲームクリア
   	}
}




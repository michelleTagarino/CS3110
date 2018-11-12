package aimgame;

import java.applet.*;;
import java.awt.*;
import static java.awt.Font.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;
import javax.swing.*;

class Vector2D{
    public double x,y;
    Vector2D(double x, double y){
        this.x = x; this.y = y;
    }
    public String toString(){
        return "{"+x+", "+y+"}";
    }
    public Vector2D add(Vector2D other){
        return new Vector2D(this.x+other.x,this.y+other.y);
    }
    public Vector2D scale(double k){
        return new Vector2D(this.x*k,this.y*k);
    }
    public double length(){
        return Math.sqrt(this.x*this.x+this.y*this.y);
    }
    public Vector2D normalize(){
        return scale(1/length());
    }
    public double dot(Vector2D other){
        return this.x*other.x+this.y*other.y;
    }
}

class Vertex2D{
    public double x,y;
    public Vertex2D(double x, double y){
        this.x = x; this.y = y;
    }
    public String toString(){
        return "v "+x+" "+y;
    }
    
}

class Matrix2D{
    private double [][]m;//The actual matrix.  Will be a 3 by 3
    public Matrix2D(){  //Identity matrix
        m = new double[3][3];
        m[0][0]=1; m[0][1]=0; m[0][2]=0;
        m[1][0]=0; m[1][1]=1; m[1][2]=0;
        m[2][0]=0; m[2][1]=0; m[2][2]=1;      
    }
    public Matrix2D(double a, double b, double c,
                    double d, double e, double f,
                    double g, double h, double i){
        m = new double[3][3];
        m[0][0]=a; m[0][1]=b; m[0][2]=c;
        m[1][0]=d; m[1][1]=e; m[1][2]=f;
        m[2][0]=g; m[2][1]=h; m[2][2]=i;
    }
    public String toString(){
        return  "{{"+m[0][0]+", "+m[0][1]+", "+m[0][2]+"}, "+
                 "{"+m[1][0]+", "+m[1][1]+", "+m[1][2]+"}, "+
                 "{"+m[2][0]+", "+m[2][1]+", "+m[2][2]+"}}";
    }
    public Matrix2D mult(Matrix2D other){
        Matrix2D c = new Matrix2D();
        for(int i = 0; i < 3; i++){
            for(int j=0; j < 3; j++){
                c.m[i][j]=0;
                for(int k=0; k <3; k++)
                    c.m[i][j]+=this.m[i][k]*other.m[k][j];
            }
        }
        return c;
    }
    public static Matrix2D translate(double tx, double ty){
        Matrix2D m = new Matrix2D();
        m.m[0][2]=tx;
        m.m[1][2]=ty;
        return m;
    }
    public static Matrix2D scale(double sx, double sy){
        Matrix2D m = new Matrix2D();
        m.m[0][0]=sx;
        m.m[1][1]=sy;
        return m;
    }
    public static Matrix2D rotate(double theta){
        Matrix2D m = new Matrix2D();
        theta = Math.toRadians(theta);
        double s = Math.sin(theta);
        double c = Math.cos(theta);
        m.m[0][0]=c;  m.m[0][1]=-s;
        m.m[1][0]=s; m.m[1][1]=c;
        return m; 
    }
    public Vertex2D [] geometricTransform(Vertex2D [] source){
        Vertex2D [] destination = new Vertex2D[source.length];
        for(int i = 0; i < destination.length; i++){
            destination[i]=new Vertex2D(
                    source[i].x*m[0][0]+source[i].y*m[0][1]+m[0][2],
                    source[i].x*m[1][0]+source[i].y*m[1][1]+m[1][2]
                  );
        }
        return destination;
    }
}

class Model2D{ // A 2D Model build out of trangles
    public Vertex2D [] vb;
    public Triangle2D [] tb;
    public Surface2D [] sb;
    public String name;
    protected int vp = 0;
    protected int sp=0;
    protected int tp=0;
    public Model2D(String name, int nVertices, int nTriangles, int nSurfaces){
        vb = new Vertex2D[nVertices];
        tb = new Triangle2D[nTriangles];
        sb = new Surface2D[nSurfaces];
        this.name=name;
    }
    public void print(PrintStream ps){
        ps.println("#Model "+name+" "+vp+" "+tp+" "+sp);
        for(int i = 0; i < vb.length; i++)
            ps.println(vb[i]);
        for(int i = 0; i < sb.length; i++){
            ps.println("#Surface "+sb[i].name);
            ps.println("c "+sb[i].color.getRed()+" "+sb[i].color.getGreen()+" "+sb[i].color.getBlue());
            for(int j = sb[i].start; j < sb[i].end; j++)
                ps.println(tb[j]);
        }
    }
    public Model2D clone(Matrix2D transform){
        Model2D m = new Model2D(this.name+" Clone",this.getVertexCount(),this.getTiangleCount(),this.getSurfaceCount());
        //Does not do anything yet!
        return m;
    }
    public int getVertexCount(){
        return vb.length;
    }
    public int getTiangleCount(){
        return tb.length;
    }
    public int getSurfaceCount(){
        return sb.length;
    }
    public void addVertex(double x, double y){
        vb[vp++]=new Vertex2D(x,y);
    }
    public void addSurface(String name){
        sb[sp++]=new Surface2D(name,tp);
    }
    public void addSurface(String name, Color c){
        addSurface(name);
        sb[sp-1].color=c;
    }
    public void addTriangle(int a, int b, int c){
        tb[tp++]=new Triangle2D(a,b,c);
        sb[sp-1].end++;
    }
    public static Model2D read(String fname)throws Exception{
        File f = new File(fname);
        FileInputStream    fos = new FileInputStream(f);
        InputStreamReader  isr = new InputStreamReader(fos);
        BufferedReader   input = new BufferedReader(isr);
        int state = -1;
        Model2D obj=null;

        String s = input.readLine();
        while(s!=null){
            String st[] = s.split("\\s+");//strip all white space and tokenize
            if(st[0].equals("#Model")){
                state = 0;
                if(st.length==5){
                   obj = new Model2D(st[1],Integer.parseInt(st[2].trim()),Integer.parseInt(st[3].trim()),Integer.parseInt(st[4].trim()));
                }else 
                    System.out.println("Bad Format: "+s); 
            }
            else if(st[0].equals("#Surface")){
                state = 1;
                if(st.length==2){
                   obj.addSurface(st[1]);
                }else 
                    System.out.println("Bad Format: "+s);                 
            }
            else if(st[0].equals("v")){
                if(state!=0){
                    System.out.println("Illegal placement of Vertex!");
                }
                else{
                    if(st.length==3)
                        obj.addVertex(Double.parseDouble(st[1].trim()),Double.parseDouble(st[2].trim()));
                    else
                        System.out.println("Bad Format: "+s);
                }    
            }
            else if(st[0].equals("c")){
                if(state!=1){
                    System.out.println("Illegal placement of Color!");
                }
                else{
                     if(st.length==4)
                        obj.sb[obj.sp-1].color=new Color(Integer.parseInt(st[1].trim()),Integer.parseInt(st[2].trim()),Integer.parseInt(st[3].trim()));
                    else
                        System.out.println("Bad Format: "+s);  
                }    
            }
            else if(st[0].equals("t")){
                if(state!=1){
                    System.out.println("Illegal placement of Triangle!");
                }
                else{
                      if(st.length==4)
                        obj.addTriangle(Integer.parseInt(st[1].trim()),Integer.parseInt(st[2].trim()),Integer.parseInt(st[3].trim()));
                    else
                        System.out.println("Bad Format: "+s);                     
                }
                
            }
            else System.out.println("Unrecongnizable Symbol:/"+st[0]+"/");
            s = input.readLine();
        }
        return obj;
        
        
    }
    public static Model2D rectangle(double width, double height, Color color){
        Model2D m = new Model2D("Rectangle",4,2,1);
        m.addVertex(-width/2, -height/2);
        m.addVertex(-width/2, height/2);
        m.addVertex(width/2, height/2);
        m.addVertex(width/2, -height/2);
        m.addSurface("Rectangle",color);
        m.addTriangle(0,1,3);
        m.addTriangle(3,1,2);
        return m;
    }
    public static Model2D circle(double radius, int sides, Color color){
        Model2D m = new Model2D("Circle",sides+1,sides,1);
        m.addVertex(0,0);
        for(int i = 0; i < sides; i++){
            double t = Math.PI*2/sides*i;
            m.addVertex(radius*Math.cos(-t), radius*Math.sin(-t));
        }
        m.addSurface("Circle",color);
        for(int i = 1; i < sides; i++)
            m.addTriangle(0, i, i+1);
        m.addTriangle(0, sides, 1);
        return m;
    }
    public static Model2D triangle(double width, double height, Color color){
        Model2D m = new Model2D("Triangle",3,1,1);
        m.addVertex(-width/2, -height/2);
        m.addVertex(0, height/2);
        m.addVertex(width/2, -height/2);
        m.addSurface("Triangle",color);
        m.addTriangle(0,1,2);
        return m;
    }

    class Triangle2D{//the three vertices that make a triangle
        public int a,b,c;
        Triangle2D(int a, int b, int c){
            this.a=a; this.b=b; this.c=c;
        }
        public String toString(){
            return "t "+a+" "+b+" "+c;
        }
    }
    class Surface2D{ //each surface that makes up a model
        public String name;
        public int start,end;//defines a contiguous range of triangles from start to end-1
        public Color color;
        public Surface2D(String name,int start){
            this.start = start; this.color = Color.BLACK;this.name=name;this.end=start;
        }
        public String toString(){
            return "("+name+" "+start+", ["+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+"])";
        }
    }
}

class Line2D{
    public Vertex2D a, b;
    public Line2D(Vertex2D a, Vertex2D b){
        this.a=a; this.b=b;
    }
    public String toString(){
        return a.toString()+ " "+b.toString();
    }
}

class Transform2D{ //Transforms and draws points, lines, triangles, quads, and Model2Ds
    protected Matrix2D w,v;//World Transform and Viewport Transform
    protected int fillMode; //0 means wireframe, 1 means solid
    public static final int SOLID = 1;
    public static final int WIREFRAME = 0;
    public Transform2D(){
        fillMode = SOLID;
        w = new Matrix2D();
        v = Matrix2D.translate(0,MyCanvas.MAXY).mult(Matrix2D.scale(1,-1));
    }
    public void setFillMode(int fill){
        fillMode = fill==WIREFRAME?WIREFRAME:SOLID;
    }
    public void setWorldTransform(Matrix2D w){
        this.w = w;
    }
    public void draw(Graphics g, Model2D model){//Transform and draw the object
        Color oldColor = g.getColor(); //save the current colour
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = m.geometricTransform(model.vb);
        int []x = new int[3];
        int []y = new int[3];
        for(int i = 0; i < model.getSurfaceCount(); i++)
            for(int j = model.sb[i].start;j<model.sb[i].end;j++){
                x[0]=(int)(vb[model.tb[j].a].x+0.5);
                x[1]=(int)(vb[model.tb[j].b].x+0.5);
                x[2]=(int)(vb[model.tb[j].c].x+0.5);
                y[0]=(int)(vb[model.tb[j].a].y+0.5);
                y[1]=(int)(vb[model.tb[j].b].y+0.5);
                y[2]=(int)(vb[model.tb[j].c].y+0.5);
                g.setColor(model.sb[i].color);
                if(fillMode!=WIREFRAME)g.fillPolygon(x, y, 3);
                else g.drawPolygon(x, y, 3);
            } 
        g.setColor(oldColor);//restore the colour
    }
    public void draw(Graphics g, Vertex2D p){ //Draw one point (a Vertex2D)
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = new Vertex2D[1];
        vb[0]=p;
        vb = m.geometricTransform(vb);
        int x = (int)(vb[0].x+0.5); int y = (int)(vb[0].y+0.5);
        g.drawLine(x,y,x,y);
    }
    public void draw(Graphics g, Line2D l){ //Draw one Line2D
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = new Vertex2D[2];
        vb[0]=l.a;vb[1]=l.b;
        vb = m.geometricTransform(vb);
        int x1 = (int)(vb[0].x+0.5); int y1 = (int)(vb[0].y+0.5);
        int x2 = (int)(vb[1].x+0.5); int y2 = (int)(vb[1].y+0.5);
        g.drawLine(x1,y1,x2,y2);   
    }
    public void drawPoints(Graphics g, Vertex2D [] p){
        Matrix2D m = v.mult(w);
        Vertex2D [] vb;
        vb = m.geometricTransform(p);
        for(int i = 0; i < p.length; i++){
            int x = (int)(vb[i].x+0.5); int y = (int)(vb[i].y+0.5);
            g.drawLine(x,y,x,y);
        }
    }
    public void drawTriangles(Graphics g, Vertex2D [] p){
        int []x = new int[3];
        int []y = new int[3];
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = new Vertex2D[3];
        int size = p.length/3;  //How many triangles?
        for(int i = 0; i < size; i++){
            vb[0]=p[i*3];vb[1]=p[i*3+1];vb[2]=p[i*3+2];
            vb = m.geometricTransform(vb);
            x[0] = (int)(vb[0].x+0.5); y[0] = (int)(vb[0].y+0.5);
            x[1] = (int)(vb[1].x+0.5); y[1] = (int)(vb[1].y+0.5);
            x[2] = (int)(vb[2].x+0.5); y[2] = (int)(vb[2].y+0.5);
            if(fillMode!=WIREFRAME)g.fillPolygon(x, y, 3);
                else g.drawPolygon(x, y, 3);
        }
    }
    public void drawQuads(Graphics g, Vertex2D [] p){
        int []x = new int[4];
        int []y = new int[4];
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = new Vertex2D[4];
        int size = p.length/4;  //How many quads?
        for(int i = 0; i < size; i++){
            vb[0]=p[i*4];vb[1]=p[i*4+1];vb[2]=p[i*4+2];vb[3]=p[i*4+3];
            vb = m.geometricTransform(vb);
            x[0] = (int)(vb[0].x+0.5); y[0] = (int)(vb[0].y+0.5);
            x[1] = (int)(vb[1].x+0.5); y[1] = (int)(vb[1].y+0.5);
            x[2] = (int)(vb[2].x+0.5); y[2] = (int)(vb[2].y+0.5);
            x[3] = (int)(vb[3].x+0.5); y[3] = (int)(vb[3].y+0.5);
            if(fillMode!=WIREFRAME)g.fillPolygon(x, y, 4);
                else g.drawPolygon(x, y, 4);
        }
    }
    public void drawLines(Graphics g, Vertex2D [] p){
        int []x = new int[2];
        int []y = new int[2];
        Matrix2D m = v.mult(w);
        Vertex2D [] vb = new Vertex2D[2];
        int size = p.length/2;  //How many lines?
        for(int i = 0; i < size; i++){
            vb[0]=p[i*2];vb[1]=p[i*2+1];
            vb = m.geometricTransform(vb);
            x[0] = (int)(vb[0].x+0.5); y[0] = (int)(vb[0].y+0.5);
            x[1] = (int)(vb[1].x+0.5); y[1] = (int)(vb[1].y+0.5);
            g.drawLine(x[0], y[0], x[1], y[1]);
        }
    }
}



abstract class AbstractCanvas extends JComponent implements ActionListener, KeyListener{
  public static final int MAXX = 800,MAXY = 600;
  private Timer timer;
  protected long tick;

  public Dimension getPreferredSize(){
      return new Dimension(MAXX,MAXY);
  }
  public Dimension getMinimumSize(){
      return getPreferredSize();
  }
  public Dimension getMaximumSize(){
      return getPreferredSize();
  }

  public AbstractCanvas(){
      timer = new Timer(1000/60,this); //fire the timer 60 times per second
      timer.start();
      tick = 0;
      this.addKeyListener(this);
      this.setFocusable(true);
  }
  public void actionPerformed(ActionEvent e){//execute this for every timer tick
     repaint();
     tick++;
  }
  public void paintComponent(Graphics g){
     super.paintComponent(g);
     display(g);
  }
  
  public void display(Graphics g){
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

}

class MyFrame extends JFrame{
  public MyFrame(String title){
    super(title);
    MyCanvas c = new MyCanvas();
    this.getContentPane().add(c);
  }
}

public class AimGame{
    public static void main(String[] args) throws Exception{
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run(){
            MyFrame frame = new MyFrame("CS3110 Shooting Game Michelle Tagarino");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
          }
      });
    }
}
/*******************************************************************************

                    DO NOT MODIFY ANY CODE ABOVE THIS LINE 

*******************************************************************************/
class MyCanvas extends AbstractCanvas{
    private Vertex2D [] shooter = {new Vertex2D(-2,-1),new Vertex2D(2,-1),new Vertex2D(0.8,1),
                                   new Vertex2D(-0.8,1)};
    private Vertex2D [] lines   = {new Vertex2D(-1,-1),new Vertex2D(1,-1),new Vertex2D(1,1),
                                   new Vertex2D(-1,1)};
    private int x, y, dx, dy;
    private int points, bulletsLeft=20;
    private int targetScale=5, targetSpeed=1;
    private int cScale=30; //invisible rectangle around target for collision detection
    private int angle;
    private double bulletSpeed;
    private boolean collision, gameOverMessage=false;
    private String str = Integer.toString(points);
    private String str2 = Integer.toString(bulletsLeft);
    private String gameOverString = "GAME OVER!";
    private Model2D bullet1, bullet2, bullet3, bullet4;
    private Model2D gunBase;
    private Model2D target1, target2, target3, target4, target5;
    private AudioClip audioClip;
    Bullet bullet = new Bullet(0,0);
    //LinkedList<Bullet> bullets1 = new LinkedList<>();
    Targets target = new Targets(0,300);
    Gun gun = new Gun(0,y);
    
    public MyCanvas(){
        super();
        angle = 0;
        bullet1 = Model2D.circle(5,50,Color.BLACK);
        bullet2 = Model2D.circle(5,50,Color.YELLOW);
        target1 = Model2D.circle(5,50,Color.RED);
        target2 = Model2D.circle(5,50,Color.WHITE);
        target3 = Model2D.circle(5,50,Color.RED);
        target4 = Model2D.circle(5,50,Color.WHITE);
        target5 = Model2D.circle(5,50,Color.RED);
        gunBase = Model2D.circle(5,50,Color.BLACK);
        
        try {
            File f = new File("chord.wav");
            audioClip = Applet.newAudioClip(f.toURI().toURL());
        }catch(Exception ex){}
        angle=0;
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE) {
            if (bullet.getY()==0 && bullet.getX()==0) {
                bulletSpeed += 10;
                bulletsLeft--;
                if (bulletsLeft < 0) { //allows the last bullet to exit screen before stopping the next one
                    bulletSpeed = 0;
                }
                str2 = Integer.toString(bulletsLeft);
            }
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT ) {
            angle+=5;
            if (bullet.getY()==0 && bullet.getX()==0) {
                bullet.setAngle(angle);
            }
            if (angle >= 40) angle = 40;
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
            angle-=5;
            if (angle <= -40) angle = -40;
        } 
    }
    public void display(Graphics g){//called 60 times per second: So 60fps
        Transform2D transform = new Transform2D();  //Sets M to I
        
        //M=T(200,200)
        Matrix2D M = Matrix2D.translate(200, 200);     
        transform.setWorldTransform(M);
        
        //Fill objects with solid colour
        transform.setFillMode(Transform2D.SOLID);

        //Draw Bullet
        M=Matrix2D.translate(bullet.getX(), bullet.getY())
                  .mult(Matrix2D.translate(400, 0))
                  .mult(Matrix2D.rotate(angle))
                  .mult(Matrix2D.scale(0.5,2));
        transform.setWorldTransform(M);
        transform.draw(g, bullet1);
        
        //Draw Bullet
        M=Matrix2D.translate(bullet.getX(), bullet.getY())
                  .mult(Matrix2D.translate(400, 0))               
                  .mult(Matrix2D.rotate(angle))
                  .mult(Matrix2D.scale(0.5,0.5));
        transform.setWorldTransform(M);
        transform.draw(g, bullet2);
        
        //Draw Gun Base
        M=Matrix2D.translate(400,-10)
                  .mult(Matrix2D.scale(15,5));
        transform.setWorldTransform(M);
        transform.draw(g, gunBase);

        //Draw Shooter (shoots bullets)
        g.setColor(Color.BLACK);
        M=Matrix2D.translate(400,0)
                  .mult(Matrix2D.rotate(angle))
                  .mult(Matrix2D.scale(10,90));
        transform.setWorldTransform(M);
        transform.drawQuads(g, shooter);
        
        //Draw Target
        M=Matrix2D.translate(target.getX(),target.getY())
                  .mult(Matrix2D.scale(targetScale,targetScale));
        transform.setWorldTransform(M);
        transform.draw(g, target1);
        
        //Draw Target Layer
        M=Matrix2D.translate(target.getX(),target.getY())
                  .mult(Matrix2D.scale(targetScale-1,targetScale-1));
        transform.setWorldTransform(M);
        transform.draw(g, target2);
        
        //Draw Target Layer
        M=Matrix2D.translate(target.getX(),target.getY())
                  .mult(Matrix2D.scale(targetScale-2,targetScale-2));
        transform.setWorldTransform(M);
        transform.draw(g, target3);
        
        //Draw Target Layer
        M=Matrix2D.translate(target.getX(),target.getY())
                  .mult(Matrix2D.scale(targetScale-3,targetScale-3));
        transform.setWorldTransform(M);
        transform.draw(g, target4);
        
        //Draw Target Layer
        M=Matrix2D.translate(target.getX(),target.getY())
                  .mult(Matrix2D.scale(targetScale-4,targetScale-4));
        transform.setWorldTransform(M);
        transform.draw(g, target5);
        
        /***********************************************************************
         *  this section controls the bullets 
         *  calculates old and new positions using trig
         **********************************************************************/
        //Calculate delta X and delta Y 
        bullet.setAngle(angle+90);
        
        dx = (int)(bulletSpeed*Math.cos(Math.toRadians(bullet.getAngle())));
        dy = (int)(bulletSpeed*Math.sin(Math.toRadians(bullet.getAngle())));

        //Calculate vector from old position to new position
        bullet.setX(bullet.getX()+dx);
        bullet.setY(bullet.getY()+dy);

        if (bullet.getY() >= 600 || bullet.getX() >=800) {
          bullet.setX(0);
          bullet.setY(0);
          bulletSpeed = 0; 
          
          if (bulletsLeft==0) {         //alows last bullet to exit screen
              gameOverMessage = true;   //displaying the game over message
          }
        }
        /***********************************************************************
         * this section controls the targets moving across the screen
         **********************************************************************/
        //When target reaches right wall, reset x, randomize new circle position
        //Move circle right by default
        if(gameOverMessage) target.setX(-30);
        if (target.getX() <= 800) {
            target.setX(target.getX()+targetSpeed);
        }
        else {
            target.setX(0);
            target.setY((int)(Math.random() * 300) + 100);
        }
        /***********************************************************************
         * this section manages collision detection
         **********************************************************************/        
        collision = bullet.getBounds().intersects(target.getBounds());
        
        if(collision) {
            audioClip.play();
            if(bulletsLeft==0) {            //if the last bullet hits a target
                gameOverMessage = true;     //player gets rewarded points before
                points += 10;               //exiting game
                
                target.setX(-30);           //make target and bullet invisible
                bullet.setX(0);             //to the player to reflect the end
                bullet.setY(0);             //of the game
                bulletSpeed = 0;
            }
            else{
                target.setX(0);
                target.setY((int)(Math.random() * 200) + 275);

                bullet.setX(0);
                bullet.setY(0);
                bulletSpeed = 0;

                points += 10;

                if (points%100==0) {
                    bulletsLeft += 20;
                    str2 = Integer.toString(bulletsLeft);
                    targetScale--;
                    targetSpeed++;
                    target.setBounds(target.getX(), target.getY(), cScale-10, cScale-10);
                }
                str = Integer.toString(points);
            }
        }
        /***********************************************************************
         * this section displays the score and number of bullets
         **********************************************************************/
        if(gameOverMessage){          //when the game is over
            g.setColor(Color.BLACK);  //display the messages indicating so
            g.setFont(new Font("Nimbus Mono L",BOLD,50));
            g.drawString("GAME OVER!",253,300);
            
            g.setColor(Color.RED);
            g.setFont(new Font("Nimbus Mono L",BOLD,20));
            g.drawString("NO MORE BULLETS LEFT",273,370);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Nimbus Mono L",PLAIN,14));
        g.drawString("POINTS = ",10,20);
        
        g.setColor(Color.red);
        g.setFont(new Font("Nimbus Mono L",BOLD,14));
        g.drawString(str, 82,20);
        
        g.setColor(Color.black);
        g.setFont(new Font("Nimbus Mono L",PLAIN,14));
        g.drawString("BULLETS LEFT = ", 645,20);
        
        g.setColor(Color.red);
        g.setFont(new Font("Nimbus Mono L",BOLD,14));
        g.drawString(str2, 765,20);
  }
}
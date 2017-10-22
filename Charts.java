import processing.core.PApplet;
import processing.core.PFont;
import processing.net.*;
import java.net.ConnectException;

public class Charts extends PApplet{
	public static void main(String[] args) {
		PApplet.main("Charts");
        // TODO Auto-generated method stub
		
    }
	int xsheet = 900;
	int ysheet = 900;
	int num_graphs = 9;
	
	int draw_interval = 50;
	int GraphPaperSquareSize = xsheet/10;
	
	public void settings(){
		size(xsheet,(ysheet+(ysheet/num_graphs)));
    }

	
	Client client_in;
	
	int[] numbers = new int[xsheet];
	int[] x_gyr = new int[xsheet];
	int[] y_gyr = new int[xsheet];
	int[] z_gyr = new int[xsheet];
	int[] x_acc = new int[xsheet];
	int[] y_acc = new int[xsheet];
	int[] z_acc = new int[xsheet];
	int[] x_mag = new int[xsheet];
	int[] y_mag = new int[xsheet];
	int[] z_mag = new int[xsheet];
	int[] moment = new int [xsheet];
	
	String data;
	
    public void setup(){
       
    	boolean client_connected = false;
        while(!client_connected)
        {
	        try {
	        	client_in = new Client(this, "192.168.1.12", 3360);  // Connect to server on port 80
	        	client_in.write("test connectivity");
	        	if (client_in.active())
	        		client_connected = true;
	        	else
	        	{
	        		client_connected = false;
	        		delay(1000);	//in order to not hamme the server
	        	}
			} catch (Exception e) {
				
			}
        }
        client_in.write("Requeting IMU data\n");  // Use the HTTP "GET" command to ask for a webpage
        
    }

    
    public void darw_graph(int [] graph,String data,int graph_loc, 
    		int color_r,int color_g, int color_b)
    {
    	stroke(50,50,50);
    	line(0,(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs),ysheet,(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs));
		fill(0);
    	noFill();
    	stroke(color_r,color_g,color_b);
    	beginShape();
    	for(int i = 0; i<graph.length;i++)
    	{
    		vertex(i,(xsheet-draw_interval)-graph[i]);
    	}
    	endShape();
    	for(int i = 1; i<graph.length;i++)
    	{
    		graph[i-1] = graph[i];
    	}
    	try {
    		graph[graph.length-1]=Integer.parseInt(data);
    		graph[graph.length-1] = (int)map(graph[graph.length-1],-32767,32768,((xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs)*2),(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs));
			
		} catch (java.lang.NumberFormatException e) {
			graph[graph.length-1]=0;
			println("error on component: ",graph_loc,", received data:",data);		
			}
    }
    
    public void draw()
    {	
    	//if (client_in.active())	//check that the client is connected
    	if (client_in.available() > 0) {    // If there's incoming data from the client...
    		  
		    data = client_in.readString();   // ...then grab it and print it 
		    String[] IMU_values = split(data, ",");
		    if(IMU_values.length == 9)
		    {
		    	background(255);
		    	//GraphPaper
		    	/*
		    	for(int i = 0 ;i<=width/GraphPaperSquareSize;i++)
		    	{
		    		int moment = second();
		    		stroke(200);
		    		line((-frameCount%10)+i*GraphPaperSquareSize,0,(-frameCount%10)+i*GraphPaperSquareSize,height);
		    		fill(0);
		    		line(0, i*GraphPaperSquareSize,width,i*GraphPaperSquareSize);
		    		//
		    	}
		    	*/
		    	darw_graph(x_gyr,IMU_values[0],1,255,0,0);
		    	darw_graph(y_gyr,IMU_values[1],2,0,255,0);
		    	darw_graph(z_gyr,IMU_values[2],3,0,0,255);
		    	darw_graph(x_acc,IMU_values[3],4,125,125,0);
		    	darw_graph(y_acc,IMU_values[4],5,0,125,125);
		    	darw_graph(z_acc,IMU_values[5],6,125,0,125);
		    	darw_graph(x_mag,IMU_values[6],7,0,0,0);
		    	darw_graph(y_mag,IMU_values[7],8,0,255,0);
		    	darw_graph(z_mag,IMU_values[8],9,0,0,255);
		    	/*
		    	noFill();
		    	stroke(255,0,0);
		    	beginShape();
		    	for(int i = 0; i<x_gyr.length;i++)
		    	{
		    		vertex(i,(xsheet-draw_interval)-x_gyr[i]);
		    	}
		    	endShape();
		    	for(int i = 1; i<x_gyr.length;i++)
		    	{
		    		x_gyr[i-1] = x_gyr[i];
		    	}
		    	try {
		    		x_gyr[x_gyr.length-1]=Integer.parseInt(IMU_values[1]);
		    		x_gyr[x_gyr.length-1] = (int)map(x_gyr[x_gyr.length-1],-32767,32768,0,xsheet/num_graphs/2);
					
				} catch (java.lang.NumberFormatException e) {
					x_gyr[x_gyr.length-1]=0;
				}
		    	*/
		    	
		    	
		    }
	    	
		     
		  }  
    	
    	
    }

}

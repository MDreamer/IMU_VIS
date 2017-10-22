import processing.core.PApplet;
import processing.net.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import processing.opengl.*;
import saito.objloader.*;
import g4p_controls.*;

public class Charts extends PApplet{
	public static void main(String[] args) {
		PApplet.main("Charts");
        // TODO Auto-generated method stub
		
    }
	
	boolean do_graphs = true;
	
	int xsheet = 900;
	int ysheet = 900;
	int num_graphs = 9;
	
	int draw_interval = 50;
	int GraphPaperSquareSize = xsheet/10;
	
	float x,y,z;

	
	public void settings(){
		size(xsheet,(ysheet+(ysheet/num_graphs)),P3D);
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
    	x = width/2;
    	y = height/2;
    	z = 0;
    	if (do_graphs)
    	{
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
	        client_in.write("Requeting IMU data\n");  
    	}
    }

    
    public void darw_graph(int [] graph,String data,int graph_loc, 
    		int color_r,int color_g, int color_b)
    {
    	data=data.trim();	//trims the newline /n
    	stroke(50,50,50);
    	line(0,(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs),ysheet,(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs));
		fill(0);
    	noFill();
    	stroke(color_r,color_g,color_b);
    	beginShape();
    	for(int i = 0; i<graph.length;i++)
    	{
    		vertex(i,graph[i]);
    	}
    	endShape();
    	for(int i = 1; i<graph.length;i++)
    	{
    		graph[i-1] = graph[i];
    	}
    	try {
    		graph[graph.length-1]=Integer.parseInt(data);
    		//graph[graph.length-1] = (int)map(graph[graph.length-1],-32767,32768,((xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs)*2),(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs));
    		graph[graph.length-1] = (int)map(graph[graph.length-1],-32767,32768,((xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs)),(xsheet/num_graphs*(graph_loc+1))-(xsheet/num_graphs*2));
			
		} catch (java.lang.NumberFormatException e) {
			graph[graph.length-1]=0;
			println("error on component: ",graph_loc,", received data:",data);		
			}
    }
    
    public void draw()
    {
    	//Graphs
    	if (do_graphs)
    	{
    	if (client_in.available() > 0) {    // If there's incoming data from the client...
    		  
		    data = client_in.readString();   // ...then grab it and print it 
		    String[] IMU_values = split(data, ",");
		    if(IMU_values.length == 9)
		    {
		    	background(255);

		    	darw_graph(x_gyr,IMU_values[0],1,255,0,0);
		    	darw_graph(y_gyr,IMU_values[1],2,0,255,0);
		    	darw_graph(z_gyr,IMU_values[2],3,0,0,255);
		    	darw_graph(x_acc,IMU_values[3],4,125,125,0);
		    	darw_graph(y_acc,IMU_values[4],5,0,125,125);
		    	darw_graph(z_acc,IMU_values[5],6,125,0,125);
		    	darw_graph(x_mag,IMU_values[6],7,0,0,0);
		    	darw_graph(y_mag,IMU_values[7],8,0,255,0);
		    	darw_graph(z_mag,IMU_values[8],9,0,0,255);

		    	//3D model:
		    	lights();
		    	pushMatrix();
		    	translate(130, height/2, 0);
		    	rotateY(PI/Integer.parseInt(IMU_values[1]));
		    	//rotateX(-z);
		    	stroke(255,20,10);
		    	box(100);
		    	popMatrix();
		    	 
		    	
		    	
		    }
	    	
		     
		  }
    	}


    }

}

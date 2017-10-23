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
	
	float x=0,y=0,z=0;
	
	int counter=0;

	OBJModel model;
	public void settings(){
		size(xsheet,ysheet,P3D);
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
    	model = new OBJModel(this);
    	model.load("Dump_Truck.obj");
    	model.scale(1);
    	  
    	x = width/2;
    	y = height/2;
    	z = 0;
    	if (do_graphs)
    	{
	    	boolean client_connected = false;
	        while(!client_connected)
	        {
		        try {
		        	//client_in = new Client(this, "169.254.24.155", 3360);  // Connect to server on port 3360
		        	client_in = new Client(this, "192.168.1.12", 3360);  // Connect to server on port 3360
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
		    if(IMU_values.length == 12)
		    {
		    	
		    	background(32);

		    	darw_graph(x_gyr,IMU_values[0],1,255,0,0);
		    	darw_graph(y_gyr,IMU_values[1],2,0,255,0);
		    	darw_graph(z_gyr,IMU_values[2],3,0,0,255);
		    	darw_graph(x_acc,IMU_values[3],4,125,125,0);
		    	darw_graph(y_acc,IMU_values[4],5,0,125,125);
		    	darw_graph(z_acc,IMU_values[5],6,125,0,125);
		    	darw_graph(x_mag,IMU_values[6],7,255,225,255);
		    	darw_graph(y_mag,IMU_values[7],8,0,255,0);
		    	darw_graph(z_mag,IMU_values[8],9,0,0,255);

		    	//3D model:
		    	// Set a new co-ordinate space
		    	//background(0,0,0);
		    	pushMatrix();
				
				// Simple 3 point lighting for dramatic effect.
				// Slightly red light in upper right, slightly blue light in upper left, and white light from behind.
				pointLight(255, 200, 200,  400, 400,  500);
				pointLight(200, 200, 255, -400, 400,  500);
				pointLight(255, 255, 255,    0,   0, -500);
				  
				// Move bunny from 0,0 in upper left corner to roughly center of screen.
				translate(xsheet/3, ysheet/2, 0);
				  
				// Rotate shapes around the X/Y/Z axis (values in radians, 0..Pi*2)
				rotateX(Float.valueOf(IMU_values[9]));
				rotateY(Float.valueOf(IMU_values[10])); // extrinsic rotation
				rotateZ(Float.valueOf(IMU_values[11]));
				/*
				float c1 = cos(radians(roll));
				float s1 = sin(radians(roll));
				float c2 = cos(radians(pitch)); // intrinsic rotation
				float s2 = sin(radians(pitch));
				float c3 = cos(radians(yaw));
				float s3 = sin(radians(yaw));
				
				applyMatrix( c2*c3, s1*s3+c1*c3*s2, c3*s1*s2-c1*s3, 0,
				-s2, c1*c2, c2*s1, 0,
				c2*s3, c1*s2*s3-c3*s1, c1*c3+s1*s2*s3, 0,
				0, 0, 0, 1);
				*/
				pushMatrix();
				noStroke();
				model.draw();
				popMatrix();
				popMatrix();
		    	
		    	
		    	/* BOX model
		    	//background(255);
		    	lights();
		    	pushMatrix();
		    	translate(130, height/2, 0);
		    	//z=PI/100;
		    	
		    	//if (counter < 10)
		    	//{
		    	try {
		    		rotateX(Float.valueOf(IMU_values[9]));
		    		rotateY(Float.valueOf(IMU_values[10]));
		    		rotateZ(Float.valueOf(IMU_values[11]));
				} catch (java.lang.NumberFormatException e) {
				}
	    		
		    	//}
		    	//delay(50);
		    	//frameRate(30);
		    	//rotateX(-z);
		    	stroke(255,20,10);
		    	box(100);
		    	popMatrix();
		    	*/
		    }
	    	
		     
		  }
    	}
    	/*
    	//3D model:
    	//background(255);
    	lights();
    	pushMatrix();
    	translate(130, height/2, 0);
    	z=PI/100;
    	
    	//if (counter < 10)
    	//{
    		x+=z;
        	y+=z;
    		rotateY(x);
    		rotateX(y);
    		counter++;
    	//}
    	delay(50);
    	//frameRate(30);
    	//rotateX(-z);
    	stroke(255,20,10);
    	box(100);
    	popMatrix();
		*/

    }

}

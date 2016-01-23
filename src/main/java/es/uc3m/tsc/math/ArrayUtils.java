package es.uc3m.tsc.math;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ArrayUtils {
	public static boolean equal(boolean [][]a,boolean [][]b){
		if (a.length!=b.length) return false;				
		boolean r=true;
		for (int i=0;i<a.length;i++){
			if (!Arrays.equals(a[i],b[i])){
				r=false;
				break;
			}
		}		
		return r;
	}
	
	public static boolean equal(int [][]a,int [][]b){
		if (a.length!=b.length) return false;				
		boolean r=true;
		for (int i=0;i<a.length;i++){
			if (!Arrays.equals(a[i],b[i])){
				r=false;
				break;
			}
		}		
		return r;
	}
	
	public static boolean equal(double [][]a,double [][]b){
		if (a.length!=b.length) return false;				
		boolean r=true;
		for (int i=0;i<a.length;i++){
			if (!Arrays.equals(a[i],b[i])){
				r=false;
				break;
			}
		}		
		return r;
	}
	
	public static boolean almostEqual(double [][]a,double [][]b,double delta){
		if (a.length!=b.length) return false;				
		boolean r=true;
		loop1:
		for (int i=0;i<a.length;i++){
			for (int j=0;j<a[i].length;j++){
				double d=Math.abs((a[i][j]-b[i][j])/b[i][j]);
				if (d>delta){
					r=false;
					break loop1;
				}
			}
		}		
		return r;
	}
	
	public static boolean equalByRows(boolean[][] a, boolean[][] b){
		if (a.length!=b.length) return false;		
		boolean eqRow;
		boolean[] bUsed=new boolean[b.length];		
		Arrays.fill(bUsed, false);
		for (int i=0;i<a.length;i++){
			eqRow=false;
			for (int j=0;j<b.length;j++){
				if (!bUsed[j]){
					if (Arrays.equals(a[i], b[j])){
						eqRow=true;
						bUsed[j]=true;
						break;
					}
				}
			}
			if (!eqRow) return false;				
		}
		
		return true;
	}
	
	static public int[] sumCols(boolean[][] inputMatrix){
		int g=inputMatrix.length;
		int m=inputMatrix[0].length;
		int[] sumMatrix=new int[m];
		
		int i;
		for (int j=0;j<m;j++){
			sumMatrix[j]=0;
			for (i=0;i<g;i++){										
				if (inputMatrix[i][j])	sumMatrix[j]++;				
			}
		}				
		return sumMatrix;
	}
	
	static public int[] sumRows(boolean[][] inputMatrix){
		int g=inputMatrix.length;
		int m=inputMatrix[0].length;
		int[] sumMatrix=new int[g];
		boolean[] row;
		
		int i;
		
		for (i=0;i<g;i++){
			row=inputMatrix[i];
			sumMatrix[i]=0;
			for (int j=0;j<m;j++){
				if (row[j]) sumMatrix[i]++;	
																				
			}
		}				
		return sumMatrix;
	}
	
	static public boolean[][] transpose(boolean[][] inputMatrix){
		int nr=inputMatrix.length;
		int nc=inputMatrix[0].length;
		boolean[][] o=new boolean[nc][nr];
		
		for (int i=0;i<nr;i++){
			for (int j=0;j<nc;j++){
				o[j][i]=inputMatrix[i][j];				
			}
		}
		
		return o;		
	}
	
	static public int sumElems(boolean[] inputRow){
		int r=0;
		for (boolean b: inputRow){
			if (b) r+=1;
		}
		return r;
	}
	
	static public double[] calcMaxMin(double[][] data){
		double maxValue=-Double.MAX_VALUE;
        double minValue=Double.MAX_VALUE; 
		for (double[] row:data){
			for (double val:row){
				if (maxValue<val) maxValue=val;
				if (minValue>val) minValue=val;
			}
		}
		return new double[]{maxValue,minValue};
	}
	
	public static String getCanvas(double[][] data, String[] colNames, String[] toolTips){
		int width=600;
    	int height=300;
    	return getCanvas( data, colNames, toolTips, width, height);
	}
	
	public static String getCanvas(double[][] data, String[] colNames, String[] toolTips, int width, int height){
		if (data==null || data.length==0){
			return "";
		}
		String canvasId="datamatrix_canvas";
    	
    	int lineWidth=3;
    	int nc=data[0].length;
    	int nr=data.length;
    	StringBuilder s=new StringBuilder();
    	double max=-Double.MAX_VALUE;
    	double min=Double.MAX_VALUE;
    	for (int i=0;i<nr;i++){
    		for (int j=0;j<nc;j++){
    			if (data[i][j]>max) max=data[i][j];
    			if (data[i][j]<min) min=data[i][j];
    		}
    	}
    	
    	String hexColor;
    	int remainDigits;
    	
    	float normColor;
    	float hue=(float)0.2;
    	float saturation=(float)0.9; 
    	float brightness=(float)0.7;
    	Color canvasColor;
    	
    	s.append("var gene_expression_canvas = document.getElementById('"+canvasId+"');\n");
    	s.append("var width="+width+";\n");
    	s.append("var height="+height+";\n");
    	
    	s.append("gene_expression_canvas.width=width;\n");
    	s.append("gene_expression_canvas.height=height+20;\n");
    	s.append("var ctx = gene_expression_canvas.getContext('2d');\n");

    	s.append("var genExp=new Array("+nc+")\n");
    	s.append("var x=0;\nvar y=0;\n");
    	s.append("var l=(height/genExp.length)*0.9;\n");
    	s.append("var wcolormap=100;\n");
    	s.append("var i=0;\n");
    	s.append("var j=0;\n");
    	s.append("var w=0;\n");    	
    	
    	
    	int numLines=100;
    	double indx=0;
    	for (int i=0;i<nc;i++){
    		s.append("ctx.fillText('"+colNames[i]+"', x, y+0.9*l);\n");	
    		s.append("var v = ctx.measureText ('"+colNames[i]+"');\n");
    		s.append("if (v.width>w) w=v.width;\n");
    		s.append("genExp["+i+"]=[");
    		
    		normColor=(float)((data[0][i]-min)/(max-min));
    		//canvasColor=Color.getHSBColor(hue,(float)Math.sqrt(1-normColor), normColor);
    		canvasColor=Color.getHSBColor(normColor*(float)0.7,saturation, brightness);
    		
    		hexColor=Integer.toHexString(canvasColor.getRGB()&0xFFFFFF);
    		remainDigits=6-hexColor.length();
    		for (int r=0;r<remainDigits;r++) hexColor="0"+hexColor;
    		
    		s.append("'#"+hexColor+"'");
    		for (int j=1;j<numLines;j++){
    			indx=(double)j/(double)numLines*nr;
    			normColor=(float)((data[(int)indx][i]-min)/(max-min));
        		//canvasColor=Color.getHSBColor(hue, (float)Math.sqrt(1-normColor), normColor);
        		canvasColor=Color.getHSBColor(normColor*(float)0.7,saturation, brightness);
        		
        		hexColor=Integer.toHexString(canvasColor.getRGB()&0xFFFFFF);
        		remainDigits=6-hexColor.length();
        		for (int r=0;r<remainDigits;r++) hexColor="0"+hexColor;
        		
    			s.append(",'#"+hexColor+"'");
    		}
    		s.append("]\n");
    		s.append("  y=y+l*1.1;\n");
    	}
    	
    	s.append("colormap=['#FFFFFF'");
    	for (int i=numLines-2;i>=0;i--){
    		normColor=(float)i/(float)numLines;
    		//canvasColor=Color.getHSBColor(hue,(float)Math.sqrt(1-normColor), normColor);
    		canvasColor=Color.getHSBColor(normColor*(float)0.7,saturation, brightness);
    		hexColor=Integer.toHexString(canvasColor.getRGB()&0xFFFFFF);
    		remainDigits=6-hexColor.length();
    		for (int r=0;r<remainDigits;r++) hexColor="0"+hexColor;
    		
    		s.append(",'#"+hexColor+"'");
    	}
    	s.append("]\n");
    	
    	DecimalFormat myFormatter = new DecimalFormat("###.###");
    	s.append("numlabels=[");
    	s.append("'"+myFormatter.format(max)+"',");
    	for (double i=0.9;i>0;i-=0.1){
    		s.append("'"+myFormatter.format(i*(max-min)+min)+"',");
    		
    	}
    	s.append("]\n");
    	
    	s.append("x=0;\n");
    	s.append("y=0;\n");
    	s.append("lineWidth=(width-w-wcolormap)/"+numLines+";\n");
    	s.append("ctx.lineWidth = lineWidth;\n"); 
    	s.append("for (i=0;i<genExp.length;i++){\n");    	     	 
    	s.append("  x=w+lineWidth;\n");
    	s.append("  for (j=0;j<genExp[i].length;j++){\n");
    	s.append("    x+=lineWidth;\n");
    	s.append("    ctx.beginPath();\n");
    	s.append("    ctx.moveTo(x,y);\n");
    	s.append("    ctx.lineTo(x,y+l);\n");
    	s.append("    ctx.strokeStyle = genExp[i][j];\n");    	
    	s.append("    ctx.stroke();\n");
    	s.append("  };\n");
    	s.append("  y=y+l*1.1;\n");
    	s.append("}\n");
    	
    	
    	s.append("xs=width-wcolormap+20;\n");
    	s.append("xe=width-wcolormap+30;\n");    	    	
    	s.append("wl=height/("+numLines+"-1);\n");
    	s.append("ctx.lineWidth = wl;\n");    	
    	s.append("for (i=0;i<"+numLines+";i++){\n");    	
    	s.append("    ctx.beginPath();\n");
    	s.append("    ctx.moveTo(xs,i*wl);\n");
    	s.append("    ctx.lineTo(xe,i*wl);\n");
    	s.append("    ctx.strokeStyle = colormap[i];\n");    	
    	s.append("    ctx.stroke();\n");    	    	
    	s.append("}\n");
    	
    	
    	s.append("ctx.lineWidth = 1;\n");   
    	s.append("ctx.strokeStyle ='#000000';\n");     	
    	s.append("for (i=0;i<numlabels.length;i++){\n");
    	s.append("    y=(i*height)/(numlabels.length-1);\n");
    	s.append("    ctx.fillText(numlabels[i], xe+10, y+8);\n");
    	s.append("    ctx.beginPath();\n");
    	s.append("    ctx.moveTo(xe,y);\n");
    	s.append("    ctx.lineTo(xe+8,y);\n");    	   
    	s.append("    ctx.stroke();\n");
    	s.append("}\n");
    	
    	if (toolTips!=null){
	    	s.append("gene_expression_canvas.onmousemove = function(e){\n");
	    	s.append("	var tooltip=document.getElementById('tooltip');\n");
	    	s.append("	tooltip.style.visibility='visible'\n");
	    	s.append("	var label_tooltip=new Array();\n");
	    	for (int i=0;i<toolTips.length;i++){
	    		s.append("	label_tooltip["+i+"]='"+toolTips[i]+"';\n");    		
	    	}
	    	s.append("	label_tooltip["+toolTips.length+"]='"+toolTips[toolTips.length-1]+"';\n");
	    	s.append("	tooltip.style.visibility='visible';\n");
	    	
	    	s.append( " ypos = (e.offsetY || e.pageY - $(e.target).offset().top);\n");
	    	s.append("	var indx=Math.round((ypos-l/2)/(l*1.1));\n");	    	
	    	s.append("	if (indx>=label_tooltip.length) indx=label_tooltip.length-1;\n");
	    	//s.append("	var indx=Math.round((event.offsetY-l/2)/(l*1.1));\n");	    	
	    	s.append("	tooltip.innerHTML=label_tooltip[indx]\n");	
	    	
	    	s.append("	tooltip.style.left=((window.Event) ? e.pageX : event.clientX + (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft))+'px';\n");		
	    	s.append("	tooltip.style.top=((window.Event) ? e.pageY : event.clientY + (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop))+'px';\n");		
	    	
	    	//s.append("	tooltip.style.top=event.offsetY+365+'px';\n");		
	    	//s.append("	tooltip.style.left=event.offsetX+160+'px';\n");		
	    	s.append("}\n");
	    	
	    	s.append("gene_expression_canvas.onmouseout = function(event){\n");
	    	s.append("	var tooltip=document.getElementById('tooltip');\n");
	    	s.append("	tooltip.style.visibility='hidden'\n");
	    	s.append("}\n");
    	}
    	
    	return s.toString();
	}
	
	
	
	public static BufferedImage generateImage(double[][] data, int pixelsWidth, int pixelsHeight ) throws Exception{

        double x =0,y = 0;
        BufferedImage bi = new BufferedImage( pixelsWidth, pixelsHeight,  BufferedImage.TYPE_3BYTE_BGR );
        Graphics2D g=(Graphics2D)bi.getGraphics();

        int w=data[0].length;
        int l=data.length;
        double Dy=(double)pixelsHeight/(double)l;
        double Dx=(double)pixelsWidth/(double)w;
        int height = (int)Math.ceil(Dy);
        int width = (int)Math.ceil(Dx);	
        int gap=(int)Math.ceil(width/4);
        
    	
    	double max=-Double.MAX_VALUE;
    	double min=Double.MAX_VALUE;
    	for (int i=0;i<l;i++){
    		for (int j=0;j<w;j++){
    			if (data[i][j]>max) max=data[i][j];
    			if (data[i][j]<min) min=data[i][j];
    		}
    	}        
        
        float fcolor;
        
        g.setColor( Color.WHITE);
        g.fillRect( 0, 0 , pixelsWidth,pixelsHeight); 
        for( int i =0; i < l; i++ ){        	
            for( int j =0; j < w; j++ ){            	
            	fcolor=(float)((data[i][j]-min)/(max-min));
            	        		
            	g.setColor(Color.getHSBColor(fcolor*(float)0.7,(float)0.9, (float)0.7));
            	//g.setColor(Color.getHSBColor((float)0.2, (float)Math.sqrt(1-fcolor), fcolor));
                
               g.fillRect( (int)x, (int)y , width-gap,height);        
               x+=Dx;
            }
            y+=Dy;
            x=0;
        }
        g.dispose();


        return bi;
    }  

	/*
	 * Gets the index corresponding to a sorted array of A
	 * It uses the Insertion sort algorithm which is efficient for small data sets (a.length<64)
	 * 
	 * @param: A array to sort
	 * @return: index of elements of A sorted	
	 */	
	public static int[] sortIndex(double[] A){
		
		double[] sortedA=new double[A.length];
		int[] index=new int[A.length];
		double valueToInsert;
		int holePos;		
		for (int i=0;i<sortedA.length;i++){
			valueToInsert=A[i];
			holePos=i;			
			while (holePos>0 && valueToInsert<sortedA[holePos-1]){
				sortedA[holePos]=sortedA[holePos-1];
				index[holePos]=index[--holePos];
			}
			sortedA[holePos]=valueToInsert;
			index[holePos]=i;			
		}
		return index;
		/*
		int[] retindex=new int[A.length];
		retindex[0]=index[0];
		int k=1;
		for (int i=1;i<sortedA.length;i++){
			if (sortedA[i]>sortedA[i-1]){
				retindex[k++]=index[i];				
			}
		}
				
		return Arrays.copyOf(retindex, k);
		*/
	}
	
	
	public static PipedInputStream getPipedMatrix(final double[][] R, final String[] rowNames, final String[] colNames) throws IOException{	
    	PipedInputStream pi=new PipedInputStream();
    	final PipedOutputStream po=new PipedOutputStream(pi);
    	
    	 new Thread(
    	            new Runnable() {
    	                public void run() {
    	                	try {
	    	                	String row="";
	    	                	for (String attrName : colNames){
	    	                		row+=attrName+";";
	    	                	}
	    	                	row+="\r\n";
	    	                	po.write(row.getBytes());
	    	                	
	    	                	for(int i=0;i<R.length;i++){
	    	                		double[] rowI=R[i];
	    	                		row=rowNames[i]+";";
	    	                		for(int j=0;j<rowI.length;j++){
	    	                			row+=rowI[j]+";";	    	                			
	    	                		}
	    	                		row+="\r\n";
	    	                		po.write(row.getBytes());																	
	    	                	}    	                	
								po.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	                }
    	            }).start();        	
    	return pi;
    }
    
	
}


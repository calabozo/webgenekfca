
function g_plotFCA(W,H,fcadesc){

var scale=1;
var ctx;
var g;
var offsetX=0;
var offsetY=0;

var edge_color="black";
var node_color='#FF0000';
var node_selectedcolor='#0000FF';
var zoom_delta=0.5;

localplotFCA(W,H,fcadesc);

function localplotFCA(W,H,fcadesc){
	var mousex_start=0;
	var mousey_start=0;
	var mousex=0;
	var mousey=0;	
	var translate=false;
	var canvas;
	canvas=document.getElementById('g_fcacanvas');
	canvas.width=W;
	canvas.height=H;

	var butzoomin=document.getElementById('g_butzoomin');
	var butzoomout=document.getElementById('g_butzoomout');
	var butcenter=document.getElementById('g_butcenter');
	var tooltip=document.getElementById('g_tooltip');
	//var txtinfo=document.getElementById('txtinfo');
	//txtinfo.innerHTML=fcadesc.txtinfo;

	ctx=canvas.getContext('2d');
	
	g=new Graph(H,0.97*W,fcadesc);
	g.fill_graph();
	repaint();

	butcenter.onclick = function(e){
		scale=1;
		offsetX=0;
		offsetY=0;
		repaint();
	};
	butzoomin.onclick = function(e){
		scale=scale+zoom_delta;
		offsetX=offsetX-W*zoom_delta/2;
		offsetY=offsetY-H*zoom_delta/2;
		repaint();
	};
	butzoomout.onclick = function(e){
		scale=scale-zoom_delta;
	    	if (scale<1){
	    		scale=1;
		    	return;
		}else{
			offsetX=offsetX+W*zoom_delta/2;
			offsetY=offsetY+H*zoom_delta/2;
		}
		repaint();
	};
		
	canvas.onmousedown = function(e){
		var x = e.offsetX==undefined?e.layerX:e.offsetX;
		var y = e.offsetY==undefined?e.layerY:e.offsetY;
	    mousex_start = -offsetX+x;
	    mousey_start = -offsetY+y;	    
	    translate=true;
	    
	    repaint(); 	   
	};
	canvas.onmousemove = function(event){
		var x = event.offsetX==undefined?event.layerX:event.offsetX;
		var y = event.offsetY==undefined?event.layerY:event.offsetY;
	    if (translate==true){
	
		    mousex = event.clientX - canvas.offsetLeft;
		    mousey = event.clientY - canvas.offsetTop;
		    
		    mousex = x;
		    mousey = y;
	   
		    offsetX=-mousex_start+mousex;
		    offsetY=-mousey_start+mousey;
		    repaint();
	
	    }else{
	        var node=g.find_node_at((x-offsetX)/scale,(event.offsetY-offsetY)/scale);
		if (node!=null && node.label!=null){
			tooltip.innerHTML="<b>"+node.label+"</b>";
			tooltip.style.top=event.offsetY+10+"px";
			if (x+tooltip.offsetWidth<W){
				tooltip.style.left=x-10+"px";
			}else{
				tooltip.style.left=W-tooltip.offsetWidth-10+"px";
			}

			tooltip.style.visibility='visible';
		}else{
			tooltip.style.visibility='hidden';
		}
	    }
	};	
	canvas.onmouseup = function(event){
	    translate=false;
	};
};


function repaint(){
    ctx.save();
    ctx.scale(scale, scale);
    ctx.translate(offsetX/scale,offsetY/scale);
    ctx.clearRect(-150, -150, ctx.canvas.width+300, ctx.canvas.height+300);
    g.redraw();
    ctx.restore();
}

function Graph(height,width,jsondescr){
	this.num_attributes=jsondescr.numattr;
	this.selected_nodes=jsondescr.conceptinfo;
	this.label_attributes=jsondescr.attrnames;
	this.width=width;
	this.height=height;
	this.margin=width/(nCr(this.num_attributes,Math.floor(this.num_attributes/2))+1);	
	this.marginY=height/(this.num_attributes+2);
	this.node_rows_posX=new Array(this.num_attributes+1);
	this.nodes=new Array(Math.pow(2,this.num_attributes));
	this.json=jsondescr;
		
	var i;
	this.marginX=new Array(this.num_attributes+1);
	for (i=0;i<=this.num_attributes;i++){
		var y=i/this.num_attributes*2-1;
		this.marginX[i]=Math.sqrt(1-y*y)/nCr(this.num_attributes,i)*width;
	}
	

	this.fill_graph=function(){
			var i;
			for (i=0;i<this.nodes.length;i++){
				g.add_node(i);
			}
			for (i=0;i<this.nodes.length;i++){
				p=this.getparents(this.nodes[i]);
				this.nodes[i].parents=p;
			};			
		};
	this.add_node=function(id){
		var rowid=getnumattr(id);
		var posY=rowid*this.marginY + this.marginY;
		if (this.node_rows_posX[rowid]!=null){
			this.node_rows_posX[rowid]+=this.marginX[rowid];
		}else{
			var max_row_nodes=nCr(this.num_attributes,rowid);
			//this.node_rows_posX[rowid]=width/2-(max_row_nodes-1)/2*this.margin;

			var y=rowid/this.num_attributes*2-1;
			this.node_rows_posX[rowid]=width/2-Math.sqrt(1-y*y)*width/2+0.01*width;
			
		}
		var radius=2;
		var label=null;
		
		var indx;
		for (indx=this.selected_nodes.length-2;indx>=0;indx--){
			if (this.selected_nodes[indx].id==id){
				break;
			}
		}

		
		
		if (indx>-1){
			var k=id;
			var i=0;
			while (k!=0){
				if (k%2==1){
					if (label==null){
						label=this.label_attributes[i];
					}else{
						label=label+","+this.label_attributes[i];
					}
				}
				i++;
				k=k>>1;
			};
			if (label==null) label="empty";
			var phi_o=this.selected_nodes[indx].phi;
			var phi_f=this.selected_nodes[indx+1].phi;
			var d_phi=Math.abs(phi_f-phi_o);	
			if (phi_o<0){
				label+="<br/>&phi;<sub>0</sub>="+phi_o+"<br/>&phi;<sub>f</sub>="+phi_f+"<br/>&Delta;&phi;="+d_phi;
			}else{
				label+="<br/>&Phi;<sub>0</sub>="+phi_f+"<br/>&Phi;<sub>f</sub>="+phi_o+"<br/>&Delta;&Phi;="+d_phi;				
			}
			radius+=5*d_phi+2;
			
		}


		
		var new_node=new Node(id,this.node_rows_posX[rowid],posY,radius);
		new_node.label=label;
		this.nodes[id]=new_node;
		return new_node;
	};
	
	this.redraw=function(){
		ctx.fillStyle=node_color;
		ctx.strokeStyle = edge_color;
		ctx.lineWidth = 1/scale;
		var i;
		
		for (i=1;i<this.nodes.length;i++){
			//this.draw_edges(i);
			this.nodes[i].draw_edges();
		};
		
		for (i=0;i<this.nodes.length;i++){
			this.nodes[i].draw();
		};
		var id=0;
		var old_id=0;
		for (i=0;i<this.selected_nodes.length;i++){
			id=this.selected_nodes[i].id;
			this.nodes[id].draw_edge_to_node(this.nodes[old_id]);
			this.nodes[id].draw_selected();						
			old_id=id;
		}	
	};

	this.getparents=function(node){
		var i = node.id;
		var ret=new Array();
	    	var ni=getnumattr(i);
		var k;
	    	for (k=i-1;k>=0;k--){
		    	if ((i&k)==k && getnumattr(i&k)==(ni-1)){
						if (this.nodes[k].r<0){
							var p=this.getparents(this.nodes[k]);
							var j=0;
							for (j=0;j<p.length;j++){
								if (ret.indexOf(p[j])==-1)
									ret.push(p[j]);
							};
						
						}else{
							if (ret.indexOf(this.nodes[k])==-1)
							    ret.push(this.nodes[k]);
						};
	    	        };
		}	
	    	return ret;
	};
	this.find_node_at=function(x,y){
		var i;
		for (i=0;i<this.nodes.length;i++){
			//console.log("x="+this.nodes[i].posX+" y="+this.nodes[i].posY+" "+Math.sqrt(Math.pow(this.nodes[i].posX-x,2)+Math.pow(this.nodes[i].posY-y,2)));
			if (Math.sqrt(Math.pow(this.nodes[i].posX-x,2)+Math.pow(this.nodes[i].posY-y,2))<(this.nodes[i].r+5)){
				//console.log(this.nodes[i].id);
				return this.nodes[i];
			};
		};
		return null;
	};
}

function Node(id,posX,posY,r){
	this.id=id;
	this.posX=posX;
	this.posY=posY;
	this.r=r;
	this.label;
	this.parents=null;
	this.child=new Array();
	this.addchild=function(node){
		if (!(node in this.child)){
			if (node!=null && node.r>-1){
				this.child.push(node);
			}
		}
	};
	this.draw=function(){
		if (this.r>0){
		        ctx.beginPath();
		        ctx.arc(this.posX, this.posY, this.r/scale, 0, Math.PI*2, true);
	        	ctx.closePath();
		        ctx.fill();
		}
	};
	this.draw_selected=function(){
		ctx.fillStyle=node_selectedcolor;
		ctx.strokeStyle = node_selectedcolor;
		ctx.lineWidth = 2/scale;
		this.draw();
	};
	
	
	this.draw_edge_to_node=function(node_end){				
       		ctx.beginPath();
		ctx.moveTo(this.posX,this.posY);
		ctx.lineTo(node_end.posX,node_end.posY);
		ctx.stroke();
	};

	this.draw_edges=function(){		
		if (this.r>-1){
			
			for (p_node in this.parents){
				var node_end=this.parents[p_node];
	        		ctx.beginPath();
				ctx.moveTo(this.posX,this.posY);
				ctx.lineTo(node_end.posX,node_end.posY);
				ctx.stroke();
			};
		};
	};
	
}


function drawcircle(x,y,r){
	ctx.beginPath();
	ctx.arc(x, y, r, r, Math.PI*2, true); 
	ctx.closePath();
	ctx.fill();
}

function getparentsattr(i){
	var ret=new Array();
	var ni=getnumattr(i);
	var k;
	for (k=i-1;k>0;k--){
		if ((i&k)==k && getnumattr(i&k)==(ni-1)){
			ret.push(k);
		};
	};
	return ret;
}

function getnumattr(i){
	var k=0;
	while(i!=0){
		if (i%2==1) k++;
		i=i>>1;
	}
	return k;
}


function nCr(n,r)
{
  var nf=1;
  for(var i=1;i<=n;i++){nf=nf*i;} 
     
  var nr=n-r;
  var nrf=1;
  for(i=1;i<=nr;i++){ nrf=nrf*i; }

  var rf=1;
  for(i=1;i<=r;i++) { rf=rf*i; }
  return nf/(nrf*rf);
};
 

};

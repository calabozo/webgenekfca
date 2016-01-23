
var scale=1;
var selected_node=null;
var ctx;
var g;
var offsetX=0;
var offsetY=0;

var edge_color="black";
var node_color='#FF0000';
var node_selectedcolor='#0000FF';
var zoom_delta=0.5;
var rmin=5;
var rmax=15;
var norm_radius=1;

function plotFCA(W,H,fcadesc){
	var mousex_start=0;
	var mousey_start=0;
	var mousex=0;
	var mousey=0;
	
	var translate=false;



	var canvas;
	canvas=document.getElementById('fcacanvas');
	canvas.width=W;
	canvas.height=H;

	var butzoomin=document.getElementById('butzoomin');
	var butzoomout=document.getElementById('butzoomout');
	var butcenter=document.getElementById('butcenter');
	var tooltip=document.getElementById('tooltip');
	//var txtinfo=document.getElementById('txtinfo');
	//txtinfo.innerHTML=fcadesc.txtinfo;

	ctx=canvas.getContext('2d');
	
	
	if (fcadesc==null){
		selected_node=null;
		ctx.clearRect(-150, -150, ctx.canvas.width+300, ctx.canvas.height+300);
		return;
	}
	
	normalize(fcadesc);
	
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
		
	canvas.onmousedown = function(event){
	    mousex_start = -offsetX+event.offsetX;
	    mousey_start = -offsetY+event.offsetY;
	    translate=true;
	    var node=g.find_node_at(mousex_start/scale,mousey_start/scale);
	    if (node!=null){
	    	selected_node=node.id;
	    }else{
	    	selected_node=null;
	    }
	    tooltip.style.visibility='hidden';
	    repaint(); 	   
	};
	canvas.onmousemove = function(event){
	    if (translate==true){
	
		    mousex = event.clientX - canvas.offsetLeft;
		    mousey = event.clientY - canvas.offsetTop;
		    
		    mousex = event.offsetX;
		    mousey = event.offsetY;
	   
		    offsetX=-mousex_start+mousex;
		    offsetY=-mousey_start+mousey;
		    repaint();
	
	    }else{
	        var node=g.find_node_at((event.offsetX-offsetX)/scale,(event.offsetY-offsetY)/scale);
			if (node!=null){
				tooltip.innerHTML="<b>"+node.label+"</b>";
				tooltip.style.top=event.offsetY+10+"px";
				if (event.offsetX+tooltip.offsetWidth<W){
					tooltip.style.left=event.offsetX-10+"px";
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
	    extern_selected_node(selected_node);	   
	};
};

function selectNodeNumber(i){
	selected_node=i;
	repaint();
}

function repaint(){
    ctx.save();
    ctx.scale(scale, scale);
    ctx.translate(offsetX/scale,offsetY/scale);
    //ctx.clearRect(-50, -50, W+50, H+50);
    ctx.clearRect(-150, -150, ctx.canvas.width+300, ctx.canvas.height+300);
    g.redraw();
    ctx.restore();
}

function normalize(jsondescr){
	var i=0;
	var max=1.0;
	var premax=1.0;
	var n;
	for (i=0;i<jsondescr.fca.length;i++){
		n=jsondescr.fca[i].n;
		if (max<n){
			premax=max;
			max=n;			
		}else{
			if (premax<n){
				premax=n;
			}
		}
	}
	norm_radius=premax/rmax;
}

function Graph(height,width,jsondescr){
	this.num_attributes=jsondescr.numattr;
	this.label_attributes=jsondescr.attrnames;
	this.width=width;
	this.height=height;
	this.margin=width/(nCr(this.num_attributes,Math.floor(this.num_attributes/2))+1);	
	this.marginY=height/(this.num_attributes+2);
	this.node_rows_posX=new Array(this.num_attributes+1);
	this.nodes=new Array(Math.pow(2,this.num_attributes));
	this.json=jsondescr;
	
	var k=4;
	
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
			if (this.nodes[this.nodes.length-1].r<=0){			
				this.nodes[this.nodes.length-1].r=1;
			};
			if (this.nodes[0].r<=0){
				this.nodes[0].r=1;
			};
			for (i=0;i<this.nodes.length;i++){
				p=this.getparents(this.nodes[i]);
				this.nodes[i].parents=p;
			};
			var k;
			for (k=0;k<this.num_attributes;k++){
				for (i=this.nodes.length-1;i>=0;i--){
					this.nodes[i].prune_parents();					
				};
			};
			for (i=0;i<this.nodes.length;i++){
				p=this.nodes[i].parents;		
				if (p!=null){
					var j;
					for (j=0;j<p.length;j++){
						p[j].addchild(this.nodes[i]);
					};
				};
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
		var radius=0;
		var label;
		var mxreal_radiux=rmax+rmin;
		if (this.json.fca[id]!=null){
			radius=this.json.fca[id].n;
			if (radius>0){
				//radius=Math.log(radius);
				radius=radius/norm_radius;
				if (radius<rmin){
					radius=rmin;
				}
				if (radius>mxreal_radiux){
					radius=mxreal_radiux;
				}
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
				label=label+"<br/>"+this.json.fca[id].n+" probesets";
			};			
		};
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
		if (selected_node!=null && selected_node!=""){			
			this.nodes[selected_node].draw_selected();			
		};
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
			if (Math.sqrt(Math.pow(this.nodes[i].posX-x,2)+Math.pow(this.nodes[i].posY-y,2))<this.nodes[i].r){
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
		this.draw_selected_parents();
		this.draw_selected_childs();
	};
	
	this.draw_selected_parents=function(){
		this.draw_edges();
		for (p_node in this.parents){
			var node_end=this.parents[p_node];	
			if (node_end!=null){
				node_end.draw_selected_parents();
			};
		};
	};
	
	this.draw_selected_childs=function(){
		this.draw_child_edges();
		for (p_node in this.child){			
			var node_end=this.child[p_node];
			if (node_end!=null){
				node_end.draw_selected_childs();
			};
		};
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
	
	this.draw_child_edges=function(){		
		if (this.r>-1){			
			for (p_node in this.child){
				var node_end=this.child[p_node];
	        		ctx.beginPath();
				ctx.moveTo(this.posX,this.posY);
				ctx.lineTo(node_end.posX,node_end.posY);
				ctx.stroke();
			};
		};
	};

	this.prune_parents=function(){
		if (this.parents.length==0) return;

		var new_parents=new Array();
		this.parents.forEach(function(node){ new_parents.push(node);});

		var i,j,k;
		
		for (i=0;i<this.parents.length;i++){
			var grandparents=this.parents[i].parents;
			for (j=0;j<grandparents.length;j++){
				k=new_parents.indexOf(grandparents[j]);
				if (k>-1){
					new_parents.splice(k,1);	
					break;
				};
			};
		}
		this.parents=new_parents;
		return new_parents;
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
 


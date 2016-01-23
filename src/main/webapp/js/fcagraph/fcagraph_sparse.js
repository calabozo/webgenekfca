
var scale=1;
var selected_node=null;
var ctx;
var g;
var offsetX=0;
var offsetY=0;

var edge_color="#000000";
var node_color='#FFEE66';
var border_color="#0000FF";
var node_selectedcolor='#AAAAFF';
var zoom_delta=0.5;
var rmin=5;
var rmax=15;
var norm_radius=1;
var line_width=1;
var selected_line_width=3;

function plotFCA(W,H,fcadesc){
	var mousex_start=0;
	var mousey_start=0;
	var mousex=0;
	var mousey=0;
	g=null;
	selected_node=null;
	ctx=null;
	
	var translate=false;



	var canvas;
	canvas=document.getElementById('fcacanvas');
	canvas.width=W;
	canvas.height=H;

	var butzoomin=document.getElementById('butzoomin');
	var butzoomout=document.getElementById('butzoomout');
	var butsavecanvas=document.getElementById('savecanvas');
	
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
	
	g=new Graph(H,W,fcadesc);
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
	butsavecanvas.onclick = function(e){
		save_canvas();
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
			if (node!=null){
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
	this.marginY=height/(this.num_attributes+3);
	this.node_rows_posX=new Array(this.num_attributes+1);
	this.nodes=new Array(0);
	this.json=jsondescr;
	this.max_nodes=Math.pow(2,this.num_attributes)-1;
	this.ordered_nodes_index=new Array(0)
	
	var k=4;
	
	var i;
	this.marginX=new Array(this.num_attributes+1);
	this.marginX[0]=width/2
	this.marginX[this.num_attributes]=width/2
	for (i=1;i<this.num_attributes;i++){
		var normi=Math.abs(i-this.num_attributes/2)/this.num_attributes;
		this.marginX[i]=Math.pow(normi*35,2);
	}
	

	this.fill_graph=function(){
			var i;
			for (i=0;i<this.json.fca.length;i++){
				this.add_node(this.json.fca[i]);				
			}

			if (this.get_node(this.max_nodes)==null){			
				fca_node=new Object();
				fca_node.n=1;
				fca_node.id=this.max_nodes;
				this.add_node(fca_node);
			};
			if (this.get_node(0)==null){	
				fca_node=new Object();		
				fca_node.n=1;
				fca_node.id=0;
				this.add_node(fca_node);
			};			
			this.add_empty_objects();

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
			
			this.ordered_nodes_index.push(0);
			var max_r_index=0;
			var max_r=this.nodes[max_r_index].r;
			var node_r;
			for (i=1;i<this.nodes.length;i++){
				max_r_index=this.ordered_nodes_index.length-1
				node_r=this.nodes[i].r;
				if (node_r>=max_r){
					this.ordered_nodes_index.push(i);
					max_r=node_r
				}else{
					if (node_r<=this.nodes[0].r){
						this.ordered_nodes_index.splice(0,0,i);
					}else{
						for (k=0;k<max_r_index;k++){
							if (node_r>=this.nodes[k].r){
								this.ordered_nodes_index.splice(k+1,0,i);
								break;
							}
						}
					}
				}
			}
			if (this.ordered_nodes_index.length!=this.nodes.length){
				alert("Error al ordenar nodos por radio");
			}

		};
	this.get_node=function(id){
		for (i=0;i<this.nodes.length;i++){
			if (this.nodes[i].id==id){
				return this.nodes[i];
			}
		}
		return null;
	}
	this.add_node=function(fca_node){
		var rowid=getnumattr(fca_node.id);
		var posY=rowid*this.marginY + this.marginY+10;
		var posX=width/2;

		var max_nodes_per_row=nCr(this.num_attributes,rowid);
		var margin=0;
		if (rowid!=0 && rowid!=this.num_attributes){
			margin=this.marginX[rowid]+rmax+10;
			posX=margin+(width-2*margin)/(max_nodes_per_row-1)*get_pos(fca_node.id,this.num_attributes);
		}
		
		var radius=0;
		var label;
		var mxreal_radiux=rmax+rmin;
		
		radius=fca_node.n;
		if (radius>0){
			//radius=Math.log(radius);
			radius=radius/norm_radius;
			if (radius<rmin){
				radius=rmin;
			}
			if (radius>mxreal_radiux){
				radius=mxreal_radiux;
			}
			var k=fca_node.id;
			var i=0;
			while (k!=0){
				if (k%2==1){
					if (label==null){
						label=this.label_attributes[i];
					}else{
						label=label+", "+this.label_attributes[i];
					}
				}
				i++;
				k=k>>1;
			};
			if (label==null) label="empty";
			label=label+"<br/> num:"+fca_node.n;
		};			
	
		var new_node=new Node(fca_node.id,posX,posY,radius);
		new_node.label=label;
		this.nodes.push(new_node);
		return new_node;
	};
	
	this.redraw=function(){
		ctx.fillStyle=node_color;
		ctx.strokeStyle = edge_color;
		ctx.lineWidth = 10;//1/scale;
		var i;
		var node;
		
		for (i=0;i<this.nodes.length;i++){
			this.nodes[i].draw_edges(line_width);
		};

		for (i=this.ordered_nodes_index.length-1;i>=0;i--){
			node=this.nodes[this.ordered_nodes_index[i]]
			node.draw();
			node.needs_repaint_when_selected=true
		};
		if (selected_node!=null && selected_node!=""){			
				this.get_node(selected_node).draw_selected();			
		};
	};

	this.getparents=function(node){
		var i = node.id;
		var ret=new Array();
	    	var ni=getnumattr(i);
		var k;
	    	for (var idx=0;idx<this.nodes.length;idx++){
			k=this.nodes[idx].id;
			if (k>=i) continue;
            		if ((i&k)==k){
				if (ret.indexOf(this.nodes[idx])==-1){
				    ret.push(this.nodes[idx]);
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

	this.add_empty_objects=function(){
		var i;
		var j;
		var l=this.nodes.length;
		var l1=l-1;
		var inodes=new Array()
		for (i=0;i<l;i++){
			inodes.push(this.nodes[i].id);				
		}
		var j;
		for (i=0;i<l1;i++){
			ni=this.nodes[i].id;
		
			for (j=i+1;j<l;j++){
				nj=this.nodes[j].id;
				nij=ni&nj;
				if (inodes.indexOf(nij)==-1){
					inodes.push(nij);
					fca_node=new Object();
					fca_node.n=0;
					fca_node.id=nij;
					this.add_node(fca_node);
				};
			}
		}

	}

}

function Node(id,posX,posY,r){
	this.id=id;
	this.posX=posX;
	this.posY=posY;
	this.r=r;
	this.label;
	this.parents=null;
	this.child=new Array();
	this.needs_repaint_when_selected=false;
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
			ctx.strokeStyle = border_color;	
			ctx.lineWidth=2;
			ctx.stroke();

		}
	};
	this.draw_selected=function(){
		ctx.fillStyle=node_selectedcolor;
		ctx.strokeStyle = node_selectedcolor;
		ctx.lineWidth = 10;//2/scale;
		this.draw();
		this.needs_repaint_when_selected=true;
		this.draw_selected_parents();
		this.needs_repaint_when_selected=true;
		this.draw_selected_childs();
	};
	
	this.draw_selected_parents=function(){
		if (this.needs_repaint_when_selected){
			this.draw_edges(selected_line_width);
			for (p_node in this.parents){
				var node_end=this.parents[p_node];	
				if (node_end!=null){
					node_end.draw_selected_parents();
				};
			};
			this.needs_repaint_when_selected=false;
		};
	};
	
	this.draw_selected_childs=function(){
		if (this.needs_repaint_when_selected){
			this.draw_child_edges();
			for (p_node in this.child){			
				var node_end=this.child[p_node];
				if (node_end!=null){
					node_end.draw_selected_childs();
				};
			};
			this.needs_repaint_when_selected=false;
		}
	};
	
	this.draw_edges=function(lwidth){		
		if (this.r>-1){
			
			for (p_node in this.parents){
				var node_end=this.parents[p_node];
	        		ctx.beginPath();
				ctx.moveTo(this.posX,this.posY);
				ctx.lineTo(node_end.posX,node_end.posY);
				ctx.lineWidth=lwidth;
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
  var coef=1;
  var n_minus_r=n-r;
  for (var i=1;i<=r;i++){
	coef=coef*(n_minus_r+i)/i;
  }
  return coef;
};
 

function get_pos(id,numattr){

	if (id==0) return 0
	
        var pos=0;
	var offset=0;
        var k=0;
        var rowId=0;

        while(id!=0){
                if (id%2==1){
			offset=k-rowId;
			if (offset>0){
				pos=pos+nCr(offset+rowId,offset-1);
			}
                        rowId++;
                }
                k++;
                id=id>>1;
        }
	return pos;
}


function equal_array(a1,a2){
	if (a1.length!=a2.length) return false;
	for (var i=0;i<a1.length;i++){
		if (a1[i]!=a2[i]) return false;		
	}	
	return true;
}

function save_canvas(){
	var canvas=document.getElementById('fcacanvas');
	var image = canvas.toDataURL("image/png");//.replace("image/png", "image/octet-stream"); 
	//header("Content-Disposition: attachment; filename=\"$filename\""); 
	window.location.href = image;
}

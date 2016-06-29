var MindMap;
var parentData; var svg;

svg = d3.select("body").append("svg");

var w = $(document).width();
var h = $(document).height();

MindMap = function(){
	console.log("w="+w+", h="+h);
	var root;
	var nodes;
	var maxNodeSize = 50;
	var padding = 18, // separation between circles
		radius = 12,
		magnifierFactor = 15;
		minimizerFactor = 10;
	var bBoxDimension;
	var nodeEnter;
	var iconWidth = 40;
	var iconHeight = 40;
	var allData, nodesG, linksG, node, link, force, linkPath, diagonal, mindmapG;
	var mindmap, update, tick, buildPathsDatasetFromJson, clickNode, clickToExpandCollapse, clickToMultiLevel;
	var parentDataShape, parentNodeClick, showParentDataShape, hideParentDataShape, fadeInSVG, fadeOutSVG, clearSVG;

	allData = [];
	nodesG = null;
	linksG = null;
	node = null;
	link = null;
	force = d3.layout.force()
			 .gravity(0.05)
			 .charge(-2000)
			 .linkDistance(100)
				 .friction(0.5)
				 .linkStrength(1)
				 .size([w, h])
				 .on("tick", tick);

		diagonal = d3.svg.diagonal()
        .projection(function(d) {
            return [d.y, d.x];
        });

    var parentDataRadius = 190;
    var arc = d3.svg.arc() 
    	.innerRadius(0) 
    	.outerRadius(parentDataRadius) 
    	.startAngle(Math.PI) 
    	.endAngle(1.5*Math.PI);

    var arcSmall = d3.svg.arc() 
    	.innerRadius(0) 
    	.outerRadius(0) 
    	.startAngle(Math.PI) 
    	.endAngle(1.5*Math.PI);

    mindmap = function(data) {
    	root = data;
    	force.on("tick", tick);
	    svg = d3.select("svg")
				.attr("class", "container")
		        .attr("width", w + 500)
		        .attr("height", h + 300);



		mindmapG = svg.append("g").attr("class", "mindmapG");
	    linksG = mindmapG.append("g").attr("class", "linksG");
	    nodesG = mindmapG.append("g").attr("class", "nodesG");

		mindmapG.append('filter')
			  .attr('id','invert')
			  .append('feColorMatrix')
			  .attr('type','matrix')
			  .attr('values',"-1 0 0 0 1 0 -1 0 0 1 0 0 -1 0 1  0 0 0 1 0");

		mindmapG.append("linearGradient")
				.attr("id", "line-gradient")
				.attr("gradientUnits", "objectBoundingBox")
				.selectAll("stop")
				.data([
					{offset: "0%", color: "#6DA69E"},
					{offset: "80%", color: "#3A656E"},
					{offset: "100%", color: "#264F5C"}
				])
				.enter().append("stop")
				.attr("offset", function(d) { return d.offset; })
				.attr("stop-color", function(d) { return d.color; });

		node = nodesG.selectAll(".node");
		link = linksG.selectAll(".link");

	    linkPath = linksG.selectAll(".link");

	    return update();
  	};

  	update = function(){
  		fadeInSVG(600, 0);
  		nodes = flatten(root),
		links = d3.layout.tree().links(nodes);
		
		var focus = root;

		var maxChildrenNumber = 0;
				nodes.forEach(function(d){
					if(d.children)
					maxChildrenNumber = d.children.length > maxChildrenNumber ? d.children.length : maxChildrenNumber;
				});

		force.nodes(nodes)
			 .links(links)
			 .linkDistance(function(d, i){

				var min_d = 80, max_d = 150, min_i = 0, max_i = 9, t = d.source.children.length; 
			 	return min_d + ( t / max_i) * (max_d - min_d);
			 })
			 .on("tick", tick)
			 .start();
			
			link = link.data(links, function(d){ return d.target.id;});
			
			link.exit().remove();

			link.enter().insert("path", ".node")
						.attr("class", function(d){
							return d.target.is_passive ? "linkPassive" : "link";
						});	

		node = node.data(nodes, function(d){ return d.id;});
		node.exit().remove();

	  	// Enter any new nodes.
	  	nodeEnter = node.enter().append("g")
					      .attr("class", function(d){ return d.root1? "node root":"node leaf"; })
					      .on("click",function(d){
					      	return clickNode(d, this);
					      })
					      .on("mousemove", mousemoveNodes)
					      .on("mouseout", mouseoutNodes)
					      .call(force.drag);

		// Append a circle
		nodeEnter.append("svg:circle")
			      .attr("class", "shape")
			      .style("fill", function(d){
			      	if(d.root1){ return ""; }
			      		else if(d.level_2) { return "#DA555C"; }
			      		else return "#264F5C";
			      })
			      .style("stroke-width", function(d){
			      		if (d._children){return 5;}
			      		else{return 0;}
			      })
			      .style("stroke", function(d){ 
			      		if (d._children){return "#6DA69E";}
			      		else{return "";}
			      });
		
   		nodeEnter.append("svg:image")
			      .attr("xlink:href", function(d){
			      	return d.root1? "center_node_out.png":"";
			      })
			      .attr("width", "150px")
			      .attr("height", "150px")
			      .attr("align","center")
			      .attr("class",function(d){ return d.root1? "":"img_remove"})
			      .attr("x", -150/2)
			      .attr("y", -150/2);


		// png works
		nodeEnter.append("svg:image")
			      .attr("xlink:href", function(d){
			      	return d.root1? "center_node_group.png":"";
			      })
			      .attr("width", "100px")
			      .attr("height", "100px")
			      .attr("align","center")
			      .attr("class",function(d){ return d.root1? "":"img_remove"})
			      .attr("x", -100/2)
			      .attr("y", -100/2);

		var icon = nodeEnter.append("svg:image")
			      .attr("xlink:href", function(d){ 
			      	if (d.image !== undefined) {
			      		console.log(d.image);
			      		return d.image; 
			      	}else{
			      		return "";
			      	}
			      })
			      .attr("align", function(d){
			      	return d.root1? "center":"";
			      })
			      .attr("width", iconWidth)
				  .attr("height", iconHeight) 
			      .attr("class","icon")
				  .style("filter", ("filter","url(#invert)"))
				  .style("display", "block");

		nodeEnter.selectAll(".img_remove").remove();

		var textNode = nodeEnter.append("text")
			          .attr("text-anchor", "middle")
			          .style("font-size", 12)
			          .style("fill", "#ffffff");

	    nodeEnter.selectAll("text")
	    		 .text(function(d){return d.name;});

	    textNode.attr("dy", function(d){
				  	bBoxDimension = wrapNode(this.parentNode.childNodes[2], 80);
					d.lineNumber = bBoxDimension[1];
					d.bBoxWidth = bBoxDimension[0];
			      		return "";
	      			});

	    nodeEnter.select(".shape")
	    			// .attr("r", function(d){
	    			// 	var radius = wrapNode(this.parentNode.childNodes[2], 80) / 2;
	    			// 	d.radius = radius;
	    			// 	return d.root1? 0: (radius + 5); //circle radius padding
	    			// });
				.attr("r", function(d){
    				if (d.image !== undefined) { //image
    					d.radius = (d.lineNumber * magnifierFactor) + iconHeight;
    					return d.root1? 0: d.radius;
    				}else{
    					if (d.lineNumber > 0) {
    						d.radius = (d.lineNumber * magnifierFactor) + padding;
    					}else{
    						d.radius = d.bBoxWidth/2 + padding;
    					}
    					return d.root1? 0: d.radius;
    				}		
	    		});


		textNode.select("tspan.first_tspan")
				.attr("dy", function(d){
					if (d.image === undefined && d.lineNumber === 0) { //no image and 1 line
	      				return d.root1? "": "0.35em"; 
	      			}else if (d.image !== undefined && d.lineNumber === 0) { //image and 1 line
	      				return d.root1? "":"-1em";
	      			}else if (d.image !== undefined && d.lineNumber > 0){ //no image and more than 1 line
			      		return d.root1? "":"-"+ (d.lineNumber) +"em";
	      			}else{ //no image and 1 line
	      				var bbox = this.parentNode.parentNode.childNodes[2].getBBox();
	      				var distance = Math.sqrt(Math.pow((Math.abs(bbox.x) - 0), 2) + 
	      					Math.pow((Math.abs(bbox.y) - 0), 2));
	      					if (distance > d.radius) {
	      						return d.root1? "":"-"+ (distance - d.radius) / minimizerFactor  +"em";
	      					}else{
	      						return d.root1? "": "-"+ (d.radius - distance) / minimizerFactor  +"em";;
	      					}
	      			}
	      		});

		icon.attr("x", function(d){
				var bbox = this.parentNode.childNodes[2].getBBox();
				return bbox.x + bbox.width/2 - iconWidth/2;
			})
			.attr("y", function(d){
				var bbox = this.parentNode.childNodes[2].getBBox();
					if (!d.root1) {
						if ((d.lineNumber > 1)) {
							return bbox.y + 2 * (d.lineNumber * magnifierFactor) + 4;
						}else{
							return bbox.y + d.lineNumber * magnifierFactor + padding;
						}
					}else{
						return "";
					}
			});	

      	var pathsDataset = buildPathsDatasetFromJson();

        pathsDataset.paths.forEach(function(e){
        	e.source = getNodeByOurId(e.source, nodes);
        	e.target = getNodeByOurId(e.target, nodes);
        });

	    linkPath = linkPath.data(pathsDataset.paths);
	    linkPath.exit().remove();
		linkPath.enter()
		   .insert("path", ".node")
		   .attr("class", "linkPath")
		   .attr("d", diagonal);

		var fisheye = d3.fisheye.circular()
							      .radius(50);

			svg.on("mousemove", function() {
			      fisheye.focus(d3.mouse(this));

			      d3.selectAll("circle.shape").each(function(d) { d.fisheye = fisheye(d); })
			          .attr("r", function(d) { 
		    				return d.root1? 0: (d.fisheye.z  * d.radius + 2);
			          });

			      link.attr("d", function(d) {
					     var dx = d.target.fisheye.x - d.source.fisheye.x,
					           dy = d.target.fisheye.y - d.source.fisheye.y,
					           dr = Math.sqrt(dx * dx + dy * dy);
					           return   "M" + d.source.fisheye.x + "," 
					            + d.source.fisheye.y 
					            + "A" + dr + "," 
					            + dr + " 0 0,1 " 
					            + d.target.fisheye.x + "," 
					            + d.target.fisheye.y;
					});   
			}); 

		return force.start();
  	}

  	tick = function(e){
  		link.attr("d", function(d) {
			 
	    var dx = d.target.x - d.source.x,
	           dy = d.target.y - d.source.y,
	           dr = Math.sqrt(dx * dx + dy * dy);
	           return   "M" + d.source.x + "," 
	            + d.source.y 
	            + "A" + dr + "," 
	            + dr + " 0 0,1 " 
	            + d.target.x + "," 
	            + d.target.y;
	  	});
	    
	    linkPath.attr("d", function(d) {
	  		if(d.source && d.target){
	  			var dx = d.target.x - d.source.x,
			           dy = d.target.y - d.source.y,
			           dr = Math.sqrt(dx * dx + dy * dy);
			           return   "M" + d.source.x + "," 
			            + d.source.y 
			            + "A" + dr + "," 
			            + dr + " 0 0,1 " 
			            + d.target.x + "," 
			            + d.target.y;
			}
	  	});

	    node.each(collide(0.8, nodes, padding));
		node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
  	}
	
	clickNode = function(d, this2){
		if(d.level_2){ //multi-level
			clickToMultiLevel(d, this2);
		}else{ //collapse
			clickToExpandCollapse(d, this2);
		}
	}

	clickToExpandCollapse = function(d, this2){
		if (d3.event.defaultPrevented) return;

		if (d.children) {
			if(d.children.length > 0){
				d3.select(this2).select("circle")
					.style("stroke", "#6DA69E")
					.style("stroke-width", 5);
			}

			d._children = d.children;
			d.children = null;

		} else {
			d.children = d._children;
			d._children = null;

			d3.select(this2).select("circle")
					.style("stroke-width", 0);
		}
		update();
	}

	clickToMultiLevel = function(d, this2){

		showParentDataShape(d.name);
		fadeOutSVG(600, 700);
		clearSVG();

		var levelMindMap;
		levelMindMap = MindMap();

		parentData = root;

	  	root = d.level_2;
		root.fixed = true;
		root.x = w/2;
		root.y = h/2;

		return levelMindMap(root);
	}

	parentNodeClick = function(d, this2){

		hideParentDataShape();
		clearSVG();

		var levelMindMap;
		levelMindMap = MindMap();

		var w = $(document).width();
		var h = $(document).height();

	  	root = parentData;
		root.fixed = true;
		root.x = w/2;
		root.y = h/2;

		return levelMindMap(root);
	}

	showParentDataShape = function(name){
  		var parentG = svg.append("g")
  						.attr("class", "parentG");

  		parentG.append("path")
	    	.attr("d", arcSmall) 
	        .attr("class", "parentDataShape")
	    	.attr("transform", "translate(" + w + "," + 0 + ")")
	    	.style("fill", "#DA555C")
	    	.each(animateParentNode)
	    	.on("click",function(d){
				    return parentNodeClick(d, this);
				});

	    parentG.append("text")
	    	.attr("dy", ".35em")
	    	.attr("class", "parentDataText")
	        .attr("text-anchor", "middle")
	    	.attr("transform", "translate(" + (w - 100) + "," + 100 + ")")
	    	.each(animateParentNode)
	        .style("font-size", 15)
	        .style("fill", "#ffffff")
	        .style("opacity", "0")
	        .text(name);

	    function animateParentNode(){
	    	d3.selectAll(".parentDataShape")
		    	.transition()
		    	.attr("d", arc)
		    	.duration(1000);

		    d3.selectAll(".parentDataText").transition()
		    	.style("opacity", "1")
		    	.duration(1500);
	    }
  	}

  	hideParentDataShape = function(){
  		d3.selectAll(".parentDataShape")
		    	.transition()
		    	.attr("d", arcSmall)
		    	.duration(1000);

		d3.selectAll(".parentDataText").transition()
		    	.style("opacity", "0")
		    	.duration(1000);


  		var clear = d3.selectAll(".parentG").remove();
  	}

	fadeInSVG = function(duration, delay){
		d3.selectAll(".mindmapG") 
			.style("opacity", 0) 
			.transition().delay(delay).duration(duration).style("opacity", 1);
	}

	fadeOutSVG = function(duration, delay){
		d3.selectAll(".mindmapG") 
			.style("opacity", 1) 
			.transition().delay(delay).duration(duration).style("opacity", 0);
	}

	clearSVG = function(){
		var clear = d3.selectAll(".mindmapG").remove();
	}

  	buildPathsDatasetFromJson = function(){
  		var paths = [];
		for(var i in nodes){
			if(nodes[i].paths !== undefined){
				for(var j in nodes[i].paths){
					var pathData = {};
					pathData.source = parseInt(nodes[i].our_id);
					pathData.target = nodes[i].paths[j];
					
					paths.push(pathData);
				}
			}
		}
		var pathsDataset = {};
		pathsDataset.paths = paths;
		return pathsDataset;
  	}

  	return mindmap;
};
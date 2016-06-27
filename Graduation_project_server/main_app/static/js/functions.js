function getNodeByOurId(our_id, nodes){
	for(var i in nodes){
		if(nodes[i].our_id === our_id){
			return nodes[i];
		}
	}
}				

function buildPathsDatasetFromJson(){
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
	//console.log("pathsDataset: " + JSON.stringify(pathsDataset, null, 4));
	return pathsDataset;
}

function buildPathsDatasetFromMultiLevel(nodes2){
	var paths = [];
	for(var i in nodes2){
		if(nodes2[i].paths !== undefined){
			for(var j in nodes2[i].paths){
				var pathData = {};
				pathData.source = parseInt(nodes[i].our_id);
				pathData.target = nodes[i].paths[j];
				
				paths2.push(pathData);
			}
		}
	}
	var pathsDataset = {};
	pathsDataset.paths = paths;
	//console.log("pathsDataset: " + JSON.stringify(pathsDataset, null, 4));
	return pathsDataset;
}

function calculateLeafRadius(maxTextWidth, lineNumber){
	return maxTextWidth > (lineNumber * 30) ? maxTextWidth : (lineNumber * 30);
}



function wrapNode(thisText, width) {
    var text = d3.select(thisText),
	words = text.text().split(/\s+/).reverse(),
      word,
      line = [],
      lineHeight = 1.1, // ems
      y = text.attr("y"),
      dy;
       if (text.attr("dy") !== null) {
      	dy = parseFloat(text.attr("dy"));
      }else{
      	dy = 0;
      }
      tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em").attr("class", "first_tspan");
      
      lineNumber = 0,
      maxTextWidth = 0;
    while (word = words.pop()) {
      line.push(word);
      tspan.text(line.join(" "));
      if (tspan.node().getComputedTextLength() > width) {

        line.pop();
        tspan.text(line.join(" "));

      	maxTextWidth = tspan.node().getComputedTextLength() > maxTextWidth ?
      			 tspan.node().getComputedTextLength() : maxTextWidth;

        line = [word];
        tspan = text.append("tspan").attr("x", 0).attr("y", y)
        		.attr("dy", 15) //height
        		.text(word);

        lineNumber++;
      }
    }
    	maxTextWidth = tspan.node().getComputedTextLength() > maxTextWidth ?
      			 tspan.node().getComputedTextLength() : maxTextWidth;
    
    d3.select(thisText.parentNode.children[0]).attr('height', 19 * (lineNumber+1));

    return [maxTextWidth, lineNumber];
}

function wrap(text, width) {
      text.each(function() {
        wrapNode(this, width);
	});
    return 25;
}

function flatten(root) {
	  var nodes = []; 
	  var i = 0;
	 
	  function recurse(node) {
	    if (node.children) 
	      node.children.forEach(recurse);
	    if (!node.id) 
	      node.id = ++i;
	    nodes.push(node);
	  }
	 
	  recurse(root);
	  return nodes;
}
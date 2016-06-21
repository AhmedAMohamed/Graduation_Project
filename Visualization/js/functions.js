function getNodeByOurId(our_id){
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
			console.log("nodesDataset: nodes[i].paths: " + nodes[i].paths + ", i: " + i);
			for(var j in nodes[i].paths){
				var pathData = {};
				pathData.source = parseInt(nodes[i].our_id);
				pathData.target = nodes[i].paths[j];
				console.log("pathData.target: " + pathData.target);
				paths.push(pathData);
			}
		}
	}
	var pathsDataset = {};
	pathsDataset.paths = paths;
	console.log("pathsDataset: " + JSON.stringify(pathsDataset, null, 4));
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
      dy = parseFloat(text.attr("dy")),
      tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em");
      
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

    return calculateLeafRadius(maxTextWidth, lineNumber);
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
function mousemoveNodes(){
    d3.select(this).select("circle").style("fill", "#6DA69E")
    d3.select(this).select("text").style("fill", function(d){
      return d.root1? "#ffffff":"#264F5C";
    })

    if(d3.select(this)[0][0].__data__._children && d3.select(this)[0][0].__data__._children.length > 0){ 
      //collapsed
      d3.select(this).select("circle").style("stroke", "#264F5C")
      d3.select(this).select("circle").style("stroke-width", 5)
    }
  }

  function mouseoutNodes(){
    d3.select(this).select("circle").style("fill", "#264F5C")
    d3.select(this).select("text").style("fill", "#ffffff")

    if(d3.select(this)[0][0].__data__._children && d3.select(this)[0][0].__data__._children.length > 0){ 
      //collapsed
      d3.select(this).select("circle").style("stroke", "#6DA69E")
      d3.select(this).select("circle").style("stroke-width", 5)
    }
}



function collide(alpha) {
  var quadtree = d3.geom.quadtree(nodes);
  return function(d) {

    var rb = 2*d.radius + padding,
        nx1 = d.x - rb,
        nx2 = d.x + rb,
        ny1 = d.y - rb,
        ny2 = d.y + rb;
    quadtree.visit(function(quad, x1, y1, x2, y2) {
      if (quad.point && (quad.point !== d)) {
        var x = d.x - quad.point.x,
            y = d.y - quad.point.y,
            l = Math.sqrt(x * x + y * y);
          if (l < rb) {
          l = (l - rb) / l * alpha;
          d.x -= x *= l;
          d.y -= y *= l;
          quad.point.x += x;
          quad.point.y += y;
        }
      }
      return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
    });
  };
}
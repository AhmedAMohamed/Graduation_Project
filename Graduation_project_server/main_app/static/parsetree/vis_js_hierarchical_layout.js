var network = null;
var layoutMethod = "directed";

window.onload = draw;

function destroy() {
  if (network !== null) {
    network.destroy();
    network = null;
  }
}

function draw() {
  destroy();

  if (document.getElementById('json') == null) {
    return;
  }
  var string = document.getElementById('json').textContent;

  var tokens = string.split(' ');

  var id_counter = 1;
  var tree_nodes = [{
    'id': 0,
    'label': 'ROOT'
  }];
  var tree_edges = [];

  var stack = [];
  stack.push(0);

  var top = function(array) {
    return array[array.length - 1];
  };

  for (var i = 1; i< tokens.length; i++) {
    var token = tokens[i];
    var node_element;
    var edge_element;
    if (token[0] === '(') {
      node_element = {
        'id': id_counter,
        'label': token.substring(1, token.length)
      };
      edge_element = {
        'from': top(stack),
        'to': id_counter
      };
      stack.push(id_counter);
    } else {
      node_element = {
        'id': id_counter,
        'label': token.substring(0, token.indexOf(')'))
      };
      edge_element = {
        'from': top(stack),
        'to': id_counter
      };
      for (var j = token.indexOf(')'); j<token.length; j++) {
        stack.pop();
      }

    }
    id_counter++;
    tree_nodes.push(node_element);
    tree_edges.push(edge_element);
  }

  var nodes = new vis.DataSet(tree_nodes);
  var edges = new vis.DataSet(tree_edges);

  /*var gephiJSON = loadJSON("./hierarchical_visjs.json");
  var parserOptions = {
    edges: {
      inheritColors: false
    },
    nodes: {
      fixed: true,
      parseColor: false
    }
  }

  // parse the gephi file to receive an object
  // containing nodes and edges in vis format.
  var parsed = vis.network.convertGephi(gephiJSON, parserOptions);

  // provide data in the normal fashion
  var data = {
    nodes: parsed.nodes,
    edged: parsed.edges
  };*/

  // create a network
  var container = document.getElementById('mynetwork');
  

  // console.log(nodes); console.log(edges);
  var data = {
    nodes: nodes,
    edges: edges
  };

var options = {
  layout: {
      hierarchical: {
        sortMethod: layoutMethod
      }
    },
    edges: {
      smooth: true,
      arrows: {to : true },
      color: {
        color: '#141454',
        highlight:{
          color: '#660740'
        }
      }
    }, 
  nodes:{
    borderWidth: 1,
    borderWidthSelected: undefined,
    brokenImage:undefined,
    color: {
      border: '#141454',
      background: '#fcf1ed',
      highlight: {
        border: '#660740',
        background: '#f7ceb2'
      },
      hover: {
        border: '#2B7CE9',
        background: '#D2E5FF'
      }
    },
    fixed: {
      x:false,
      y:false
    },
    font: {
      color: '#000000',
      size: 30, // px
      face: 'roboto',
      background: 'none',
      strokeWidth: 0, // px
      strokeColor: '#ffffff',
      align: 'horizontal'
    },
    group: undefined,
    hidden: false,
    icon: {
      face: 'FontAwesome',
      code: undefined,
      size: 50,  //50,
      color:'#2B7CE9'
    },
    image: undefined,
    label: undefined,
    labelHighlightBold: true,
    level: undefined,
    mass: 1,
    physics: true,
    /*scaling: {
      min: 10,
      max: 30,
      label: {
        enabled: false,
        min: 14,
        max: 30,
        maxVisible: 30,
        drawThreshold: 5
      },
      customScalingFunction: function (min,max,total,value) {
        if (max === min) {
          return 0.5;
        }
        else {
          let scale = 1 / (max - min);
          return Math.max(0,(value - min)*scale);
        }
      }
    },*/
    shadow:{
      enabled: false,
      color: 'rgba(0,0,0,0.5)',
      size:10,
      x:5,
      y:5
    },
    shape: 'ellipse',
    shapeProperties: {
      borderDashes: false, // only for borders
      borderRadius: 6,     // only for box shape
      useImageSize: false,  // only for image and circularImage shapes
      useBorderWithImage: false  // only for image shape
    },
    size: 25,
    title: undefined,
    value: undefined,
    x: undefined,
    y: undefined
  }
}

  network = new vis.Network(container, data, options);
}
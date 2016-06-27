function get_max_frame(frames) {
  var max_frame,
      max = -Infinity;
  for (var index in frames) {
    var action_frame = frames[index];
    if (action_frame.score >= max && !action_frame.nextLevel) {
      max_frame = action_frame;
      max = action_frame.score
    }
  }
  return max_frame
}

function get_max_argument_word(argument_frames) {
  var max_score = -Infinity,
      max_word;
  for (var arguemnt_type in argument_frames) {
    var argument_array = argument_frames[arguemnt_type];
    for (var i = 0; i <argument_array.length; i++) {
      var argument = argument_array[i];
      if (argument.score >= max_score) {
        max_score = argument.score;
        max_word = argument.word;
      }
    }
  }
  return max_word;
}

function parseParseTree(parseTree) {
  var med = parseTree
      .trim()
      .split(' ')
      .filter(function(node) {
        return node[node.length - 1] == ')';
      })
      .map(function (node) {
        return node.substr(0, node.indexOf(')'));
      });
  var out = med
      .join(' ');
  return out;
}

function isFrameDrawable(frame, nodes) {
  var argument_frames = frame.arguments;

  if (argument_frames["A0"]) {
    var argA_frames_array = argument_frames["A0"];
    for (var i = 0; i < argA_frames_array.length; i++) {
      var argument = argA_frames_array[i];
      if (argument.word[0] == '(') argument.word = parseParseTree(argument.word);
      if (nodes[argument.word]) {
        return [argument.word, argument.argumentType == 'A0' ? true : false];
      }
    }
  }

  for (var arguemnt_type in argument_frames) {
    if (arguemnt_type != "A0") {
      var argument_array = argument_frames[arguemnt_type];
      for (var i = 0; i < argument_array.length; i++) {
        var argument = argument_array[i];
        if (nodes[argument.word]) {
          return [argument.word, argument.argumentType == 'A0' ? true : false];
        }
      }
    }
  }
  return null;
}

function getJSONFromFrames (original_frames, level, id_counter) {
  var maximum_frame,
      output_json = {},
      node_obj,
      nodes_dict = {},
      maximum_argument_word;
  // Get max score frame
  maximum_frame = get_max_frame(original_frames);
  console.log('max frame: ' + maximum_frame.word);
  if (maximum_frame.word[0] == '(') {
    maximum_frame.word = parseParseTree(maximum_frame.word);
  }
  node_obj = {
    name: maximum_frame['word'],
    children: [],
    is_passive: false,
    our_id: id_counter.now
  };
  id_counter.now++;
  nodes_dict[maximum_frame.word] = node_obj;

  // Get Arguments
  // TODO: don't return a second level frame
  maximum_argument_word = get_max_argument_word(maximum_frame.arguments);
  if (maximum_argument_word[0] == '(') {
    maximum_argument_word = parseParseTree(maximum_argument_word);
  }
  for (var argument_type in maximum_frame.arguments) {
    var argument_array = maximum_frame.arguments[argument_type];
    for (var i = 0; i <argument_array.length; i++) {
      var argument = argument_array[i];

      if (argument['word'][0] == '(') {
        argument['word'] = parseParseTree(argument['word']);
      }

      console.log('arg word: ' + argument.word);
      if (argument['word'] != maximum_argument_word) {
        node_obj = {
          name: argument['word'],
          children: [],
          is_passive: false,
          our_id: id_counter.now
        };
        id_counter.now++;
        if (argument_type != 'A0') {
          node_obj.is_passive = true;
        }
        nodes_dict[maximum_frame.word].children.push(node_obj);
        nodes_dict[argument['word']] = node_obj;
      } else {
        // Highest score argument as root
        node_obj = {
          name: argument['word'],
          root1: true,
          children: [nodes_dict[maximum_frame.word]],
          is_passive: false,
          our_id: id_counter.now
        };
        id_counter.now++;
        if (argument_type != 'A0') {
          nodes_dict[maximum_frame.word].is_passive = true;
        }
        output_json = node_obj;
        nodes_dict[argument['word']] = node_obj;
      }
    }
  }

  var scanned_frames = [];
  // Iterate over frames
  for (var loop = 0; loop < 2; loop++) {
    for (var i = 0; i < original_frames.length; i++) {
      var frame = original_frames[i];
      console.log('frame: ' + frame.word + ', max frame: ' + maximum_frame.word);
      if (frame.word != maximum_frame.word && scanned_frames.indexOf(frame.word) == -1) {
        if (nodes_dict[frame.word] == undefined && frame.nextLevel != undefined) {
          if (frame.word[0] == '(') {
            frame.word = parseParseTree(frame.word);
          }
          node_obj = {
            name: frame.word,
            children: [],
            is_passive: false,
            our_id: id_counter.now
          };
          id_counter.now++;
          node_obj['level_' + (level+1)] = getJSONFromFrames(frame.nextLevel, level+1, id_counter);
          nodes_dict[frame.word] = node_obj;
          output_json.children.push(node_obj);
          continue;
        }
        var link_word,
            link_is_A0,
            drawable_array = isFrameDrawable(frame, nodes_dict);
        if (drawable_array) {
          link_word = drawable_array[0];
          link_is_A0 = drawable_array[1];
        }
        console.log('\tlinkword: ' + link_word + 'where dict = ' + JSON.stringify(frame, null, 4));

        if (link_word) {
          if (frame.word[0] == '(') {
            frame.word = parseParseTree(frame.word);
          }
          scanned_frames.push(frame.word);
          node_obj = {
            name: frame['word'],
            children: [],
            is_passive: false,
            our_id: id_counter.now
          };
          id_counter.now++;
          nodes_dict[frame.word] = node_obj;

          var argument_frames = frame.arguments;

          for (var argument_type in argument_frames) {
            var argument_array = argument_frames[argument_type];

            for (var j = 0; j <argument_array.length; j++) {
              var argument = argument_array[j];
              if (argument.word[0] == '(') {
                argument.word = parseParseTree(argument.word);
              }
              if (argument.argumentType == 'A0') {
                if (argument.word == link_word) {
                  console.log('\t\targument is A0 and link word: ' + argument.word);
                  nodes_dict[link_word].children.push(nodes_dict[frame.word]);
                } else {
                  console.log('\t\targument is A0 and not link word: ' + argument.word);
                  node_obj = {
                    name: argument.word,
                    children: [],
                    is_passive: false,
                    our_id: id_counter.now
                  };
                  id_counter.now++;
                  nodes_dict[frame.word].children.push(node_obj);
                  nodes_dict[argument.word] = node_obj;
                }
              } else {
                if (argument.word == link_word) {
                  console.log('\t\targument is ' + argument.argumentType +'  and link word: ' + argument.word);
                  nodes_dict[frame.word].is_passive = true;
                  nodes_dict[link_word].children.push(nodes_dict[frame.word]);
                } else {
                  console.log('\t\targument is ' + argument.argumentType +'  and not link word: ' + argument.word);
                  if (nodes_dict[argument.word]) {
                    var paths = nodes_dict[frame.word].paths;
                    if (paths) {
                      paths.push(nodes_dict[argument.word].our_id)
                    } else {
                      nodes_dict[frame.word].paths = [nodes_dict[argument.word].our_id];
                    }
                  } else {
                    node_obj = {
                      name: argument.word,
                      children: [],
                      is_passive: true,
                      our_id: id_counter.now
                    };
                    id_counter.now++;
                    nodes_dict[frame.word].children.push(node_obj);
                    nodes_dict[argument.word] = node_obj;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  return output_json
}

function framesFirst(orginial_json) {
  return getJSONFromFrames(orginial_json.frames, 1, {now: 0});
};
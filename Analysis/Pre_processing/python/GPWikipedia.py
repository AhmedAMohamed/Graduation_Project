__author__ = 'yousefhamza'
import json
from wikipedia import wikipedia


class GPWikipedia():
    @staticmethod
    def json_content(name):
        entity = wikipedia.page(name)

        paragraph_list = [paragraph for paragraph in entity.content.split('\n') if paragraph != ""]
        paragraph_list.insert(0, "=" + entity.title + "=")

        # Algorithm starts here
        json_stack = []
        current_level, next_level = 0, 0
        node = {}

        for line in paragraph_list:
            if line[0] == "=":
                next_level = line.count("=") / 2
                node = {
                    "name": line.strip("=").strip(" "),
                    "text": [],
                    "subtopics": []
                }
                if next_level > current_level:
                    if json_stack:
                        parent_dict = json_stack[-1]
                        parent_dict['subtopics'].append(node)
                    json_stack.append(node)
                elif next_level < current_level:
                    for i in range(next_level, current_level + 1):
                        json_stack.pop()
                    parent_dict = json_stack[-1]
                    parent_dict['subtopics'].append(node)
                    json_stack.append(node)
                elif next_level == current_level:
                    parent_dict = json_stack[-2]
                    parent_dict["subtopics"].append(node)
                    json_stack.pop()
                    json_stack.append(node)
                current_level = next_level
            else:
                node["text"].append(line.strip(" "))
        return json.dumps(json_stack[0])
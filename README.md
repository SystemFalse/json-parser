# json-parser
This library should be usefull for parsing JSON and working with it. Main features are: support of <a href="https://github.com/json5/json5">JSON5</a>
and <a href="https://github.com/vi/json2">JSON2</a>. In the future, support for other versions of JSON is also possible. This project was made to combine 
abilities of different JSON versions in one library.
## Default usage
To start working, you can use utility class `system_false.json.Json`. It has some methods that can parse json and return parsed objects in required class.
An example:
```java
JsonObject obj;
try (FileInputStream in = new FileInputStream("file.json")) {
    obj = Json.parseJsonObject(in);
}
if (!obj.isValue("foo")) {
    throw new RuntimeException("element \"foo\" is not value");
}
System.out.println(obj.getValue("foo").asString());
```
## Parser usage
Also you can interact with json parser directly. Standart parser is `system_false.json.parser.Json5Parser`. It can parses JSON and JSON5 text.
Common methods begins with *parse* (parseObject(), parseNumber(), parseAny()). All of these methods do not requare arguments but they expect
that given JSON source was correct. If it contains syntax mistakes, parser will stop working and throws an excpetion. Using parser you can
known what is next element by using method `nextElement()`. An example:
```java
Json5Parser j5p;
FileInputStream in = new FileInputStream("file.json");
j5p = Json5Parser.create(in);
if (j5p.nextElement() != Json5Parser.ELEMENT_OBJECT) {
    throw new RuntimeException("json element is not object");
}
JsonObject obj = j5p.parseObject();
in.close();
if (!obj.isValue("foo")) {
    throw new RuntimeException("element \"foo\" is not value");
}
System.out.println(obj.getValue("foo").asString());
```
## Exception handling
To make it easier to find the error, the exception message will indicate the line number and character on which the failure occurred.
All error messages look like this:
> Exception at line *number*, index *number*: message.

Сounting strings and characters starts from 1. If you want to use regex, here is example of it:
`\w+line\s(?<line>\d+),\sindex\s(?<index>\d+):\w+`. After matching with string, you can use method `matcher.group("line")` to get string with number of
line and `matcher.group("index")` to get string with number of character.

## JsonPath

### Description and rules
JsonPath is new way to search element in deep JSON structures. To get any JSON element it is needed to compile it with method `JsonPath.compile(String path)`.
Path is building be naming all elements that shuold be got before searching element.
* To get element in JsonObject, it is nedded to write it's key. For example, path "foo" points to the element in root searching object element with key "foo".
* To get element in JsonArray, it is needed to write it's index in square brackets. For example, path "[0]" points to the element in root searching array element at index 0.
* To point to the next level object, it is needed to write dot and next level key. For example, path "foo.bar" points to the element with key "bar" in object with key "foo".
* To point to the next level array, it is needed to add nothing. For example, path "foo[0]" point to the element at index 0 in array with key "foo"
* If object has empty name, it is needed to write \0 to the path. All this object names will be interpreted as empty, so this name for element is illegal.
* According to the above rules, object names can not include any square brackets, dots and must not be equal to "\0".

### Use case:
There is this JSON document:
```json
{
    "foo": {
        "bar": [
            56,
            90,
            true
        ],
        "foo": {
            "": 20
        }
    },
    "bar": [
        [
            88,
            "foo"
        ],
        42
    ]
}
```
And the code below:
```java
String json = "...";
JsonObject obj = Json.parseJsonObject(json);
System.out.println(obj.findElement("foo.bar[2]"));
System.out.println(obj.findElement("bar[0][1]"));
System.out.println(obj.findElement("foo.foo.\0"));
```
Output will be that:
```
true
foo
20
```

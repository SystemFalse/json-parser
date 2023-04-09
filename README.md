# json-parser
This library should be usefull for parsing JSON and working with it. Main features are: support of <a href="https://github.com/json5/json5">JSON5</a>
and <a href="https://github.com/vi/json2">JSON2</a>. In the future, support for other versions of JSON is also possible. This project was made to combine 
abilities of different JSON versions in one library.
### Default usage
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
### Parser usage
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
### Exception handling
To make it easier to find the error, the exception message will indicate the line number and character on which the failure occurred.
All error messages look like this:
> Exception at line *number*, index *number*: message.

Сounting strings and characters starts from 1. If you want to use regex, here is example of it:
`\w+line\s(?<line>\d+),\sindex\s(?<index>\d+):\w+`. After matching with string, you can use method `matcher.group("line")` to get string with number of
line and `matcher.group("index")` to get string with number of character.
### Plan for upcoming updates
- [ ] Add JSON2 parser
- [ ] Add JSON parser (this version will support only JSON, not JSON5 text)
- [ ] Do some tests to check correctness of parsers work
- [ ] Think about adding json4s support

# femtojson

A tiny JSON parser for Java that is built for simplicity
and not for performance. It is based on the [official JSON grammar](https://www.json.org/json-en.html)
and uses Java objects (maps, lists, strings, ...) to represent JSON value.

## Installation

### Maven

```xml
<dependency>
    <groupId>me.bechberger.util</groupId>
    <artifactId>femtojson</artifactId>
    <version>0.3.0</version>
</dependency>
```

## Usage

### Parsing JSON

Parse JSON strings into Java objects using the `JSONParser` class:

```java
import me.bechberger.util.json.JSONParser;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class Example {
    public static void main(String[] args) throws IOException {
        // Parse a simple object
        String jsonObject = "{\"name\": \"Alice\", \"age\": 30}";
        Map<String, Object> obj = (Map<String, Object>) JSONParser.parse(jsonObject);
        
        System.out.println(obj.get("name"));  // Output: Alice
        System.out.println(obj.get("age"));   // Output: 30
        
        // Parse an array
        String jsonArray = "[1, 2, 3, 4, 5]";
        List<Object> numbers = (List<Object>) JSONParser.parse(jsonArray);
        
        System.out.println(numbers.get(0)); // Output: 1
        
        // Parse nested structures
        String complexJson = "{\"items\": [1, \"two\", 3.14], \"active\": true}";
        Map<String, Object> complex = (Map<String, Object>) JSONParser.parse(complexJson);
        
        List<Object> items = (List<Object>) complex.get("items");
        System.out.println(items.get(1)); // Output: two
        
        Boolean active = (Boolean) complex.get("active");
        System.out.println(active); // Output: true
    }
}
```

### Pretty Printing JSON

Pretty print parsed JSON objects using the `PrettyPrinter` class:

```java
import me.bechberger.util.json.JSONParser;
import me.bechberger.util.json.PrettyPrinter;
import java.io.IOException;
import java.util.*;

public class PrettyPrintExample {
    public static void main(String[] args) throws IOException {
        // Parse JSON
        String json = "{\"users\": [{\"name\": \"Alice\", \"age\": 30}, {\"name\": \"Bob\", \"age\": 25}]}";
        Object parsed = JSONParser.parse(json);
        
        // Pretty print with indentation (returns formatted string)
        String formatted = PrettyPrinter.prettyPrint(parsed);
        System.out.println(formatted);
    }
}
```

Output:
```json
{
  "users": [
    {
      "name": "Alice",
      "age": 30.0
    },
    {
      "name": "Bob",
      "age": 25.0
    }
  ]
}
```

### Compact Printing JSON

Print JSON in compact format (single line, no extra whitespace) using the `PrettyPrinter` class:

```java
import me.bechberger.util.json.JSONParser;
import me.bechberger.util.json.PrettyPrinter;
import java.io.IOException;

public class CompactPrintExample {
    public static void main(String[] args) throws IOException {
        // Parse JSON
        String json = "{\"users\": [{\"name\": \"Alice\", \"age\": 30}, {\"name\": \"Bob\", \"age\": 25}]}";
        Object parsed = JSONParser.parse(json);
        
        // Compact print (single line)
        String compact = PrettyPrinter.compactPrint(parsed);
        System.out.println(compact);
    }
}
```

Output:
```json
{"users":[{"name":"Alice","age":30.0},{"name":"Bob","age":25.0}]}
```


## Implemented Grammar

This library implements a transformed version of the original grammar using
a simple recursive descent parser. The original grammar is transformed to remove left recursion and to make it easier to parse.

```yaml
json
   element

element
    ws value ws

value
   object
   array
   string
   number
   "true"
   "false"
   "null"

object  # a JSON object is either empty ('{ }') or has members
    '{' ws '}'
    '{' member (',' member)* '}'

member  # a member is '"key": value', with arbitrary whitespace 
    ws string ws ':' element

array   # an array is either empty or has elements
    '[' ws ']'
    '[' element (',' elements)* ']'

string  # a string is characters inside '"'
    '"' character* '"

character # essentially all non control characters excluding '"' and '\'
    '0020' . '10FFFF' - '"' - '\'
    '\' escape

escape   # the characters that can be escaped + special characters
    '"'
    '\'
    '/'
    'b'
    'f'
    'n'
    'r'
    't'
    'u' hex hex hex hex

hex     # valid hexadecimal character
    digit
    'A' . 'F'
    'a' . 'f'

number  # numbers a floating points with optional exponents
    integer fraction exponent

integer
    digit
    onenine digits
    '-' digit
    '-' onenine digits

digits
    digit
    digit digits

digit
    '0'
    onenine

onenine
    '1' . '9'

fraction
    ""
    '.' digits

exponent
    ""
    'E' sign digits
    'e' sign digits

sign
    ""
    '+'
    '-'

ws   # all supported whitespace characters (as hex codepoints)
    ""
    '0020' ws
    '000A' ws
    '000D' ws
    '0009' ws
```

## Support, Feedback, Contributing

This project is open to feature requests/suggestions, bug reports etc.
via [GitHub](https://github.com/parttimenerd/femtojson/issues) issues.
Contribution and feedback are encouraged and always welcome.

## License

MIT, Copyright 2026 Johannes Bechberger and contributors
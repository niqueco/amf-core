Model: file://amf-client/shared/src/test/resources/org/raml/parser/examples/simple-json-example/input.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: file://amf-client/shared/src/test/resources/org/raml/parser/examples/simple-json-example/input.raml#/declarations/types/User_validation
  Message: Object at / must be valid
Data at //name must have length smaller than 5
Data at //lastName must have length greater than 10

  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/examples/simple-json-example/input.raml#/declarations/types/User/example/default-example
  Property: file://amf-client/shared/src/test/resources/org/raml/parser/examples/simple-json-example/input.raml#/declarations/types/User/example/default-example
  Position: Some(LexicalInformation([(13,13)-(17,7)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/examples/simple-json-example/input.raml
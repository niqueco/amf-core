Model: file://amf-client/shared/src/test/resources/org/raml/parser/types/datetime/input.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: file://amf-client/shared/src/test/resources/org/raml/parser/types/datetime/input.raml#/declarations/types/scalar/when_validation_range/prop
  Message: Scalar at / must be valid RFC3339 date
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/types/datetime/input.raml#/declarations/types/scalar/when/example/default-example
  Property: http://a.ml/vocabularies/data#value
  Position: Some(LexicalInformation([(6,17)-(6,24)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/types/datetime/input.raml
Model: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml
Profile: RAML
Conforms? false
Number of results: 2

Level: Violation

- Source: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/countryBad_validation_validation_enum/prop
  Message: Data at / must be within the values (usa,rus)
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/countryBad/example/default-example
  Property: http://a.ml/vocabularies/data#value
  Position: Some(LexicalInformation([(11,13)-(11,16)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml

- Source: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/sizesBad_validation_validation_enum/prop
  Message: Data at / must be within the values (1,2,3)
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/sizesBad/example/default-example
  Property: http://a.ml/vocabularies/data#value
  Position: Some(LexicalInformation([(19,13)-(19,14)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml
Model: file://amf-client/shared/src/test/resources/validations/examples/declared-type-ref.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: http://a.ml/vocabularies/amf/parser#exampleError
  Message: lastName should be string
name should be string

  Level: Violation
  Target: file://amf-client/shared/src/test/resources/validations/examples/declared-type-ref.raml#/declarations/types/Person/example/default-example
  Property: file://amf-client/shared/src/test/resources/validations/examples/declared-type-ref.raml#/declarations/types/Person/example/default-example
  Position: Some(LexicalInformation([(10,0)-(13,0)]))
  Location: file://amf-client/shared/src/test/resources/validations/examples/declared-type-ref.raml
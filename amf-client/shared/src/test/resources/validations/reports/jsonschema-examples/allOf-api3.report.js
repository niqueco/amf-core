Model: file://amf-client/shared/src/test/resources/validations/jsonschema/allOf/api3.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: http://a.ml/vocabularies/amf/parser#exampleError
  Message:  should be <= 30
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/validations/jsonschema/allOf/api3.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/any/schema/example/default-example
  Property: 
  Position: Some(LexicalInformation([(29,21)-(29,23)]))
  Location: file://amf-client/shared/src/test/resources/validations/jsonschema/allOf/api3.raml
ModelId: file://amf-cli/shared/src/test/resources/validations/raml/reffed-additional-properties/api.raml
Profile: RAML 1.0
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: party['anInvalidThing'].id should be string
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/raml/reffed-additional-properties/api.raml#/web-api/end-points/%2Fendpoint/post/request/application%2Fjson/application%2Fjson/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/raml/reffed-additional-properties/api.raml#/web-api/end-points/%2Fendpoint/post/request/application%2Fjson/application%2Fjson/example/default-example
  Range: [(1,0)-(10,1)]
  Location: file://amf-cli/shared/src/test/resources/validations/raml/reffed-additional-properties/example.json

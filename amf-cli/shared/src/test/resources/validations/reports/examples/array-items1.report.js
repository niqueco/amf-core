ModelId: file://amf-cli/shared/src/test/resources/validations/examples/arrayItems1.raml
Profile: RAML 1.0
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: recipients should NOT have fewer than 1 items
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/arrayItems1.raml#/web-api/endpoint/%2Fnotifications%2Femails/supportedOperation/post/expects/request/payload/application%2Fjson/shape/schema/examples/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/examples/arrayItems1.raml#/web-api/endpoint/%2Fnotifications%2Femails/supportedOperation/post/expects/request/payload/application%2Fjson/shape/schema/examples/example/default-example
  Range: [(54,16)-(62,17)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/arrayItems1.raml

ModelId: file://amf-cli/shared/src/test/resources/validations/08/validation_error1.raml
Profile: RAML 0.8
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be object
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/08/validation_error1.raml#/web-api/end-points/%2Freservations%2F%7Bpnrcreationdate%7D/get/200/application%2Fjson/application%2Fjson/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/08/validation_error1.raml#/web-api/end-points/%2Freservations%2F%7Bpnrcreationdate%7D/get/200/application%2Fjson/application%2Fjson/example/default-example
  Range: [(27,26)-(28,77)]
  Location: file://amf-cli/shared/src/test/resources/validations/08/validation_error1.raml

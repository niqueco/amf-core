Model: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api2.raml
Profile: RAML 1.0
Conforms? false
Number of results: 1

Level: Violation

- Source: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: bar should be integer
  Level: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api2.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/schema/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api2.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/schema/example/default-example
  Position: Some(LexicalInformation([(31,0)-(31,23)]))
  Location: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api2.raml
ModelId: file://amf-cli/shared/src/test/resources/validations/jsonschema/not/api1.raml
Profile: RAML 1.0
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should NOT be valid
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/jsonschema/not/api1.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/any/schema/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/jsonschema/not/api1.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/any/schema/example/default-example
  Range: [(26,21)-(26,22)]
  Location: file://amf-cli/shared/src/test/resources/validations/jsonschema/not/api1.raml

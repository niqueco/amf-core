ModelId: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml
Profile: RAML 1.0
Conforms: false
Number of results: 2

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should match exactly one schema in oneOf
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml#/web-api/end-points/%2Fep3/get/200/application%2Fjson/any/schema/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml#/web-api/end-points/%2Fep3/get/200/application%2Fjson/any/schema/example/default-example
  Range: [(51,0)-(54,0)]
  Location: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: bar should be integer
foo should be string
should match exactly one schema in oneOf

  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml#/web-api/end-points/%2Fep4/get/200/application%2Fjson/any/schema/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml#/web-api/end-points/%2Fep4/get/200/application%2Fjson/any/schema/example/default-example
  Range: [(62,0)-(63,23)]
  Location: file://amf-cli/shared/src/test/resources/validations/jsonschema/oneOf/api3.raml

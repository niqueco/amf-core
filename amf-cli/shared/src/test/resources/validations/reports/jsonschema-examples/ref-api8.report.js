ModelId: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api8.raml
Profile: RAML 1.0
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: formOfPayments[0].auditDetails.formOfPayment.createTime should be number
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api8.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/schema/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api8.raml#/web-api/end-points/%2Fep2/get/200/application%2Fjson/schema/example/default-example
  Range: [(80,0)-(86,40)]
  Location: file://amf-cli/shared/src/test/resources/validations/jsonschema/ref/api8.raml

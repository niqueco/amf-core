ModelId: file://amf-cli/shared/src/test/resources/validations/08/pattern.raml
Profile: RAML 0.8
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should match pattern "^[^0-9]*$"
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/08/pattern.raml#/web-api/end-points/%2Fresources/get/request/parameter/query/param/scalar/param/example/default-example
  Property: file://amf-cli/shared/src/test/resources/validations/08/pattern.raml#/web-api/end-points/%2Fresources/get/request/parameter/query/param/scalar/param/example/default-example
  Range: [(11,21)-(11,24)]
  Location: file://amf-cli/shared/src/test/resources/validations/08/pattern.raml

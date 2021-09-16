ModelId: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml
Profile: RAML 1.0
Conforms: false
Number of results: 4

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be integer
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int/example/invalid
  Range: [(10,15)-(10,19)]
  Location: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 127
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int8/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int8/example/invalid
  Range: [(16,15)-(16,18)]
  Location: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 32767
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int16/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int16/example/invalid
  Range: [(22,15)-(22,20)]
  Location: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 2147483647
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int32/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml#/declarations/types/scalar/int32/example/invalid
  Range: [(28,15)-(28,25)]
  Location: file://amf-cli/shared/src/test/resources/validations/types/int-formats.raml

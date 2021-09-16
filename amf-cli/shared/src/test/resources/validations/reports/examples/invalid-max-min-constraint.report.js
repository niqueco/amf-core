ModelId: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml
Profile: RAML 1.0
Conforms: false
Number of results: 6

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be >= 2.4
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad1
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad1
  Range: [(10,12)-(10,13)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be >= 2.4
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad2
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad2
  Range: [(11,12)-(11,15)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be >= 2.4
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad3
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad3
  Range: [(12,12)-(12,15)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 5.3
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad4
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad4
  Range: [(13,12)-(13,15)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 5.3
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad5
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad5
  Range: [(14,12)-(14,15)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be <= 5.3
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad6
  Property: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml#/declarations/types/scalar/OtherCustomType/example/bad6
  Range: [(15,12)-(15,13)]
  Location: file://amf-cli/shared/src/test/resources/validations/examples/invalid-max-min-constraint.raml

ModelId: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml
Profile: RAML 1.0
Conforms: false
Number of results: 3

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: r.c should be string
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/A/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/A/example/invalid
  Range: [(18,0)-(25,0)]
  Location: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: r.c should be string
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/B/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/B/example/invalid
  Range: [(38,0)-(44,0)]
  Location: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml

- Constraint: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: r.c should be string
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/C/example/invalid
  Property: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml#/declarations/types/C/example/invalid
  Range: [(56,0)-(60,14)]
  Location: file://amf-cli/shared/src/test/resources/validations/recursives/inherits-and-props.raml

ModelId: file://amf-cli/shared/src/test/resources/validations/traits/trait1.raml
Profile: RAML 1.0
Conforms: false
Number of results: 1

Level: Violation

- Constraint: http://a.ml/vocabularies/amf/parser#closed-shape
  Message: Property '/hi' not supported in a RAML 1.0 trait node
  Severity: Violation
  Target: file://amf-cli/shared/src/test/resources/validations/traits/trait1.raml#/declarations/traits/trait/secured/applied/secured
  Property: 
  Range: [(13,4)-(13,8)]
  Location: file://amf-cli/shared/src/test/resources/validations/traits/trait1.raml

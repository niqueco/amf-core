Model: file://amf-client/shared/src/test/resources/validations/resource_types/parametrized-includes-of-examples/api.raml
Profile: RAML 1.0
Conforms? false
Number of results: 1

Level: Violation

- Source: http://a.ml/vocabularies/amf/parser#example-validation-error
  Message: should be integer
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/validations/resource_types/parametrized-includes-of-examples/api.raml#/web-api/end-points/%2Fendpoint2/get/request/default/scalar/default/examples/example/myExample
  Property: 
  Position: Some(LexicalInformation([(3,9)-(3,27)]))
  Location: file://amf-client/shared/src/test/resources/validations/resource_types/parametrized-includes-of-examples/example.raml
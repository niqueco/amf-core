package amf.validation

import amf.RAML08Profile
import amf.core.remote.{Hint, RamlYamlHint}

class RamlExamplesValidationTest extends MultiPlatformReportGenTest {

  override val basePath: String    = "file://amf-client/shared/src/test/resources/validations/"
  override val reportsPath: String = "amf-client/shared/src/test/resources/validations/reports/examples/"

  test("Array minCount 1") {
    validate("examples/arrayItems1.raml", Some("array-items1.report"))
  }

  test("Example model validation test") {
    validate("examples/examples_validation.raml", Some("examples_validation.report"))
  }

  // todo: review test ofor warning schema
  test("Validates html example value example1.raml") {
    validate("examples/example1.raml", Some("example1.report"))
  }

  test("Discriminator test 1") {
    validate("examples/discriminator1.raml", Some("discriminator1.report"))
  }

  test("Discriminator test 2") {
    validate("examples/discriminator2.raml", Some("discriminator2.report"))
  }

  test("Shape facets model validations test") {
    validate("facets/custom-facets.raml", Some("custom-facets.report"))
  }

  test("Annotations validations test") {
    validate("annotations/annotations.raml", Some("annotations.report"))
  }

  test("Annotations enum validations test") {
    validate("annotations/annotations_enum.raml", Some("annotations_enum.report"))
  }

  test("MinLength, maxlength facets validations test") {
    validate("types/lengths.raml", Some("min-max-length.report"))
  }

  test("big numbers validations test") {
    validate("types/big_nums.raml", Some("big_nums.report"))
  }

  test("Parse and validate named examples as external framents") {
    validate("examples/inline-named-examples/api.raml", Some("inline-named-examples.report"))
  }

  test("Nil value validation") {
    validate("examples/nil_validation.raml", Some("nil-validation.report"))
  }

  test("Example invalid min and max constraint validations test") {
    validate("examples/invalid-max-min-constraint.raml", Some("invalid-max-min-constraint.report"))
  }

  test("Test js custom validation - multiple of") {
    validate("custom-js-validations/mutiple-of.raml", Some("mutiple-of.report"))
  }

  test("Can validate an array example") {
    validate("examples/invalid-property-in-array-items.raml", Some("invalid-property-in-array-items.report"))
  }

  test("Param in raml 0.8 api") {
    validate("08/pattern.raml", Some("pattern-08.report"), profile = RAML08Profile)
  }

  test("Validation error raml 0.8 example 1") {
    validate("08/validation_error1.raml", Some("validation_error1.report"), profile = RAML08Profile)
  }

  test("Test validate pattern with invalid example") {
    validate("examples/pattern-invalid.raml", Some("pattern-invalid.report"))
  }

  test("Test failed union ex 1 with invalid example") {
    validate("examples/union1-invalid.raml", Some("union1-invalid.report"))
  }

  // this is not in ParaPayloadValidation test because we need to check the validation against a raml 08 parsed and resolved model (with that profile).
  test("Raml 0.8 Query Parameter Negative test case") {
    validate("/08/date-query-parameter.raml", Some("date-query-parameter.report"), profile = RAML08Profile)
  }

  test("Invalid example validation over union shapes") {
    validate("/shapes/invalid-example-in-unions.raml", Some("example-in-unions.report"))
  }

  test("Test resource type invalid examples args validation") {
    validate("/resource_types/parameterized-references/input.raml", Some("examples-resource-types.report"))
  }

  // In fact, the violation its while resolving the model, not running validation itself
  test("Invalid key in trait test") {
    validate("/traits/trait1.raml", Some("invalid-hey-trait.report"))
  }

  test("Invalid nested endpoint in resource type") {
    validate("/resource_types/nested-endpoint.raml", Some("nested-endpoint.report"))
  }

  test("Test minItems maxItems examples") {
    validate("/examples/min-max-items.raml", Some("min-max-items.report"))
  }

  test("Date times invalid examples test") {
    validate("/examples/date_time_validations2.raml", Some("date-time-validation.report"))
  }

  test("Example xml with sons results test") {
    validate("/xmlexample/offices_xml_type.raml", Some("offices-xml-type.report"))
  }

  test("Invalid number prop with bool example") {
    validate("/examples/number-prop-bool-example.raml", Some("number-prop-bool-example.report"))
  }

  test("Invalid double example for int type") {
    validate("/examples/double-example-inttype.raml", Some("double-example-inttype.report"))
  }

  test("Invalid annotation value") {
    validate("/examples/invalid-annotation-value.raml", Some("invalid-annotation-value.report"))

  }

  test("Test validate declared type from header") {
    validate("/examples/declared-from-header.raml", Some("declared-from-header.report"))

  }

  test("Test maxProperties and minProperties constraints example") {
    validate("/examples/min-max-properties-example.raml", Some("min-max-properties-example.report"))
  }

  test("lock-unlock example test (raml dates)") {
    validate("/examples/raml-dates/lockUnlockStats.raml", Some("raml-dates-lockunlock.report"))
  }

  test("Pattern properties key") {
    validate("/examples/pattern-properties/pattern_properties.raml",
             Some("pattern-properties/pattern_properties.report"))
  }

  test("Pattern properties key 2 (all additional properties)") {
    validate("/examples/pattern-properties/pattern_properties2.raml",
             Some("pattern-properties/pattern_properties2.report"))
  }

  test("Pattern properties key 3 (precedence)") {
    validate("/examples/pattern-properties/pattern_properties3.raml",
             Some("pattern-properties/pattern_properties3.report"))
  }

  test("Pattern properties key 4 (additionalProperties: false clash)") {
    validate("/examples/pattern-properties/pattern_properties4.raml",
             Some("pattern-properties/pattern_properties4.report"))
  }

  test("Check binary file included for string shape example") {
    validate("/examples/image-file-example/in-stringshape.raml", Some("image-file-example/in-stringshape.report"))
  }

  test("File examples always validate unless encoding problems are found") {
    validate("/examples/image-file-example/in-fileshape.raml", Some("image-file-example/in-fileshape.report"))
  }

  test("Examples facet with an array instead a map") {
    validate("/examples/examples-array-not-map.raml", Some("examples-array-not-map.report"))
  }

  // is here this test ok? or i should move to another new test suit
  test("Test validate default value") {
    validate("/examples/invalid-default.raml", Some("invalid-default.report"))
  }

  test("Test invalid example in object against enum") {
    validate("/enums/invalid-obj-example-enum.raml", Some("invalid-obj-example-enum.report"))
  }

  test("Test invalid example in array against enum") {
    validate("/enums/invalid-array-enums.raml", Some("invalid-array-enums.report"))
  }

  test("Test invalid example in obj array against enum") {
    validate("/enums/invalid-obj-array-enums.raml", Some("invalid-obj-array-enums.report"))
  }

  test("Test invalid obj enum value") {
    validate("/enums/invalid-obj-enum.raml", Some("invalid-obj-enum.report"))
  }

  override val hint: Hint = RamlYamlHint
}
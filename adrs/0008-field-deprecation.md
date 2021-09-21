# 8. Field deprecation

Date: 2021-09-20

## Status

Accepted

## Context

We need to start deprecating old fields and have no mechanism to do so.

## Decision

We will start deprecating fields. Deprecated fields should no longer be emitted nor set, but should still be parsed from JSON-LD. Getter/setter methods in Scala and Platform classes will be deprecated also.

How to deprecate a field:
1. Deprecate field definition in model class
   1. Deprecate field definition with `@deprecated` annotation
   2. Deprecate field definition with the `deprecated=true` paramter from the `Field` class
   3. Annotate the `fields` value assignment with `@silent("deprecated")` annotation to avoid compilation errors from deprecated fields
2. Deprecate getter/setter methods in Scala and Platform classes with `@deprecated` annotation
3. Update usages of getter/setter methods

## Consequences

Raw JSON-LD consumers will experience breaking changes. Consumers that parse JSON-LD with AMF will be unaffected until the removal of the getter/setter methods in the next release.
Braking in JSON-LD emission can still be emitted at the cost of having to silence (`@silent` annotation) all usages of deprecated fields across the AMF code.

package amf.core.client.scala.validation

import amf.core.client.common.validation.ProfileName
import amf.core.internal.validation.EffectiveValidations
import amf.core.internal.validation.core.ValidationProfile

object EffectiveValidationsCompute {

  def build(profile: ProfileName, constraints: Map[ProfileName, ValidationProfile]): Option[EffectiveValidations] = {
    computeApplicableConstraints(profile, constraints)
  }

  def buildAll(constraints: Map[ProfileName, ValidationProfile]): Map[ProfileName, EffectiveValidations] = {
    constraints.keySet.foldLeft(Map[ProfileName, EffectiveValidations]()) { (acc, curr) =>
      build(curr, constraints).map(eff => acc + (curr -> eff)).getOrElse(acc)
    }
  }

  private def computeApplicableConstraints(
      profileName: ProfileName,
      constraints: Map[ProfileName, ValidationProfile]): Option[EffectiveValidations] = {
    val profiles = findProfileHierarchy(profileName, constraints)
    if (profiles.isEmpty) return None

    val applicable = EffectiveValidations()
    profiles.foldLeft(applicable) { (acc, curr) =>
      acc.someEffective(curr)
    }
    Some(applicable)
  }

  private def findProfileHierarchy(profileName: ProfileName,
                                   constraints: Map[ProfileName, ValidationProfile],
                                   seen: Set[ProfileName] = Set.empty): Seq[ValidationProfile] = {
    if (seen.contains(profileName)) return Seq.empty
    constraints
      .map {
        case (key, value) => key.p -> value
      }
      .get(profileName.p)
      .map { profile =>
        profile.baseProfile
          .map(base => findProfileHierarchy(base, constraints, seen + profile.name))
          .getOrElse(Seq.empty) ++ Seq(profile)
      }
      .getOrElse(Seq.empty)
  }
}

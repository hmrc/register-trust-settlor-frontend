import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.13.0"

  private val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc" %% "domain-play-30"                        % "11.0.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30"            % "12.1.0",
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "3.3.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.jsoup"             % "jsoup"                  % "1.21.1",
    "org.scalatestplus"    %% "scalacheck-1-17"        % "3.2.18.0",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"  % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]      = compile ++ test
}

import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"

  private val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc" %% "domain-play-30"                        % "9.0.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30"            % "9.11.0",
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "2.0.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.jsoup"          % "jsoup"                   % "1.17.2",
    "org.mockito"       %% "mockito-scala-scalatest" % "1.17.37",
    "org.scalatestplus" %% "scalacheck-1-17"         % "3.2.18.0",
    "wolfendale"        %% "scalacheck-gen-regexp"   % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]      = compile ++ test
}

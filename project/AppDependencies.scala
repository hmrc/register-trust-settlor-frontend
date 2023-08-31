import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.21.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "play-frontend-hmrc"            % "7.19.0-play-28",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.13.0-play-28",
    "uk.gov.hmrc" %% "domain"                        % "8.3.0-play-28",
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28"    % bootstrapVersion
  )

  val test: Seq[ModuleID]    = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatest"          %% "scalatest"                % "3.2.16",
    "org.scalatestplus.play" %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"      %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.jsoup"               % "jsoup"                    % "1.16.1",
    "com.typesafe.play"      %% "play-test"                % PlayVersion.current,
    "org.mockito"            %% "mockito-scala-scalatest"  % "1.17.14",
    "wolfendale"             %% "scalacheck-gen-regexp"    % "0.1.2",
    "org.scalacheck"         %% "scalacheck"               % "1.17.0",
    "com.github.tomakehurst"  % "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"    % "flexmark-all"             % "0.64.8",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"   % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}

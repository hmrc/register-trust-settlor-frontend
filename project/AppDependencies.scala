import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"   %% "play2-reactivemongo"            % "0.20.13-play28",
    "uk.gov.hmrc"         %% "play-frontend-hmrc"             % "1.1.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping"  % "1.9.0-play-28",
    "uk.gov.hmrc"         %% "domain"                         % "6.2.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"     % "5.9.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"              % "3.0.9",
    "org.scalatestplus.play"      %% "scalatestplus-play"     % "5.0.0",
    "org.pegdown"                 %  "pegdown"                % "1.6.0",
    "org.jsoup"                   %  "jsoup"                  % "1.12.1",
    "com.typesafe.play"           %% "play-test"              % PlayVersion.current,
    "org.mockito"                 %  "mockito-all"            % "1.10.19",
    "wolfendale"                  %% "scalacheck-gen-regexp"  % "0.1.2",
    "org.scalacheck"              %% "scalacheck"             % "1.14.0",
    "com.github.tomakehurst"      % "wiremock-standalone"     % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}

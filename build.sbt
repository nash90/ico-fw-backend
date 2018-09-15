name := """ico-fw-backend"""
organization := "com.ico-fw"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
	guice,
	javaJdbc,
	javaJpa,
	evolutions,
	"javax.json" % "javax.json-api" % "1.0",
	"org.hibernate" % "hibernate-core" % "5.2.12.Final",
	"com.h2database" % "h2" % "1.4.197",
	"mysql" % "mysql-connector-java" % "5.1.41",
	"com.squareup.okhttp" % "okhttp" % "2.7.0",
	
	//validation
	"org.hibernate.validator" % "hibernate-validator" % "6.0.2.Final",
	"org.hibernate.validator" % "hibernate-validator-annotation-processor" % "6.0.2.Final",
	"javax.validation" % "validation-api" % "2.0.0.Final",
	"javax.el" % "javax.el-api" % "3.0.0",
	"org.glassfish.web" % "javax.el" % "2.2.6",
	"org.mindrot" % "jbcrypt" % "0.3m",
	
	// project lombak 
	"org.projectlombok" % "lombok" % "1.16.20",

	// play email service plugin
	"com.typesafe.play" %% "play-mailer" % "6.0.1",
  	"com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
  	
  	//ethereum client
  	"org.web3j" % "core" % "3.4.0"
  
)

PlayKeys.externalizeResources := false
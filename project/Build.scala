import sbt._
import Keys._

object MinimalBuild extends Build {
  
  lazy val buildVersion =  "0.1"
  
  lazy val typesafeSnapshot = "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
  lazy val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  lazy val repo = if (buildVersion.endsWith("SNAPSHOT")) typesafeSnapshot else typesafe  
  lazy val sip14 = new java.io.File("src/main/sip14")
  lazy val scalaVer = Option(System.getProperty("scala.version"))
  lazy val sip14Lib = scalaVer.filter(_.startsWith("2.10")).map(x=>Seq()).getOrElse( Seq("com.typesafe" % "sip14_2.9.2" % "0.1"))
  lazy val root = Project(id = "sbench", base = file("."), settings = Project.defaultSettings).settings(
    version := buildVersion,
    publishTo <<= (version) { version: String =>
                val nexus = "http://repo.typesafe.com/typesafe/"
                if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "ivy-snapshots/") 
                else                                   Some("releases"  at nexus + "ivy-releases/")
    },
    //useful for 2.9.1 SIP14 unmanagedSourceDirectories in Compile ++= Seq(sip14),
    organization := "com.typesafe",
    //scalacOptions ++= Seq("-encoding", "UTF-8", "-Xlint","-deprecation", "-unchecked"),
    resolvers += "oss" at "https://oss.sonatype.org/content/repositories/snapshots",
    resolvers += repo,
    scalaVersion := scalaVer.getOrElse("2.9.2"),
    libraryDependencies ++= sip14Lib ++ Seq(
       ("com.google.guava"                 %    "guava"                    %   "10.0.1" notTransitive())
              .exclude("com.google.code.findbugs", "jsr305")
            ,
      "joda-time"                         %    "joda-time"                %   "2.1",
      "org.joda"                          %    "joda-convert"             %   "1.2",
      "org.javassist"                     %    "javassist"                %   "3.16.1-GA",
      "org.apache.commons"                %    "commons-lang3"            %   "3.1",
      "org.apache.ws.commons"             %    "ws-commons-util"          %   "1.0.1",
      "io.netty" %    "netty"  %   "3.5.0.Final",
      "net.sf.ehcache"                    %    "ehcache-core"             %   "2.5.0",
      "com.typesafe.config"               %    "config"                   %   "0.2.1",  
      "org.slf4j"                         %    "slf4j-api"                %   "1.6.4",
      "com.h2database"                    %    "h2"                       %   "1.3.158",
      "org.slf4j"                         %    "jul-to-slf4j"             %   "1.6.4",
      "org.slf4j"                         %    "jcl-over-slf4j"           %   "1.6.4",
       ("com.ning"                         %    "async-http-client"        %   "1.7.0" notTransitive())
              .exclude("org.jboss.netty", "netty")
            ,
       ("org.reflections"                  %    "reflections"              %   "0.9.7" notTransitive())
              .exclude("com.google.guava", "guava")
              .exclude("javassist", "javassist")
            ,
            
            "javax.servlet"                     %    "javax.servlet-api"        %   "3.0.1",
            "javax.transaction"                 %    "jta"                      %   "1.1",
            "tyrex"                             %    "tyrex"                    %   "1.0.1",      
      "oauth.signpost"                    %    "signpost-core"            %   "1.2.1.1",
      "oauth.signpost"                    %    "signpost-commonshttp4"    %   "1.2.1.1",
      ("com.jolbox"                       %    "bonecp"                   %   "0.7.1.RELEASE" notTransitive())
              .exclude("com.google.guava", "guava")
              .exclude("org.slf4j", "slf4j-api"),
      "ch.qos.logback"                    %    "logback-core"             %   "1.0.3",
      "ch.qos.logback"                    %    "logback-classic"          %   "1.0.3")
  )
}

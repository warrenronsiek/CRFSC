name := "CRFSC"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.poi" % "poi" % "4.0.1"
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "0.9.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.0"

libraryDependencies  ++= Seq(
  "org.scalanlp" %% "breeze" % "0.13.2",
  "org.scalanlp" %% "breeze-natives" % "0.13.2",
  "org.scalanlp" %% "breeze-viz" % "0.13.2",
  "com.databricks" % "spark-csv_2.11" % "1.5.0"
)
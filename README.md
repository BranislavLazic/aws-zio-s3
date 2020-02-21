# aws-zio-s3 #

Welcome to aws-zio-s3!

ZIO based client for AWS S3.

### How to use

```scala
package com.github.branislavlazic.aws.zio.s3
import zio._
import zio.console._
import java.nio.file.Paths
import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.regions.Region
import scala.collection.JavaConverters._

object Main extends App {

  private val s3 = new S3(
    Region.EU_WEST_1,
    StaticCredentialsProvider.create(
      AwsBasicCredentials.create("api-key", "secret-key")
    )
  )

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = (
    for {
      // Create the bucket 
      _    <- s3.createBucket("s3-bucket-name")
      // Upload the file
      _    <- s3.putObject("s3-bucket-name", Paths.get("/tmp/file.txt").getFileName.toString, Paths.get("/tmp/file.txt"))
      // Delete the file
      _    <- s3.deleteObject("s3-bucket-name", "file.txt")
      // Delete the bucket
      _    <- s3.deleteBucket("s3-bucket-name")
      // List all buckets
      resp <- s3.listBuckets().map(_.buckets().asScala)
      _    <- ZIO.foreach(resp)(b => putStrLn(b.name()))
    } yield 0).foldM(e => UIO(println(e.toString)).as(1), IO.succeed)
}
```

Add SBT dependency:

`libraryDependencies += "com.github.branislavlazic" %% "aws-zio-s3" % "0.3.0"`

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.

## License ##

This code is open source software licensed under the
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0) license.

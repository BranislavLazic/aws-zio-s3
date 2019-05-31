# aws-zio-s3 #

Welcome to aws-zio-s3!

ZIO based client for AWS S3.

### How to use

```scala
package com.github.branislavlazic.aws.zio.s3
import scalaz.zio._
import java.nio.file.Paths
import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

object Main extends App {
  override def run(args: List[String]): ZIO[Main.Environment, Nothing, Int] = (
    for {
      client <- Task {
        S3AsyncClient.builder()
          .region(Region.EU_WEST_1)
          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("api-key", "secret-key")))
          .build()

      }
      // Create the bucket
      _ <- S3.createBucket(client, "s3-bucket-name")
      // Upload the file
      _ <- S3.putObject(client, "s3-bucket-name", Paths.get("/tmp/file.txt").getFileName.toString, Paths.get("/tmp/file.txt"))
      // Delete the bucket
      _ <- S3.deleteBucket(client, "s3-bucket-name")
    } yield 0).foldM(e => UIO(println(e.toString)).const(1), IO.succeed)
}

```

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

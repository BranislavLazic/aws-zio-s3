/*
 * Copyright 2019 Branislav Lazic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.branislavlazic.aws.zio.s3

import java.nio.file.Path
import java.util.concurrent.CompletableFuture

import zio.{ IO, Task, ZIO }
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{
  CreateBucketRequest,
  CreateBucketResponse,
  DeleteBucketRequest,
  DeleteBucketResponse,
  DeleteObjectRequest,
  DeleteObjectResponse,
  ListBucketsResponse,
  PutObjectRequest,
  PutObjectResponse
}

class S3(region: Region, awsCredentialsProvider: AwsCredentialsProvider) {

  val s3AsyncClient: Task[S3AsyncClient] =
    Task {
      S3AsyncClient
        .builder()
        .region(region)
        .credentialsProvider(awsCredentialsProvider)
        .build()
    }

  /**
   * Create S3 bucket with the given name.
   * @param name - the name of the bucket
   */
  def createBucket(name: String): Task[CreateBucketResponse] =
    handleS3EffectAsync(_.createBucket(CreateBucketRequest.builder().bucket(name).build()))

  /**
   * Delete the bucket with the given name.
   *
   * @param name - the name of the bucket
   */
  def deleteBucket(name: String): Task[DeleteBucketResponse] =
    handleS3EffectAsync(_.deleteBucket(DeleteBucketRequest.builder().bucket(name).build()))

  /**
   * Upload an object with a given key on S3 bucket.
   *
   * @param bucketName - the name of the bucket
   * @param key        - object key
   * @param filePath   - file path
   */
  def putObject(
    bucketName: String,
    key: String,
    filePath: Path
  ): Task[PutObjectResponse] =
    handleS3EffectAsync(_.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(), filePath))

  /**
   * Delete an object with a given key on S3 bucket.
   *
   * @param bucketName - the name of the bucket
   * @param key        - object key
   */
  def deleteObject(
    bucketName: String,
    key: String
  ): Task[DeleteObjectResponse] =
    handleS3EffectAsync(_.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build()))

  /**
   * Obtain a list of all buckets owned by the authenticated sender.
   */
  def listBuckets(): Task[ListBucketsResponse] = handleS3EffectAsync(_.listBuckets())

  private def handleS3EffectAsync[T](toS3Response: S3AsyncClient => CompletableFuture[T]): ZIO[Any, Throwable, T] =
    s3AsyncClient.flatMap { client =>
      IO.effectAsync[Throwable, T] { callback =>
        toS3Response(client).handle[Unit]((response, err) => {
          err match {
            case null => callback(IO.succeed(response))
            case ex   => callback(IO.fail(ex))
          }
        })
      }
    }
}

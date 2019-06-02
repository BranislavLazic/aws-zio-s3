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

import scalaz.zio.{ IO, Task, ZIO }
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

object S3 {

  /**
    * Create an async S3 client.
    *
    * @param region                 - The AWS region
    * @param awsCredentialsProvider - credentials loader
    */
  def createClient(region: Region,
                   awsCredentialsProvider: AwsCredentialsProvider): Task[S3AsyncClient] =
    Task {
      S3AsyncClient
        .builder()
        .region(region)
        .credentialsProvider(awsCredentialsProvider)
        .build()
    }

  /**
    * Create S3 bucket with the given name.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param name          - the name of the bucket
    */
  def createBucket(s3AsyncClient: S3AsyncClient, name: String): Task[CreateBucketResponse] =
    IO.effectAsync[Any, Throwable, CreateBucketResponse] { callback =>
      handleResponse(s3AsyncClient
                       .createBucket(CreateBucketRequest.builder().bucket(name).build()),
                     callback)
    }

  /**
    * Delete the bucket with the given name.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param name          - the name of the bucket
    */
  def deleteBucket(s3AsyncClient: S3AsyncClient, name: String): Task[DeleteBucketResponse] =
    IO.effectAsync[Any, Throwable, DeleteBucketResponse] { callback =>
      handleResponse(s3AsyncClient
                       .deleteBucket(DeleteBucketRequest.builder().bucket(name).build()),
                     callback)
    }

  /**
    * Upload an object with a given key on S3 bucket.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param bucketName    - the name of the bucket
    * @param key           - object key
    * @param filePath      - file path
    */
  def putObject(s3AsyncClient: S3AsyncClient,
                bucketName: String,
                key: String,
                filePath: Path): Task[PutObjectResponse] =
    IO.effectAsync[Any, Throwable, PutObjectResponse] { callback =>
      handleResponse(
        s3AsyncClient
          .putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(), filePath),
        callback
      )
    }

  /**
    * Delete an object with a given key on S3 bucket.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param bucketName    - the name of the bucket
    * @param key           - object key
    */
  def deleteObject(s3AsyncClient: S3AsyncClient,
                   bucketName: String,
                   key: String): Task[DeleteObjectResponse] =
    IO.effectAsync[Any, Throwable, DeleteObjectResponse] { callback =>
      handleResponse(
        s3AsyncClient.deleteObject(
          DeleteObjectRequest.builder().bucket(bucketName).key(key).build()
        ),
        callback
      )
    }

  /**
    * Obtain a list of all buckets owned by the authenticated sender.
    *
    * @param s3AsyncClient - the client for async access to S3
    */
  def listBuckets(s3AsyncClient: S3AsyncClient): Task[ListBucketsResponse] =
    IO.effectAsync[Any, Throwable, ListBucketsResponse] { callback =>
      handleResponse(s3AsyncClient.listBuckets(), callback)
    }

  private def handleResponse[T](completableFuture: CompletableFuture[T],
                                callback: ZIO[Any, Throwable, T] => Unit) =
    completableFuture.handle[Unit]((response, err) => {
      err match {
        case null => callback(IO.succeed(response))
        case ex   => callback(IO.fail(ex))
      }
    })
}

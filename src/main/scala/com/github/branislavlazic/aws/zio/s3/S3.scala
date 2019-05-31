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

import scalaz.zio.{ IO, Task }
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{
  CreateBucketRequest,
  CreateBucketResponse,
  DeleteBucketRequest,
  DeleteBucketResponse,
  PutObjectRequest,
  PutObjectResponse
}

object S3 {

  /**
    * Create S3 bucket with the given name.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param name - the name of the bucket
    */
  def createBucket(s3AsyncClient: S3AsyncClient, name: String): Task[CreateBucketResponse] =
    IO.effectAsync[Throwable, CreateBucketResponse] { callback =>
      s3AsyncClient
        .createBucket(CreateBucketRequest.builder().bucket(name).build())
        .handle[CreateBucketResponse]((response, err) => {
          err match {
            case null => callback(IO.apply(response))
            case ex   => callback(IO.fail(ex))
          }
          response
        })
    }

  /**
    * Delete the bucket with the given name.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param name - the name of the bucket
    */
  def deleteBucket(s3AsyncClient: S3AsyncClient, name: String): Task[DeleteBucketResponse] =
    IO.effectAsync[Throwable, DeleteBucketResponse] { callback =>
      s3AsyncClient
        .deleteBucket(DeleteBucketRequest.builder().bucket(name).build())
        .handle[DeleteBucketResponse]((response, err) => {
          err match {
            case null => callback(IO.apply(response))
            case ex   => callback(IO.fail(ex))
          }
          response
        })
    }

  /**
    * Upload an object with a given key on S3 bucket.
    *
    * @param s3AsyncClient - the client for async access to S3
    * @param bucketName - the name of the bucket
    * @param keyName - object key
    * @param filePath - file path
    */
  def putObject(s3AsyncClient: S3AsyncClient,
                bucketName: String,
                keyName: String,
                filePath: Path): Task[PutObjectResponse] =
    IO.effectAsync[Throwable, PutObjectResponse] { callback =>
      s3AsyncClient
        .putObject(PutObjectRequest.builder().bucket(bucketName).key(keyName).build(), filePath)
        .handle[PutObjectResponse]((response, err) => {
          err match {
            case null => callback(IO.apply(response))
            case ex   => callback(IO.fail(ex))
          }
          response
        })
    }
}

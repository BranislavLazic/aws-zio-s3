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

import scalaz.zio.{ IO, Task }
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{ CreateBucketRequest, CreateBucketResponse }

object S3 {

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
}

package io.findify.akka.persistence.s3

import java.io.InputStream

import com.amazonaws.auth.{BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.{AmazonS3Client, S3ClientOptions}

import scala.concurrent.{ExecutionContext, Future}

trait S3Client {
  val s3ClientConfig: S3ClientConfig

  lazy val client: AmazonS3Client = {
    val client =
      if (s3ClientConfig.awsUseDefaultCredentialsProviderChain)
        new AmazonS3Client(new DefaultAWSCredentialsProviderChain).withRegion(s3ClientConfig.region)
      else
        new AmazonS3Client(new BasicAWSCredentials(s3ClientConfig.awsKey, s3ClientConfig.awsSecret))

    s3ClientConfig.endpoint.foreach { endpoint =>
      client.withEndpoint(endpoint)
      ()
    }
    client.setS3ClientOptions(new S3ClientOptions()
      .withPathStyleAccess(s3ClientConfig.options.pathStyleAccess)
      .withChunkedEncodingDisabled(s3ClientConfig.options.chunkedEncodingDisabled))
    client
  }

  def createBucket(bucketName: String)(implicit ec: ExecutionContext): Future[Bucket] = Future {
    client.createBucket(bucketName)
  }

  def deleteBucket(bucketName: String)(implicit ec: ExecutionContext): Future[Unit] = Future {
    client.deleteBucket(bucketName)
  }

  def putObject(bucketName: String, key: String, input: InputStream, metadata: ObjectMetadata)(implicit ec: ExecutionContext): Future[PutObjectResult] = Future {
    client.putObject(new PutObjectRequest(bucketName, key, input, metadata))
  }

  def getObject(bucketName: String, key: String)(implicit ec: ExecutionContext): Future[S3Object] = Future {
    val res = client.getObject(new GetObjectRequest(bucketName, key))
    println(res.toString)
    val br=1
    res
  }

  def listObjects(request: ListObjectsRequest)(implicit ec: ExecutionContext): Future[ObjectListing] = Future {
    val list = client.listObjects(request)
    list
  }

  def deleteObject(bucketName: String, key: String)(implicit ec: ExecutionContext): Future[Unit] = Future {
    client.deleteObject(bucketName, key)
  }

  def deleteObjects(request: DeleteObjectsRequest)(implicit ec: ExecutionContext): Future[Unit] = Future {
    client.deleteObjects(request)
  }
}

output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "ec2_public_ip" {
  description = "EC2 인스턴스 공개 IP"
  value       = module.ec2.public_ip
}

output "ec2_private_ip" {
  description = "EC2 인스턴스 프라이빗 IP"
  value       = module.ec2.private_ip
}

output "rds_endpoint" {
  description = "RDS 엔드포인트"
  value       = module.rds.endpoint
}

output "cloudfront_domain_name" {
  description = "CloudFront 배포 도메인 이름"
  value       = module.cloudfront_s3.cloudfront_domain_name
}

output "s3_bucket_name" {
  description = "S3 버킷 이름"
  value       = module.cloudfront_s3.s3_bucket_name
}

output "cloudfront_cert_arn" {
  description = "CloudFront ACM 인증서 ARN"
  value       = module.acm.cloudfront_cert_arn
}

output "ec2_cert_arn" {
  description = "EC2 ACM 인증서 ARN"
  value       = module.acm.ec2_cert_arn
} 
locals {
  project     = var.project  # var.project 로 변경 필요
  domain_name = var.domain_name

  ec2_domain        = "api.${local.project}.${local.domain_name}"
  cloudfront_domain = "static.${local.project}.${local.domain_name}"
  s3_bucket_name    = "${local.project}-cdn-bucket-${local.domain_name}"

  hosted_zone_name = "${local.project}.${local.domain_name}."
}
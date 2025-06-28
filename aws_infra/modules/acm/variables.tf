variable "cloudfront_domain" {
  description = "CloudFront 도메인"
  type        = string
}

variable "ec2_domain" {
  description = "EC2 도메인"
  type        = string
}

variable "region" {
  description = "AWS 리전"
  type        = string
}

variable "zone_id" {
  description = "Route53 호스팅 존 ID"
  type        = string
}
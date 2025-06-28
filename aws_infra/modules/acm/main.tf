# 이 provider 설정은 루트 모듈에서 alias로 넘겨야 합니다.
# 예:
# module "acm" {
#   source = "./modules/acm"
#   providers = {
#     aws.us_east_1  = aws.us_east_1
#     aws.ec2_region = aws.ap_northeast_2
#   }
# }

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
      configuration_aliases = [aws.us_east_1, aws.ec2_region]
    }
  }
}

# Route 53 Zone lookup (zone_id로)
data "aws_route53_zone" "main" {
  zone_id = var.zone_id
}

# CloudFront 인증서 (us-east-1)
resource "aws_acm_certificate" "cloudfront_cert" {
  provider          = aws.us_east_1
  domain_name       = var.cloudfront_domain
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

# EC2 인증서 (EC2 리전)
resource "aws_acm_certificate" "ec2_cert" {
  provider          = aws.ec2_region
  domain_name       = var.ec2_domain
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

locals {
  cloudfront_domain_validation = tolist(aws_acm_certificate.cloudfront_cert.domain_validation_options)[0]
  ec2_domain_validation        = tolist(aws_acm_certificate.ec2_cert.domain_validation_options)[0]
}

# DNS Validation Record - CloudFront
resource "aws_route53_record" "cloudfront_cert_validation" {
  provider = aws.us_east_1
  zone_id  = data.aws_route53_zone.main.zone_id
  name     = local.cloudfront_domain_validation.resource_record_name
  type     = local.cloudfront_domain_validation.resource_record_type
  ttl      = 60
  records  = [local.cloudfront_domain_validation.resource_record_value]
}

# DNS Validation Record - EC2
resource "aws_route53_record" "ec2_cert_validation" {
  provider = aws.ec2_region
  zone_id  = data.aws_route53_zone.main.zone_id
  name     = local.ec2_domain_validation.resource_record_name
  type     = local.ec2_domain_validation.resource_record_type
  ttl      = 60
  records  = [local.ec2_domain_validation.resource_record_value]
}

# 인증서 검증 완료 리소스
resource "aws_acm_certificate_validation" "cloudfront_cert_validation" {
  provider                = aws.us_east_1
  certificate_arn         = aws_acm_certificate.cloudfront_cert.arn
  validation_record_fqdns = [aws_route53_record.cloudfront_cert_validation.fqdn]
}

resource "aws_acm_certificate_validation" "ec2_cert_validation" {
  provider                = aws.ec2_region
  certificate_arn         = aws_acm_certificate.ec2_cert.arn
  validation_record_fqdns = [aws_route53_record.ec2_cert_validation.fqdn]
}
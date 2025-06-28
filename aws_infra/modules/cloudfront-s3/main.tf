resource "aws_cloudfront_distribution" "cdn" {
  origin {
    domain_name = aws_s3_bucket.cdn.bucket_regional_domain_name
    origin_id   = "s3-origin"
    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id
  }

  enabled = true

  aliases = [var.cloudfront_domain]  # 사용자 정의 도메인 사용 (예: static.sns.haechan.site)

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "s3-origin"

    viewer_protocol_policy = "redirect-to-https"

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }
  }

  # Signed Cookies 캐시 동작 예시
  ordered_cache_behavior {
    path_pattern     = "/private/*"
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "s3-origin"

    viewer_protocol_policy = "redirect-to-https"

    trusted_key_groups = [aws_cloudfront_key_group.signed_cookie_key_group.id]

    forwarded_values {
      query_string = true
      cookies {
        forward = "all"
      }
      headers = ["Origin", "Access-Control-Request-Headers", "Access-Control-Request-Method"]
    }

    min_ttl     = 0
    default_ttl = 3600
    max_ttl     = 86400
  }

  viewer_certificate {
    acm_certificate_arn      = var.cloudfront_cert_arn  # 변수로 받아온 ACM 인증서
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  tags = {
    Name = "${var.prefix}-cdn"
  }
}
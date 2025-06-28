resource "aws_route53_record" "api" {
  zone_id = var.zone_id
  name    = var.api_domain
  type    = "A"
  ttl     = 300
  records = [var.api_target]
}

resource "aws_route53_record" "cdn" {
  zone_id = var.zone_id
  name    = var.cdn_domain
  type    = "A"
  alias {
    name                   = var.cdn_target
    zone_id                = var.cdn_target_zone_id
    evaluate_target_health = false
  }
} 
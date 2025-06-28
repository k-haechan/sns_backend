output "cloudfront_cert_arn" {
  value       = aws_acm_certificate.cloudfront_cert.arn
  description = "CloudFront 인증서 ARN"
}

output "ec2_cert_arn" {
  value       = aws_acm_certificate.ec2_cert.arn
  description = "EC2 인증서 ARN"
}
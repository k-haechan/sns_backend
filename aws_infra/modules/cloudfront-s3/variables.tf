variable "prefix" {
  description = "Resource name prefix"
  type        = string
}

variable "cloudfront_domain" {
  description = "CloudFront distribution domain name"
  type        = string
}

variable "cloudfront_cert_arn" {
  description = "ACM certificate ARN for CloudFront"
  type        = string
}

variable "bucket_name" {
  description = "S3 bucket name for CDN"
  type        = string
}

variable "public_key" {
  description = "Public key for signed cookies"
  type        = string
  default     = ""
}
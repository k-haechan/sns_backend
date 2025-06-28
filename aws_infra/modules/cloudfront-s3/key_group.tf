resource "aws_cloudfront_origin_access_control" "oac" {
  name                              = "${var.prefix}-oac"
  description                       = "Origin Access Control for S3"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_public_key" "signed_cookie_key" {
  comment     = "Public key for signed cookies"
  encoded_key = var.public_key
  name        = "${var.prefix}-signed-cookie-key"
}

resource "aws_cloudfront_key_group" "signed_cookie_key_group" {
  comment = "Key group for signed cookies"
  items   = [aws_cloudfront_public_key.signed_cookie_key.id]
  name    = "${var.prefix}-signed-cookie-key-group"
} 
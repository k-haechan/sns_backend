variable "domain_name" {
  description = "호스팅 영역으로 생성할 도메인 이름"
  type        = string
}

variable "api_domain" {
  description = "API 서버 도메인 이름"
  type        = string
  default     = ""
}

variable "api_target" {
  description = "API 서버 타겟 (ELB 또는 EC2)"
  type        = string
  default     = ""
}

variable "api_target_zone_id" {
  description = "API 서버 타겟의 zone ID"
  type        = string
  default     = ""
}

variable "cdn_domain" {
  description = "CDN 도메인 이름"
  type        = string
  default     = ""
}

variable "cdn_target" {
  description = "CDN 타겟 (CloudFront)"
  type        = string
  default     = ""
}

variable "cdn_target_zone_id" {
  description = "CDN 타겟의 zone ID"
  type        = string
  default     = ""
}

variable "zone_id" {
  description = "Route53 호스팅 존 ID"
  type        = string
}
variable "region" {
  description = "AWS 리전"
  type        = string
}

variable "project" {
  description = "리소스 이름 접두사"
  type        = string
}

variable "domain_name" {
  description = "메인 도메인 이름"
  type        = string
}


variable "ec2_ami" {
  description = "EC2 인스턴스 AMI ID"
  type        = string
}

variable "ec2_instance_type" {
  description = "EC2 인스턴스 타입"
  type        = string
}

variable "rds_instance_class" {
  description = "RDS 인스턴스 클래스"
  type        = string
}

variable "db_name" {
  description = "데이터베이스 이름"
  type        = string
}

variable "db_username" {
  description = "데이터베이스 사용자명"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "데이터베이스 비밀번호"
  type        = string
  sensitive   = true
}

variable "public_key" {
  description = "CloudFront 서명된 쿠키용 공개키"
  type        = string
  sensitive   = true
}
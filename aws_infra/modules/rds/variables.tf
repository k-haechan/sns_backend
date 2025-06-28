variable "prefix" {
  description = "리소스 이름 접두사"
  type        = string
}

variable "private_subnet_ids" {
  description = "DB 인스턴스를 배치할 private 서브넷 ID 목록"
  type        = list(string)
}

variable "db_sg_id" {
  description = "DB 인스턴스에 적용할 보안 그룹 ID"
  type        = string
}

variable "instance_class" {
  description = "DB 인스턴스 타입"
  type        = string
  default     = "db.t4g.micro"
}

variable "db_name" {
  description = "DB 이름"
  type        = string
  default     = "sns"
}

variable "db_username" {
  description = "DB 사용자 이름"
  type        = string
}

variable "db_password" {
  description = "DB 비밀번호"
  type        = string
  sensitive   = true
}
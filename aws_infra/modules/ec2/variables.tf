variable "prefix" {
  description = "리소스 이름 접두사"
  type        = string
}

variable "ami" {
  description = "EC2 AMI ID"
  type        = string
}

variable "instance_type" {
  description = "EC2 인스턴스 타입"
  type        = string
  default     = "t3.micro"
}

variable "subnet_id" {
  description = "EC2 인스턴스를 배치할 서브넷 ID"
  type        = string
}

variable "security_group_ids" {
  description = "EC2 인스턴스에 적용할 보안 그룹 목록"
  type        = list(string)
}
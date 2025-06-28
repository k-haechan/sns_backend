output "endpoint" {
  description = "RDS 인스턴스 엔드포인트"
  value       = aws_db_instance.this.endpoint
}

output "db_instance_id" {
  description = "RDS 인스턴스 ID"
  value       = aws_db_instance.this.id
}

output "db_name" {
  description = "데이터베이스 이름"
  value       = aws_db_instance.this.db_name
}
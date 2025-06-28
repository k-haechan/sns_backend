output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = [aws_subnet.public_1.id, aws_subnet.public_2.id]
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value       = [aws_subnet.private_1.id, aws_subnet.private_2.id]
}

output "igw_id" {
  value = aws_internet_gateway.igw.id
}

output "backend_security_group_id" {
  description = "Backend security group ID"
  value       = aws_security_group.backend.id
}

output "database_security_group_id" {
  description = "Database security group ID"
  value       = aws_security_group.database.id
}
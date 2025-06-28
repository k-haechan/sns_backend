resource "aws_db_subnet_group" "this" {
  name       = "${var.prefix}-db-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = {
    Name = "${var.prefix}-db-subnet-group"
  }
}

resource "aws_db_instance" "this" {
  identifier              = "${var.prefix}-db"
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = var.instance_class
  db_name                 = var.db_name
  username                = var.db_username
  password                = var.db_password
  allocated_storage       = 20
  storage_type            = "gp2"
  db_subnet_group_name    = aws_db_subnet_group.this.name
  vpc_security_group_ids  = [var.db_sg_id]
  multi_az                = false
  skip_final_snapshot     = true
  publicly_accessible     = false

  tags = {
    Name = "${var.prefix}-rds"
  }
}
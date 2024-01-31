package com.wissensalt.springjasper.repository;

import com.wissensalt.springjasper.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}

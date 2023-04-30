package example.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;

@Data
@DynamoDBTable(tableName = "EmployeeTbl")
public class Employee {
	@DynamoDBHashKey(attributeName = "id")
	private String id;
	private String firstName;
	private String lastName;
	// private Contact contact;
	private String emailAddress;
    private String mobileNumber;
	private String fullName;
	private Integer salary;
	private String doj;
	private String source;
}
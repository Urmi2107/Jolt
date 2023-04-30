

package example;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.dynamodbv2.document.Item;

import example.entity.Employee;

import example.util.JoltUtil;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
  
 public class LambdaRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static String tableName = System.getenv("STORAGE_EMPLOYEETBL_NAME");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        Map<String , String>headers=new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        headers.put("Access-Control-Allow-Origin", "*"); 
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET,DELETE,PUT");
        
        Gson gson=new Gson();

        Employee bambooEmployee = getEmployee("https://bamboohr.mocklab.io/emp/1", "/bambooSpec.json");
        Employee capterraEmployee = getEmployee("https://capterra.mocklab.io/employee/1", "/capterraSpec.json");        
        
        // DynamoDBMapper mapper = new DynamoDBMapper(client);
        List<Employee> employees = new ArrayList<>();
        employees.add(bambooEmployee);
        employees.add(capterraEmployee);
       // mapper.batchSave(employees);

        saveData(bambooEmployee);
        saveData(capterraEmployee);
      
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withBody(gson.toJson(employees)).withStatusCode(200);
		return response.withHeaders(headers);
    }

    private void saveData(Employee emp) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(tableName);
        Item item = new Item()
                .withPrimaryKey("id", emp.getId())
                .withString("firstName", emp.getFirstName())
                .withString("lastName", emp.getLastName())
                .withString("emailAddress", emp.getEmailAddress())
                .withString("mobileNumber", emp.getMobileNumber())
                .withString("fullName", emp.getFullName())
                .withInt("salary", emp.getSalary())
                .withString("doj", emp.getDoj())
                .withString("source", emp.getSource());
        table.putItem(item);
    }

    private Employee getEmployee(String url, String specJson) {
        Map<String, Object> result = new JoltUtil().invokeRest(url);
        System.out.println("result" + result);
        Employee employee = new JoltUtil().getEntity(specJson, Employee.class, result);
        return employee;
    }
}
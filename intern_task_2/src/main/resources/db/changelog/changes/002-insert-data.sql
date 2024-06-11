
INSERT INTO companies (id, company_name, country, foundation_date)
VALUES
    (nextval('company_seq'), 'ABC Corporation', 'USA', '2000-01-01'),
    (nextval('company_seq'), 'XYZ Ltd', 'UK', '1995-05-12'),
    (nextval('company_seq'), '123 Inc', 'Canada', '2010-10-20');

INSERT INTO employees (id, employee_name, employee_surname, salary, hiring_date, job, company_id)
VALUES
    (nextval('employee_seq'), 'John', 'Doe', 50000, '2010-01-01', 'FRONTEND_DEVELOPER', currval('company_seq')),
    (nextval('employee_seq'), 'Alice', 'Smith', 60000, '2012-03-15', 'BACKEND_DEVELOPER', currval('company_seq')),
    (nextval('employee_seq'), 'Bob', 'Johnson', 70000, '2015-06-30', 'SALES', currval('company_seq')),
    (nextval('employee_seq'), 'Emily', 'Williams', 55000, '2017-09-20', 'HIRING_MANAGER', currval('company_seq')),
    (nextval('employee_seq'), 'Michael', 'Brown', 65000, '2018-11-10', 'RECRUITER', currval('company_seq')),
    (nextval('employee_seq'), 'Emma', 'Jones', 75000, '2019-12-25', 'PROJECT_MANAGER', currval('company_seq')),
    (nextval('employee_seq'), 'David', 'Miller', 80000, '2020-02-28', 'SQL_DEVELOPER', currval('company_seq')),
    (nextval('employee_seq'), 'Olivia', 'Davis', 70000, '2021-04-15', 'DEVOPS', currval('company_seq'));


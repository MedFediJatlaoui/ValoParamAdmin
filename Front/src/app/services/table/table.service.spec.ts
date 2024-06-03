import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TableService } from './table.service';
import {TableInfo} from "../../model/table-info";
import {TablesWithColumns} from "../../model/tables-with-columns";

describe('TableService', () => {
  let service: TableService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TableService]
    });

    service = TestBed.inject(TableService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to delete a record', () => {
    const tableName = 'example_table';
    const primaryKeyValue = 'example_primary_key';
    const expectedResponse = { success: true };

    service.deleteRecord(tableName, primaryKeyValue).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne(`${service.baseUrl}/${tableName}/delete/${primaryKeyValue}`);
    expect(req.request.method).toEqual('GET');

    req.flush(expectedResponse);
  });

  it('should send a POST request to cancel deletion', () => {
    const tableName = 'Mancity';
    const primaryKeyValue = 'Silva';
    const expectedResponse = { success: true };

    service.cancelDeletion(tableName, primaryKeyValue).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne(`${service.baseUrl}/${tableName}/canceldeletion/${primaryKeyValue}`);
    expect(req.request.method).toEqual('POST');

    req.flush(expectedResponse);
  });
  it('should retrieve All Tables And Columns ', async () => {
  const limit=5
  const offset=0
    const tableWithColumns ={
      numberTables: 0,
      allTablesWithColumns: []
    }
    service.retrieveAllTablesAndColumns(limit, offset).subscribe(response => {
       expect(response).toEqual(tableWithColumns);
    });
    const req = httpTestingController.expectOne(`${service.baseUrl}/${limit}/${offset}`);
    expect(req.request.method).toEqual('GET');

    req.flush(tableWithColumns);
  });
  it('should retrieve all tables and columns', () => {
    const limit = 5;
    const offset = 0;
    const expectedResponse: TablesWithColumns = {
      numberTables: 0,
      allTablesWithColumns: []
    };

    service.retrieveAllTablesAndColumns(limit, offset).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne(`${service.baseUrl}/${limit}/${offset}`);
    expect(req.request.method).toEqual('GET');

    req.flush(expectedResponse);
  });

  it('should get data from a table', () => {
    let table = new TableInfo();
    table.name='example_table',
      table.selectedColumns=['column1', 'column2'],
      table.sortByColumn='column1',
      table.sortOrder='asc',
      table.limit=10,
      table.offset=0,
      table.search='example_search'

    const expectedResponse = [{}, {}]; // Replace with your expected response

    service.getDataFromTable(table).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const columnsVar = table.selectedColumns.join(',');
    const url = `${service.baseUrl}/${table.name}?columns=${columnsVar}&sortByColumn=${table.sortByColumn}&sortOrder=${table.sortOrder}&limit=${table.limit}&offset=${table.offset}&search=${table.search}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('GET');

    req.flush(expectedResponse);
  });
  it('should add an instance', () => {
    const tableName = 'example_table';
    const instanceData = {  id: '5',
      firstname: 'Rivaldo',
      lastname: 'DobrasilVoyVoy' };
    const expectedResponse = 'success'; // Replace with your expected response

    service.addInstance(instanceData, tableName).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const url = `${service.baseUrl}/${tableName}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(instanceData);
    expect(req.request.responseType).toEqual('text');

    req.flush(expectedResponse);
  });
  it('should update an instance', () => {
    const tableName = 'example_table';
    const instanceData = {  id: '5',
      firstname: 'Rivaldo',
      lastname: 'DobrasilVoyVoy' };
    const expectedResponse = 'success'; // Replace with your expected response

    service.updateInstance(instanceData, tableName).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const url = `${service.baseUrl}/update/${tableName}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('PUT');
    expect(req.request.body).toEqual(instanceData);
    expect(req.request.responseType).toEqual('text');

    req.flush(expectedResponse);
  });

  it('should cancel updating an instance', () => {
    const tableName = 'example_table';
    const primaryKeyValue = 'example_primary_key';
    const expectedResponse = 'success';
    service.cancelUpdateInstance(tableName, primaryKeyValue).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const url = `${service.baseUrl}/cancelupdate/${tableName}/${primaryKeyValue}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual({});
    expect(req.request.responseType).toEqual('json');
    req.flush(expectedResponse);
  });

  it('should fetch param history', () => {
    const tableName = 'example_table';
    const expectedHistory = [  {id: 0,
    tableName:"",
    action:"",
    version:0,
    rowId:"",
    oldRow:"",
    newRow:"",
    createdBy:"",
    createdAt:"",
    lastModifiedBy:"",
    lastModifiedAt:""}
  ];

    service.paramHistory(tableName).subscribe(history => {
      expect(history).toEqual(expectedHistory);
    });

    const url = `${service.baseUrl}/${tableName}/history`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('GET');
    expect(req.request.responseType).toEqual('json');

    req.flush(expectedHistory);
  });
  it('should delete cascade an instance', () => {
    const tableName = 'example_table';
    const primaryKeyValue = 'example_primary_key';
    const expectedResponse = 'success';
    service.deleteCascade(tableName, primaryKeyValue).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const url = `${service.baseUrl}/${tableName}/cascade/${primaryKeyValue}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual({});
    expect(req.request.responseType).toEqual('json');
    req.flush(expectedResponse);
  });
it('should have foreignKey options for input dropdown',()=>{
  const tableName = "test_table";
  const expectedResponse =[{column:"1",options:["1","2"]}];
  service.fkoptions(tableName).subscribe(response=>
  {
    expect(response).toEqual(expectedResponse)
  })
  const url = `${service.baseUrl}/${tableName}/fkoptions`;
  const req = httpTestingController.expectOne(url);
  expect(req.request.method).toEqual('GET');
  expect(req.request.body).toEqual(null);
  expect(req.request.responseType).toEqual('json');
  req.flush(expectedResponse);
});
  it('should Check References',()=>{
    const tableName = "test_table";
    const primaryKeyValue = 'example_primary_key';
    const expectedResponse =[{column:"1",options:["1","2"]}];
    service.checkreferences(tableName,primaryKeyValue).subscribe(response=>
    {
      expect(response).toEqual(expectedResponse)
    })
    const url = `${service.baseUrl}/${tableName}/references/${primaryKeyValue}`;
    const req = httpTestingController.expectOne(url);
    expect(req.request.method).toEqual('GET');
    expect(req.request.body).toEqual(null);
    expect(req.request.responseType).toEqual('json');
    req.flush(expectedResponse);
  });
});

